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
package be.shad.tsqb.grouping;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeQueryGroupBys implements HqlQueryBuilder, Copyable {
    private final List<TypeSafeValue<?>> values = new LinkedList<>();

    /**
     * Copy constructor
     */
    protected TypeSafeQueryGroupBys(CopyContext context, TypeSafeQueryGroupBys original) {
        for(TypeSafeValue<?> value: original.values) {
            values.add(context.get(value));
        }
    }

    public TypeSafeQueryGroupBys() {
        // default constructor for regular query creation
    }

    @Override
    public void appendTo(HqlQuery query, HqlQueryBuilderParams params) {
        for(TypeSafeValue<?> value: values) {
            HqlQueryValue hqlQueryValue = value.toHqlQueryValue(params);
            query.appendGroupBy(hqlQueryValue.getHql());
            query.addParams(hqlQueryValue.getParams());
        }
    }

    public <T> TypeSafeValue<T> add(TypeSafeValue<T> val) {
        values.add(val);
        return val;
    }

    /**
     * Delegates to copy constructor
     */
    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeQueryGroupBys(context, this);
    }

}
