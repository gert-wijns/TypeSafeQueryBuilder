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
import java.util.Map;
import java.util.Map.Entry;

import be.shad.tsqb.helper.ConcreteDtoClassResolver;

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
    private final ConcreteDtoClassResolver concreteDtoClassResolver;
    private final Class<?> resultType;
    private final boolean isMap;
    private int resultIndex;

    public SelectionTree(
            ConcreteDtoClassResolver concreteDtoClassResolver,
            Class<?> resultType) {
        this.concreteDtoClassResolver = concreteDtoClassResolver;
        this.resultType = concreteDtoClassResolver.getConcreteClass(resultType);
        this.isMap = Map.class.isAssignableFrom(this.resultType);
    }

    public boolean isMap() {
        return isMap;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    /**
     * Sets the resultIndexes on this and the subtrees
     * and returns the max resultIndex
     */
    public int assignResultIndexes(int parentIndex) {
        resultIndex = parentIndex+1;
        int childParentIndex = resultIndex;
        for(SelectionTree subtree: subtrees.values()) {
            childParentIndex = subtree.assignResultIndexes(childParentIndex);
        }
        return childParentIndex;
    }

    /**
     * The result index, used to select one of the data elements
     * in the dataArray durring object creation based on a result tuple.
     */
    public int getResultIndex() {
        return resultIndex;
    }

    /**
     * Add a property path to the tree, if it wasn't added before.
     * Return the existing subtree otherwise
     */
    public SelectionTree getSubtree(String property) throws SecurityException {
        Field field = getField(resultType, property);
        SelectionTree subtree = subtrees.get(field);
        if (subtree == null) {
            field.setAccessible(true);
            subtree = new SelectionTree(concreteDtoClassResolver, field.getType());
            subtrees.put(field, subtree);
        }
        return subtree;
    }

    /**
     * Sets the value object to a new value and creates sets its subobjects if they were part of the projections.
     */
    protected void initialize(SelectionTreeData[] dataArray, Object value) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        dataArray[getResultIndex()].setCurrentValue(value);
        dataArray[getResultIndex()].setDuplicate(false);
        for(Entry<Field, SelectionTree> entry: subtrees.entrySet()) {
            Field field = entry.getKey();
            Object object = field.get(value);
            if (object == null) {
                object = entry.getValue().getResultType().newInstance();
                field.set(value, object);
            }
            entry.getValue().initialize(dataArray, object);
        }
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
