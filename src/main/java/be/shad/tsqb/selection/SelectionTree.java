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
import java.util.function.Supplier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Tree of values created once per query.
 * <p>
 * Call populate per tuple to create the values, so they are available without further
 * if-logic for performance when selecting many rows.
 * <p>
 * Works together with TypeSafeQueryResultTransformer in order to select nested values fast.
 */
@RequiredArgsConstructor
public class SelectionTree {
    private final @Getter Class<?> resultType;
    private final @Getter boolean isMap;
    private final Supplier<?> newResultProducer;

    /**
     * The result index, used to select one of the data elements
     * in the dataArray durring object creation based on a result tuple.
     */
    private final @Getter int resultIndex;

    public Object newResultValue() {
        return newResultProducer.get();
    }

    /**
     * Sets the value object to a new value and creates sets its subobjects if they were part of the projections.
     */
    protected SelectionTreeData initialize(SelectionTreeData[] dataArray, Object value) throws IllegalArgumentException {
        dataArray[getResultIndex()].setResult(new SelectionTreeResult(value));
        dataArray[getResultIndex()].setDuplicate(false);
        return dataArray[getResultIndex()];
    }

    /**
     * Search for the field on the class or one of its super classes.
     */
    public static Field getField(Class<?> clazz, String name) {
        if (clazz == null) {
            throw new IllegalArgumentException("Clazz may not be null");
        }
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
