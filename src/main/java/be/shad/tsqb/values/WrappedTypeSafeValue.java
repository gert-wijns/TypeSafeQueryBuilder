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
package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryScopeValidator;

/**
 * Wraps a value in a function. 
 * Examples uses are {@link TypeSafeValueFunctions#sum(Number) sum(...)},
 * {@link TypeSafeValueFunctions#max(Number) max(...)} etc.
 */
public class WrappedTypeSafeValue<T> extends TypeSafeValueImpl<T> implements TypeSafeValueContainer {
    private String function; // sum/max/min/trim/count/...
    private TypeSafeValue<T> value;
    
    public WrappedTypeSafeValue(TypeSafeQuery query, String function, TypeSafeValue<T> value) {
        super(query, value.getValueClass());
        this.function = function;
        this.value = value;
    }
    
    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        HqlQueryValue value = this.value.toHqlQueryValue(params);
        return new HqlQueryValueImpl(function + "("+value.getHql()+")", value.getParams());
    }
    
    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        validator.validateInScope(value);
    }
}
