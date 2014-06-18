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
package be.shad.tsqb.param;

import java.util.Collection;

public interface QueryParameterCollection<T> extends QueryParameter<T> {

    /**
     * The currently set value, may be updated by setValue.
     * Must not be null before the query is transformed to hql.
     */
    Collection<T> getValues();

    /**
     * Sets a singleton collection with the value and 
     * delegates to setValue({@link #setValue(Collection)}).
     */
    void setValue(T object);
    
    /**
     * Sets the collection value of this parameter, 
     * the collection will have to be null or the elements in 
     * the collection will have to be assignable from the value class.
     * The collection will be validated:
     * <ul>
     * <li>Null is allowed as collection value. Though presense of a value will be
     *     checked when the query is transformed to HQL.</li>
     * <li>When a collection is set, all of its elements must be assignable from the value class.</li>
     * <li>Elements must not be null.</li>
     * <li>When a collection is set, it must not be empty. A defensive copy is taken, 
     *     so adding elements to a referenced list will not work.</li>
     * </ul>
     * 
     * @throws IllegalArgumentException when {@link #isCollectionRepresentative()} is false.
     */
    <ST extends T> void setValue(Collection<ST> collection);
}
