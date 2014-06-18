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


public interface QueryParameter<T> {
    
    Class<T> getValueClass();
    
    /**
     * The alias for this parameter.
     * 
     * This will not be in the resulting hql, it is used to find 
     * a parameter which was named by the user. 
     */
    String getAlias();

    /**
     * Sets the alias on the parameter.
     * This should not be called outside QueryParameters,
     * otherwise the mapping to look up aliased parameters
     * will break.
     */
    void setAlias(String alias);

    /**
     * The name used in the resulting hql, and the name to be used
     * when binding the parameter to the session query object.
     */
    String getName();
    
    /**
     * Sets the value of this parameter, the value will have to be null or it
     * must be assignable from the value class.
     * <p>
     * If this parameter is a collection representative, the value will be wrapped 
     * in a collection and delegated to {@link #setValue(Collection)}.
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
    
    /**
     * Whether this parameter represents a collection.
     */
    boolean isCollectionRepresentative();
    
}
