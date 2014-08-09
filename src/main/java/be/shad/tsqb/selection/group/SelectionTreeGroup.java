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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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

    public SelectionTreeGroup(
            TypeSafeQuerySelectionGroup group, 
            List<SelectionTreeValue> tupleValues,
            SelectionTreeGroup parent) throws NoSuchFieldException, SecurityException {
        super(group.getResultClass());
        this.group = group;
        this.parent = parent;
        
        List<String> identityPaths = group.getResultIdentifierPropertyPaths();
        identityFields = new SelectionTreeField[identityPaths.size()];
        otherFields = new SelectionTreeField[tupleValues.size() - identityFields.length];
        
        int fieldsCount = tupleValues.size();
        if (group.getCollectionPropertyPath() != null) {
            fieldsCount++;
        }
        Field[] fields = new Field[fieldsCount]; 
        
        int f = 0; 
        int otherFieldsIndex = 0;
        for(SelectionTreeValue value: tupleValues) {
            int identityIdx = identityPaths.indexOf(value.propertyPath);
            SelectionTreeField field = createSelectionTreeField(value);
            if (identityIdx >= 0) {
                identityFields[identityIdx] = field;
            } else {
                otherFields[otherFieldsIndex++] = field;
            }
            fields[f++] = field.field;
        }

        if (group.getCollectionPropertyPath() != null) {
            String[] alias = group.getCollectionPropertyPath().split("\\.");
            SelectionTree collectionOwner = parent;
            for(int i=0; i < alias.length-1; i++) {
                collectionOwner = collectionOwner.getSubtree(alias[i]);
            }
            parentCollectionField = getField(collectionOwner.getResultType(), alias[alias.length-1]);
            Class<?> fieldClass = parentCollectionField.getType();
            if (fieldClass.isInterface()) {
                if (List.class.isAssignableFrom(fieldClass)) {
                    fieldClass = ArrayList.class;
                } else {
                    fieldClass = HashSet.class;
                }
            } else if (fieldClass.isAssignableFrom(Collection.class)) {
                fieldClass = HashSet.class;
            }
            collectionClass = fieldClass;
            fields[f] = parentCollectionField;
        } else {
            parentCollectionField = null;
            collectionClass = null;
        }
        
        AccessibleObject.setAccessible(fields, true);
    }

    /**
     * Go down to the node which has the property as value and create a selection field for the value.
     * Going down is for embedded/composite objects, in almost all cases, the valueTree will be 'this'.
     */
    private SelectionTreeField createSelectionTreeField(SelectionTreeValue value) {
        String[] alias = value.propertyPath.split("\\.");
        SelectionTree valueTree = this;
        for(int i=0; i < alias.length-1; i++) {
            try {
                valueTree = valueTree.getSubtree(alias[i]);
            } catch (NoSuchFieldException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        return new SelectionTreeField(valueTree, value.valueTransformer, 
                getField(valueTree.getResultType(), alias[alias.length-1]), 
                value.tupleValueIndex);
    }
    
    /**
     * 
     */
    public Object createFromTuple(SelectionTreeData[] dataArray, Object[] tuple) 
            throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        SelectionTreeData data = dataArray[getResultIndex()];
        // populate 'new instances' of this and composite/embedded objects
        populate(getResultType().newInstance());
        
        // populate identity fields:
        if (identityFields.length > 0) {
            boolean identityExists = identityFields.length > 0;
            SelectionIdentityTree identity = data.identityTree;
            for(SelectionTreeField field: identityFields) {
                Object value = setField(field, tuple);
                SelectionIdentityTree nextIdentity = identity.getSubtree(value);
                if (nextIdentity == null) {
                    // identity doesn't exist yet, this object is 'new'.
                    nextIdentity = identity.createSubtree(value);
                    identityExists = false;
                }
                identity = nextIdentity;
            }

            // check identity, if equal, return existing object
            if (identityExists) {
                data.setCurrentValue(identity.getIdentityValue());
                data.setDuplicate(true);
                if (group.isResultGroup()) {
                    return null;
                }
                return identity.getIdentityValue();
            }
            identity.setIdentityValue(getValue());
        }
        
        // object didn't exist, set remaining fields:
        for(SelectionTreeField field: otherFields) {
            setField(field, tuple);
        }
        data.setCurrentValue(getValue());
        data.setDuplicate(false);
        
        if (parent != null) {
            SelectionTreeData parentData = dataArray[parent.getResultIndex()];
            SelectionMerger selectionMerger = group.getSelectionMerger();
            if (selectionMerger != null) {
                // subselect value merged result dto:
                selectionMerger.mergeIntoResult(parentData.getCurrentValue(), data.getCurrentValue());
            } else {
                // subselect collection result dto:
                Object parent = parentData.getCurrentValue();
                Collection<Object> collection = (Collection<Object>) parentCollectionField.get(parent);
                if (collection == null) {
                    collection = (Collection<Object>) collectionClass.newInstance();
                    parentCollectionField.set(parent, collection);
                }
                collection.add(data.getCurrentValue());
            }
        }
        return getValue();
    }

    private Object setField(SelectionTreeField field, Object[] tuple) 
            throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        Object value = tuple[field.tupleValueIndex];
        if (field.valueTransformer != null) {
            value = field.valueTransformer.convert(value);
        }
        field.field.set(field.valueTree.getValue(), value);
        return value;
    }

    public TypeSafeQuerySelectionGroup getGroup() {
        return group;
    }

    private final static class SelectionTreeField {
        final int tupleValueIndex;
        final SelectionValueTransformer valueTransformer;
        final SelectionTree valueTree;
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
}
