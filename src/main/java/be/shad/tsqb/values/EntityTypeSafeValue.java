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
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;

/**
 * Represents an entity for which the identity is resolved as identityValue.
 */
public class EntityTypeSafeValue<E> extends TypeSafeValueImpl<E> implements TypeSafeValueContainer {

    private final TypeSafeValue<?> identityValue;

    public EntityTypeSafeValue(TypeSafeQuery query, Class<E> valueType, TypeSafeValue<?> identityValue) {
        super(query, valueType);
        this.identityValue = identityValue;
    }

    protected EntityTypeSafeValue(CopyContext context, EntityTypeSafeValue<E> original) {
        super(context, original);
        identityValue = context.get(original.identityValue);
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new EntityTypeSafeValue<>(context, this);
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        return identityValue.toHqlQueryValue(params);
    }

    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        validator.validateInScope(identityValue);
    }
}
