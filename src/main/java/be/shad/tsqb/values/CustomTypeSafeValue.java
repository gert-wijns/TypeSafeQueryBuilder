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

import java.util.Collection;
import java.util.Collections;

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;

/**
 * When the TypeSafeQueryBuilder doesn't support a certain hql construction,
 * this custom value may be used to inject hql with params into the query.
 */
public class CustomTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private final HqlQueryValue value;

    /**
     * Copy constructor
     */
    protected CustomTypeSafeValue(CopyContext context, CustomTypeSafeValue<T> original) {
        super(context, original);
        this.value = context.get(original.value);
    }

    public CustomTypeSafeValue(TypeSafeQuery query, Class<T> valueType, String hql) {
        this(query, valueType, hql, Collections.emptyList());
    }

    public CustomTypeSafeValue(TypeSafeQuery query, Class<T> valueType,
            String hql, Collection<Object> params) {
        this(query, valueType, new HqlQueryValueImpl(hql, params));
    }

    public CustomTypeSafeValue(TypeSafeQuery query,
            Class<T> valueType, HqlQueryValue value) {
        super(query, valueType);
        this.value = value;
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        return value;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new CustomTypeSafeValue<>(context, this);
    }

}
