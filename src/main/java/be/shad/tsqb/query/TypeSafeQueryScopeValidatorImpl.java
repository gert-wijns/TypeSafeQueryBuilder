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

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.exceptions.ValueNotInScopeException;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;
import be.shad.tsqb.values.TypeSafeValueContainer;

class TypeSafeQueryScopeValidatorImpl implements TypeSafeQueryScopeValidator {
    private final TypeSafeQueryInternal query;
    private final TypeSafeQueryProxyData join;
    
    public TypeSafeQueryScopeValidatorImpl(
            TypeSafeQueryInternal query, 
            TypeSafeQueryProxyData join) {
        this.query = query;
        this.join = join;
    }
    
    /**
     * {@inheritDoc}
     */
    public void validateInScope(TypeSafeValue<?> value) {
        if( value instanceof ReferenceTypeSafeValue<?> ) {
            query.validateInScope(((ReferenceTypeSafeValue<?>) value).getData(), join);
        }
        if( value instanceof TypeSafeQueryInternal ) {
            TypeSafeQueryInternal valueQuery = (TypeSafeQueryInternal) value;
            if( valueQuery.getParentQuery() != query) {
                throw new ValueNotInScopeException(
                        "Subqueries may only be used as direct child of their parent."
                        + query.getRootQuery().toString());
            }
        }
        if( value instanceof TypeSafeValueContainer ) {
            ((TypeSafeValueContainer) value).validateContainedInScope(this);
        }
    }
    
}
