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
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;

public class CastTypeSafeValue<T> extends TypeSafeValueImpl<T> {

    private final TypeSafeValue<?> value;

    /**
     * Copy constructor
     */
    protected CastTypeSafeValue(CopyContext context, CastTypeSafeValue<T> original) {
        super(context, original);
        this.value = context.get(original.value);
    }

    protected CastTypeSafeValue(TypeSafeQuery query,
            Class<T> valueType, TypeSafeValue<?> value) {
        super(query, valueType);
        this.value = value;
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        HqlQueryValue hqlValue = value.toHqlQueryValue(params);
        return new HqlQueryValueImpl(String.format("cast(%s as %s)", hqlValue.getHql(),
                query.getHelper().getResolvedTypeName(getValueClass())),
                hqlValue.getParams());
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new CastTypeSafeValue<>(context, this);
    }

}
