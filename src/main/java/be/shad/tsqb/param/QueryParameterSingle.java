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


public interface QueryParameterSingle<T> extends QueryParameter<T> {

    /**
     * The currently set value, may be updated by setValue.
     * Must not be null before the query is transformed to hql.
     */
    T getValue();

    /**
     * Sets the value of this parameter, the value will have to be null or it
     * must be assignable from the value class.
     */
    void setValue(T object);
    
}
