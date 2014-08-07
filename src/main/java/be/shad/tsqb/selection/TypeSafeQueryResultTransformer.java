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
package be.shad.tsqb.selection;

import static be.shad.tsqb.selection.SelectionTree.getField;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.BasicTransformerAdapter;

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyData;
import be.shad.tsqb.selection.collection.ResultIdentifierProvider;
import be.shad.tsqb.selection.group.SelectionTreeGroup;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroup;
import be.shad.tsqb.selection.parallel.SelectionMerger;

/**
 * Implementation to set values on nested select dtos.
 * Seems to be faster than the alias to bean result transformer too.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TypeSafeQueryResultTransformer extends BasicTransformerAdapter {
    private static final long serialVersionUID = 4686800769621139636L;
    
    private final Field[] setters;
    private final SelectionTree[] values;
    private final SelectionTreeGroup[] groups;
    private final SelectionTreeGroup[] mergeGroups;
    private final SelectionTreeGroup[] resultIdentifierGroups;
    private final CollectionField[] collectionFields;
    
    private final SelectionValueTransformer[] transformers;
    
    public TypeSafeQueryResultTransformer(
            List<TypeSafeQuerySelectionProxyData> selectionDatas, 
            List<SelectionValueTransformer<?, ?>> transformers) {
        try {
            this.transformers = transformers.toArray(new SelectionValueTransformer[transformers.size()]);
            this.setters = new Field[selectionDatas.size()];
            this.values = new SelectionTree[selectionDatas.size()];
            LinkedHashMap<TypeSafeQuerySelectionGroup, SelectionTreeGroup> groups = new LinkedHashMap<>();
            int a = 0;
            for(TypeSafeQuerySelectionProxyData selectionData: selectionDatas) {
                TypeSafeQuerySelectionGroup group = selectionData.getGroup();
                SelectionTreeGroup tree = groups.get(group);
                if (tree == null) {
                    tree = new SelectionTreeGroup(group.getResultClass(), group, groups.size());
                    groups.put(group, tree);
                }
                String propertyPath = selectionData.getEffectivePropertyPath();
                String[] alias = propertyPath.split("\\.");
                SelectionTree subtree = tree;
                for(int i=0; i < alias.length-1; i++) {
                    subtree = subtree.getSubtree(alias[i]);
                }
                values[a] = subtree;
                setters[a++] = getField(subtree.getResultType(), alias[alias.length-1]);
            }
            
            // Create groups array, having the result group as first group:
            a = 1;
            this.groups = new SelectionTreeGroup[groups.size()];

            List<SelectionTreeGroup> mergeGroups = new ArrayList<>(groups.size());
            List<SelectionTreeGroup> resultIdentifierGroups = new ArrayList<>(groups.size());
            List<CollectionField> collectionFields = new ArrayList<>(groups.size());
            for(SelectionTreeGroup group: groups.values()) {
                if (group.getGroup().getResultIdentifierProvider() != null) {
                    resultIdentifierGroups.add(group);
                }
                if (group.getGroup().isResultGroup()) {
                    this.groups[0] = group;
                } else {
                    this.groups[a] = group;
                    if (group.getGroup().getSelectionMerger() != null) {
                        mergeGroups.add(group);
                    }
                    TypeSafeQuerySelectionGroup collectionGroup = group.getGroup().getCollectionGroup();
                    if (collectionGroup != null) {
                        SelectionTreeGroup collectionTree = groups.get(collectionGroup);
                        String[] alias = group.getGroup().getCollectionPropertyPath().split("\\.");
                        SelectionTree collectionOwner = collectionTree;
                        for(int i=0; i < alias.length-1; i++) {
                            collectionOwner = collectionOwner.getSubtree(alias[i]);
                        }
                        Field collectionField = getField(collectionOwner.getResultType(), alias[alias.length-1]);
                        collectionField.setAccessible(true);
                        collectionFields.add(new CollectionField(collectionField, collectionTree.getPosition(), a));
                    }
                    a++;
                }
            }
            this.mergeGroups = mergeGroups.toArray(new SelectionTreeGroup[mergeGroups.size()]);
            this.resultIdentifierGroups = resultIdentifierGroups.toArray(new SelectionTreeGroup[resultIdentifierGroups.size()]);
            this.collectionFields = collectionFields.toArray(new CollectionField[collectionFields.size()]);
            AccessibleObject.setAccessible(setters, true);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return tuple;
    }
    
    @Override
    public List transformList(List list) {
        if (list.isEmpty()) {
            // no values found, though then this transformList is most likely not called.
            return list;
        } else if (!(list.iterator().next() instanceof Object[])) {
            // only one value was selected, nothing needs to be done
            return list;
        }
        
        List result = new ArrayList(list.size());
        Object[] resultArray = new Object[this.groups.length]; 
        if (this.groups.length == 1) {
            // if only one group, then there is no merging/collection 
            // handling, so this is the quickest way:
            for(Object obj: list) {
                decorateResultArray(resultArray, (Object[]) obj);
                result.add(resultArray[0]);
            }
            return result;
        }
        
        Map[] mapped = new Map[this.groups.length];
        for(int i=0; i < mapped.length; i++) {
            mapped[i] = new HashMap<Object, Object>();
        }

        for(Object obj: list) {
            decorateResultArray(resultArray, (Object[]) obj);
            mergeResultArray(resultArray);
            Object currentReturnValue = resultArray[0];
            replaceIdenticalAndMapNew(resultArray, mapped);
            
            for(CollectionField field: collectionFields) {
                field.addToCollection(resultArray);
            }
            
            // replaceIdenticalAndMapNew may have replaced the main result object,
            // it was still useful to process this tuple because some values will
            // have been added to the collections, but this 'duplicate' should not
            // be included in the result again
            if (currentReturnValue == resultArray[0]) {
                result.add(currentReturnValue);
            }
        }
        return result;
    }

    /**
     * Create identifier and check if result is already mapped, when it is not mapped, 
     * then add it to the mapping. When it is mapped, replace the result with the mapped value.
     */
    private void replaceIdenticalAndMapNew(Object[] resultArray, Map[] mapped) {
        // map identifiers
        for(SelectionTreeGroup resultIdentifierGroup: resultIdentifierGroups) {
            int position = resultIdentifierGroup.getPosition();
            Map mapping = mapped[position];
            ResultIdentifierProvider identifierProvider = resultIdentifierGroup.
                    getGroup().getResultIdentifierProvider();
            Object identifier = identifierProvider.createIdentifier(resultArray[position]);
            Object object = mapping.get(identifier);
            if (object == null) {
                // value was not mapped yet, add to mapping
                mapping.put(identifier, resultArray[position]);
            } else {
                // value was already mapped, replace value
                resultArray[position] = object;
            }
        }
        
    }

    /**
     * Merge all extra selected dtos, included for merging, into their target
     */
    private void mergeResultArray(Object[] resultArray) {
        for(SelectionTreeGroup mergeGroup: mergeGroups) {
            SelectionMerger merger = mergeGroup.getGroup().getSelectionMerger();
            // TODO: change '0' to the target index.. in case of merge with collection value
            merger.mergeIntoResult(resultArray[0], resultArray[mergeGroup.getPosition()]);
        }
    }

    /**
     * Instantiate new objects for each group into the result array
     * Set the values on the objects using the tuple
     */
    private void decorateResultArray(Object[] resultArray, Object[] tuple) {
        try {
            int i=0;
            for(SelectionTree group: groups) {
                resultArray[i] = group.getResultType().newInstance();
                group.populate(resultArray[i++]);
            }
            for(i=0; i < tuple.length; i++) {
                Object value = tuple[i];
                if (transformers[i] != null) {
                    value = transformers[i].convert(value);
                }
                setters[i].set(values[i].getValue(), value);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles collection field information/instantiation/filling.
     */
    private static final class CollectionField {
        private final Field field;
        private final int parentPosition;
        private final int position;
        private final Class collectionClass;
        
        public CollectionField(Field field, int parentPosition, int position) {
            this.field = field;
            this.parentPosition = parentPosition;
            this.position = position;
            Class<?> fieldClass = field.getType();
            if (fieldClass.isInterface()) {
                if (List.class.isAssignableFrom(fieldClass)) {
                    fieldClass = ArrayList.class;
                } else {
                    fieldClass = HashSet.class;
                }
            }
            collectionClass = fieldClass;
        }

        /**
         * Adds the value at <code>position</code> to the collection
         */
        public void addToCollection(Object[] resultArray) {
            try {
                Object parent = resultArray[parentPosition];
                Collection<Object> collection = (Collection<Object>) field.get(parent);
                if (collection == null) {
                    collection = (Collection<Object>) collectionClass.newInstance();
                    field.set(parent, collection);
                }
                collection.add(resultArray[position]);
            } catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
