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



public interface QueryParameter<T> {
    
    /**
     * The class which must be assignable from all/any 
     * value(s) set on this parameter.
     */
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
     * Method for easy assignment during the parameter binding phase
     * when the hql query is created to list results.
     */
    Object getParameterValue();
    
}
