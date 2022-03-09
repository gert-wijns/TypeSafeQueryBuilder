/*
 * Copyright Gert Wijns gert.wijns@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.shad.tsqb.selection.group;

import static be.shad.tsqb.selection.group.SelectionTreeFieldSetter.SelectionTreeFieldSetterType.COLLECTION;
import static be.shad.tsqb.selection.group.SelectionTreeFieldSetter.SelectionTreeFieldSetterType.IDENTITY;
import static be.shad.tsqb.selection.group.SelectionTreeFieldSetter.SelectionTreeFieldSetterType.SINGLE;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyPropertyData;
import be.shad.tsqb.result.ResultGroupProjection;
import be.shad.tsqb.result.ResultMergeProjection;
import be.shad.tsqb.result.ResultSubProjection;
import be.shad.tsqb.selection.SelectionIdentityTree;
import be.shad.tsqb.selection.SelectionTree;
import be.shad.tsqb.selection.SelectionTreeData;
import be.shad.tsqb.selection.SelectionTreeResult;
import be.shad.tsqb.selection.SelectionTreeValue;
import be.shad.tsqb.selection.group.SelectionTreeFieldSetter.SelectionTreeFieldSetterType;
import be.shad.tsqb.selection.parallel.SelectionMerger;

/**
 * The root of a selection tree which will select into a dto.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SelectionTreeGroup extends SelectionTree {
    private static final SelectionTreeResult NULL_RESULT = new SelectionTreeResult(null);
    private final ResultGroupProjection group;
    private final SelectionTreeFieldSetter[] identityFields;
    private final SelectionTreeFieldSetter[] singleFields;
    private final SelectionTreeFieldSetter[] collectionFIelds;

    /**
     * Collects and prepares the fields to set data onto
     * during the {@link #createFromTuple(SelectionTreeData[], Object[])} phase.
     */
    public SelectionTreeGroup(
            Class<?> resultType,
            Supplier<?> newResultProducer,
            ResultGroupProjection group,
            int resultIndex,
            List<SelectionTreeValue> tupleValues) throws SecurityException {
        super(resultType, Map.class.isAssignableFrom(resultType), newResultProducer, resultIndex);
        this.group = group;

        Set<String> identityPaths = new HashSet<>(group.getGroup().getResultIdentifierPropertyPaths());

        Collection<SelectionTreeFieldSetter> setters = new ArrayList<>(
                tupleValues.size() + group.getSubProjections().size());
        for(SelectionTreeValue value: tupleValues) {
            setters.add(createSelectionTreeField(value, identityPaths.remove(value.propertyPath)));
        }

        for (ResultSubProjection subProjection: group.getSubProjections()) {
            TypeSafeQuerySelectionProxyPropertyData<Object> subData = subProjection.getData();
            setters.add(new SubSelectionTreeField<>(
                    subProjection.getSubGroupIndex(), subData.getCollectionSupplier(),
                    getPropertyField(subData.getPropertyPath()),
                    identityPaths.remove(subData.getPropertyPath())));
        }
        if (!identityPaths.isEmpty()) {
            throw new IllegalStateException("Identity fields for result type "
                    + "[" + group.getGroup().getSelectionBuilderSpec().getSelectionBuilderClass() + "] were not selected: "
                    + identityPaths);
        }

        Map<SelectionTreeFieldSetterType, List<SelectionTreeFieldSetter>> byType = setters.stream()
                .collect(groupingBy(SelectionTreeFieldSetter::getType));
        identityFields = byType.getOrDefault(IDENTITY, emptyList()).toArray(new SelectionTreeFieldSetter[0]);
        singleFields = byType.getOrDefault(SINGLE, emptyList()).toArray(new SelectionTreeFieldSetter[0]);
        collectionFIelds = byType.getOrDefault(COLLECTION, emptyList()).toArray(new SelectionTreeFieldSetter[0]);

        // set the entire array accessible at once (for this object):
        AccessibleObject.setAccessible(setters.stream()
                .map(SelectionTreeFieldSetter::getField)
                .filter(Objects::nonNull)
                .toArray(Field[]::new), true);
    }

    /**
     * Go down to the node which has the property as value and create a selection field for the value.
     * Going down is for embedded/composite objects, in almost all cases, the valueTree will be 'this'.
     */
    private SelectionTreeField createSelectionTreeField(SelectionTreeValue value, boolean identityField) {
        return new SelectionTreeField(value.valueTransformer, getPropertyField(value.propertyPath),
                value.mapSelectionKey, value.tupleValueIndex, identityField);
    }

    /**
     * Traverses the subtrees by using the nested property path until just before the last path part.
     * Wraps the subtreetree (= owner of the field) and the field into a subtreefield.
     */
    private Field getPropertyField(String propertyPath) {
        return isMap() ? null: getField(getResultType(), propertyPath);
    }

    /**
     *
     */
    public void createFromTuple(SelectionTreeData[] dataArray, Object[] tuple)
            throws IllegalArgumentException, IllegalAccessException {
        // populate 'new instances' of this and composite/embedded objects
        Object resultValue = newResultValue();
        SelectionTreeData data = initialize(dataArray, resultValue);

        // populate identity fields:
        setIdentityFields(data, dataArray, tuple);
        if (data.getResult() == NULL_RESULT) {
            return;
        }

        boolean hasValue = identityFields.length > 0;
        for(SelectionTreeFieldSetter field: collectionFIelds) {
            hasValue |= field.setField(data, dataArray, tuple) != null;
        }

        if (data.isDuplicate()) {
            return;
        }

        for (SelectionTreeFieldSetter field: singleFields) {
            hasValue |= field.setField(data, dataArray, tuple) != null;
        }

        for (ResultMergeProjection mergeProjection: group.getMergeProjections()) {
            SelectionTreeData subData = dataArray[mergeProjection.getSubGroupIndex()];
            ((SelectionMerger) mergeProjection.getMerger()).mergeIntoResult(resultValue,
                    subData == null ? null: subData.getCurrentValue());
        }

        if (!hasValue) {
            dataArray[getResultIndex()].setResult(NULL_RESULT);
        }
    }

    private void setIdentityFields(SelectionTreeData data, SelectionTreeData[] dataArray, Object[] tuple)
            throws IllegalAccessException {
        if (identityFields.length > 0) {
            boolean identityExists = true;
            boolean nullIdentity = true;
            SelectionIdentityTree identity = data.identityTree;
            for(SelectionTreeFieldSetter field: identityFields) {
                Object value = field.setField(data, dataArray, tuple);
                if (nullIdentity && value != null) {
                    nullIdentity = false;
                }
                SelectionIdentityTree nextIdentity = identity.getSubtree(value);
                if (nextIdentity == null) {
                    // identity doesn't exist yet, this object is 'new'.
                    nextIdentity = identity.createSubtree(value);
                    identityExists = false;
                }
                identity = nextIdentity;
            }

            if (nullIdentity) {
                data.setResult(NULL_RESULT);
            } else if (identityExists) {
                // check identity, if equal, return existing object
                data.setResult(identity.getIdentityResult());
                data.setDuplicate(true);
            } else {
                // remember value for future identity check
                identity.setIdentityResult(data.getResult());
            }
        }
    }

    public TypeSafeQuerySelectionGroupInternal getGroup() {
        return group.getGroup();
    }

}
