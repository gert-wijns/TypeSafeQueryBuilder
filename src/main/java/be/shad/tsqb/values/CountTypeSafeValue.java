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
 * Wrap a value to put inside the count function.
 * <p>
 * Can't use WrappedTypeSafeValue because the wrapped type must be the same as the wrapper type.
 */
public class CountTypeSafeValue extends TypeSafeValueImpl<Long> implements IsMaybeDistinct, TypeSafeValueContainer {
    private TypeSafeValue<?> value;

    public CountTypeSafeValue(TypeSafeQuery query, TypeSafeValue<?> value) {
        super(query, Long.class);
        this.value = value;
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        HqlQueryValue value = this.value.toHqlQueryValue(params);
        return new HqlQueryValueImpl("count("+value.getHql()+")", value.getParams());
    }

    @Override
    public boolean isDistinct() {
        return value instanceof IsMaybeDistinct && ((IsMaybeDistinct) value).isDistinct();
    }
    
    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        validator.validateInScope(value);
    }
    
}
