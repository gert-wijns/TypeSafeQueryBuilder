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

import static java.lang.String.format;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.shad.tsqb.selection.SelectionIdentityTree;
import be.shad.tsqb.selection.SelectionTree;
import be.shad.tsqb.selection.SelectionTreeData;
import be.shad.tsqb.selection.SelectionTreeValue;
import be.shad.tsqb.selection.SelectionValueTransformer;
import be.shad.tsqb.selection.parallel.SelectionMerger;

/**
 * The root of a selection tree which will select into a dto.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SelectionTreeGroup extends SelectionTree {

    private TypeSafeQuerySelectionGroup group;

    private final SelectionTreeGroup parent;
    private final Field parentCollectionField;
    private final Class collectionClass;

    private final SelectionTreeField[] otherFields;
    private final SelectionTreeField[] identityFields;

    /**
     * Collects and prepares the fields to set data onto
     * during the {@link #createFromTuple(SelectionTreeData[], Object[])} phase.
     */
    public SelectionTreeGroup(
            TypeSafeQuerySelectionGroup group,
            List<SelectionTreeValue> tupleValues,
            SelectionTreeGroup parent) throws NoSuchFieldException, SecurityException {
        super(group.getResultClass());
        this.group = group;
        this.parent = parent;

        Set<String> identityPaths = group.getResultIdentifierPropertyPaths();
        identityFields = new SelectionTreeField[identityPaths.size()];
        otherFields = new SelectionTreeField[tupleValues.size() - identityFields.length];

        int fieldsCount = tupleValues.size();
        if (group.getCollectionPropertyPath() != null) {
            fieldsCount++;
        }

        int fieldIndex = 0;
        int otherFieldsIndex = 0;
        int identityFieldsIndex = 0;
        Field[] fields = new Field[fieldsCount];
        for(SelectionTreeValue value: tupleValues) {
            SelectionTreeField field = createSelectionTreeField(value);
            if (identityPaths.contains(value.propertyPath)) {
                identityFields[identityFieldsIndex++] = field;
            } else {
                otherFields[otherFieldsIndex++] = field;
            }
            fields[fieldIndex++] = field.field;
        }

        if (group.getCollectionPropertyPath() != null) {
            SubtreeField collectionField = getSubtreeField(parent, group.getCollectionPropertyPath());
            parentCollectionField = collectionField.field;
            collectionClass = determineCollectionClassToUse(collectionField.field.getType());
            fields[fieldIndex] = parentCollectionField;
        } else {
            parentCollectionField = null;
            collectionClass = null;
        }

        // set the entire array accessible at once (for this object):
        AccessibleObject.setAccessible(fields, true);
    }

    /**
     * Chooses a collection type based on the type.
     * If the type is a List interface, then use ArrayList.
     * If the type is a Set interface, then use HashSet.
     * If the type is not a subtype of Collection (must be something like Object/Serializable), then use HashSet.
     * Otherwise assume it is a concrete collection type.
     */
    private Class determineCollectionClassToUse(Class<?> type) {
        Class<?> fieldClass = type;
        if (fieldClass.isInterface()) {
            if (List.class.isAssignableFrom(fieldClass)) {
                fieldClass = ArrayList.class;
            } else {
                fieldClass = HashSet.class;
            }
        } else if (fieldClass.isAssignableFrom(Collection.class)) {
            // this is the case when the fieldClass is a super class of Collection,
            // this can happen when using generics to select a collection into
            fieldClass = HashSet.class;
        } else if (!Collection.class.isAssignableFrom(fieldClass)) {
            throw new IllegalArgumentException(format(
                    "Class [%s] can't be used as collection.",
                    type));
        }
        return fieldClass;
    }

    /**
     * Go down to the node which has the property as value and create a selection field for the value.
     * Going down is for embedded/composite objects, in almost all cases, the valueTree will be 'this'.
     */
    private SelectionTreeField createSelectionTreeField(SelectionTreeValue value) {
        SubtreeField subtreeField = getSubtreeField(this, value.propertyPath);
        return new SelectionTreeField(subtreeField.subtree, value.valueTransformer,
                subtreeField.field, value.tupleValueIndex);
    }

    /**
     * Traverses the subtrees by using the nested property path until just before the last path part.
     * Wraps the subtreetree (= owner of the field) and the field into a subtreefield.
     */
    private static SubtreeField getSubtreeField(SelectionTree root, String propertyPath) {
        String[] alias = propertyPath.split("\\.");
        SelectionTree valueTree = root;
        for(int i=0; i < alias.length-1; i++) {
            try {
                valueTree = valueTree.getSubtree(alias[i]);
            } catch (NoSuchFieldException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        Field field = getField(valueTree.getResultType(), alias[alias.length-1]);
        return new SubtreeField(valueTree, field);
    }

    /**
     *
     */
    public void createFromTuple(SelectionTreeData[] dataArray, Object[] tuple)
            throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        // populate 'new instances' of this and composite/embedded objects
        Object resultValue = getResultType().newInstance();
        Object parentValue = null;
        Collection<Object> collection = null;
        if (parent != null) {
            parentValue = dataArray[parent.getResultIndex()].getCurrentValue();
            if (parentValue == null) {
                // parent is null while there should be one, so don't consider this result either.
                return;
            }
            if (parentCollectionField != null) {
                collection = (Collection<Object>) parentCollectionField.get(parentValue);
                if (collection == null) {
                    collection = (Collection<Object>) collectionClass.newInstance();
                    parentCollectionField.set(parentValue, collection);
                }
            }
        }
        initialize(dataArray, resultValue);

        // populate identity fields:
        if (identityFields.length > 0) {
            SelectionTreeData data = dataArray[getResultIndex()];
            boolean identityExists = identityFields.length > 0;
            boolean nullIdentity = true;
            SelectionIdentityTree identity = data.identityTrees.get(parentValue);
            if (identity == null) {
                identity = new SelectionIdentityTree();
                data.identityTrees.put(parentValue, identity);
                identityExists = false;
            }
            for(SelectionTreeField field: identityFields) {
                Object value = setField(dataArray, field, tuple);
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
                data.setCurrentValue(null);
                data.setDuplicate(false);
                return;
            }

            // check identity, if equal, return existing object
            if (identityExists) {
                data.setCurrentValue(identity.getIdentityValue());
                data.setDuplicate(true);
                return;
            }
            // remember value for future identity check
            identity.setIdentityValue(resultValue);

            // object didn't exist, set remaining fields:
            for(SelectionTreeField field: otherFields) {
                setField(dataArray, field, tuple);
            }
        } else {
            // object didn't exist, set remaining fields:
            boolean nullValue = true;
            for(SelectionTreeField field: otherFields) {
                Object value = setField(dataArray, field, tuple);
                if (nullValue && value != null) {
                    nullValue = false;
                }
            }
            if (nullValue) {
                SelectionTreeData data = dataArray[getResultIndex()];
                data.setCurrentValue(null);
                data.setDuplicate(false);
                return;
            }
        }

        if (parent != null) {
            SelectionMerger selectionMerger = group.getSelectionMerger();
            if (selectionMerger != null) {
                // subselect value merged result dto:
                selectionMerger.mergeIntoResult(parentValue, resultValue);
            } else {
                // subselect collection result dto:
                collection.add(resultValue);
            }
        }
    }

    private Object setField(SelectionTreeData[] dataArray, SelectionTreeField field, Object[] tuple)
            throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        Object value = tuple[field.tupleValueIndex];
        if (field.valueTransformer != null) {
            value = field.valueTransformer.convert(value);
        }
        field.field.set(dataArray[field.valueTree.getResultIndex()].getCurrentValue(), value);
        return value;
    }

    public TypeSafeQuerySelectionGroup getGroup() {
        return group;
    }

    private final static class SelectionTreeField {
        final SelectionValueTransformer valueTransformer;
        final SelectionTree valueTree;
        final int tupleValueIndex;
        final Field field;

        public SelectionTreeField(SelectionTree valueTree,
                SelectionValueTransformer valueTransformer,
                Field field, int tupleValueIndex) {
            this.valueTree = valueTree;
            this.valueTransformer = valueTransformer;
            this.field = field;
            this.tupleValueIndex = tupleValueIndex;
        }
    }

    /**
     * Pair containing a subtree and one of its resultType fields.
     */
    private final static class SubtreeField {
        final SelectionTree subtree;
        final Field field;

        public SubtreeField(SelectionTree subtree, Field field) {
            this.subtree = subtree;
            this.field = field;
        }
    }
}
