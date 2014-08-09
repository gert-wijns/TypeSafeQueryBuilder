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

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Tree of values created once per query.
 * <p>
 * Call populate per tuple to create the values, so they are available without further 
 * if-logic for performance when selecting many rows.
 * <p>
 * Works together with TypeSafeQueryResultTransformer in order to select nested values fast.
 */
public class SelectionTree {
    private final LinkedHashMap<Field, SelectionTree> subtrees = new LinkedHashMap<>();
    private final Class<?> resultType;
    private int resultIndex;
    private Object value;

    public SelectionTree(Class<?> resultType) {
        this.resultType = resultType;
    }
    
    public Class<?> getResultType() {
        return resultType;
    }
    
    public int assignResultIndexes(int parentIndex) {
        resultIndex = parentIndex+1;
        int childParentIndex = resultIndex;
        for(SelectionTree subtree: subtrees.values()) {
            childParentIndex = subtree.assignResultIndexes(childParentIndex);
        }
        return childParentIndex;
    }

    public int getResultIndex() {
        return resultIndex;
    }

    public void collectFields(List<Field> fields) {
        for(Entry<Field, SelectionTree> subtree: subtrees.entrySet()) {
            fields.add(subtree.getKey());
            subtree.getValue().collectFields(fields);
        }
    }
    
    /**
     * Add a property path to the tree, if it wasn't added before.
     * Return the existing subtree otherwise
     */
    public SelectionTree getSubtree(String property) throws NoSuchFieldException, SecurityException {
        Field field = getField(resultType, property);
        SelectionTree subtree = subtrees.get(field);
        if( subtree == null ) {
            field.setAccessible(true);
            subtree = new SelectionTree(field.getType());
            subtrees.put(field, subtree);
        }
        return subtree;
    }
    
    /**
     * Sets the value object to a new value and creates sets its subobjects if they were part of the projections.
     */
    public void populate(Object value) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        this.value = value;
        for(Entry<Field, SelectionTree> entry: subtrees.entrySet()) {
            Field field = entry.getKey();
            Object object = field.get(value);
            if( object == null ) {
                object = field.getType().newInstance();
                field.set(value, object);
            }
            entry.getValue().populate(object);
        }
    }
    
    /**
     * The current object which can be populated/projected onto.
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * Search for the field on the class or one of its super classes.
     */
    public static Field getField(Class<?> clazz, String name) {
        Class<?> current = clazz;
        while (current != null) {
            for(Field field: current.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    return field;
                }
            }
            current = current.getSuperclass();
        }
        
        throw new IllegalArgumentException(String.format("Couldn't find field [%s] on class [%s]", name, clazz.getName()));
    }
}
