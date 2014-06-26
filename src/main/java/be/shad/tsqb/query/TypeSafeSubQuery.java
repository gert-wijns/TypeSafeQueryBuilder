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
package be.shad.tsqb.query;

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Represents the selection of a value using a subquery. 
 * The valueType is the generic type T.
 * Subqueries may be nested as deep as needed.
 */
public interface TypeSafeSubQuery<T> extends TypeSafeValue<T>, TypeSafeQuery {
    
    /**
     * Creates a type safe value which will check for exists.
     * <p>
     * Wraps the query in a case when(exists...) then true else false.
     */
    boolean selectExists();
    
    /**
     * Creates a type safe value which will check for not exists.
     * <p>
     * Wraps the query in a case when(exists...) then false else true.
     */
    boolean selectNotExists();
    
    /**
     * Selects the count using the count function.
     */
    Long selectCount();

    /**
     * Selects the count using the count and nested distinct function.
     */
    Long selectCountDistinct(T val);
    
    /**
     * Set the value to select.
     * <p>
     * Converts the value to a TypeSafeValue and delegates to {@link #select(TypeSafeValue)}.
     */
    T select(T value);
    
    /**
     * Set the value to select.
     * <p>
     * This method should be called before converting to hql, unless using the exists function.
     */
    T select(TypeSafeValue<T> value);
    
}
