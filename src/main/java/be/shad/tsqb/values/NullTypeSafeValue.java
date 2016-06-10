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

/**
 * Not statically available because the .select()
 * should register the value in the relevant query.
 * <p>
 * This is the only way to select null in the query.
 */
public final class NullTypeSafeValue<T> extends TypeSafeValueImpl<T> {

    protected NullTypeSafeValue(CopyContext context, NullTypeSafeValue<T> original) {
        super(context, original);
    }

    public NullTypeSafeValue(TypeSafeQuery query, Class<T> valueType) {
        super(query, valueType);
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        // suggested on stackoverflow to select null:
        return new HqlQueryValueImpl("NULLIF(1,1)");
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new NullTypeSafeValue<>(context, this);
    }
}
