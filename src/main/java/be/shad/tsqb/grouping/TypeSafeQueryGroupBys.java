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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeQueryGroupBys implements HqlQueryBuilder, OnGoingGroupBy {
    private final List<TypeSafeValue<?>> values = new LinkedList<>();
    private final TypeSafeQueryInternal query;
    
    public TypeSafeQueryGroupBys(TypeSafeQueryInternal query) {
        this.query = query;
    }

    @Override
    public void appendTo(HqlQuery query) {
        for(TypeSafeValue<?> value: values) {
            HqlQueryValue hqlQueryValue = value.toHqlQueryValue();
            query.appendGroupBy(hqlQueryValue.getHql());
            query.addParams(hqlQueryValue.getParams());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(Number val) {
        return add(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(String val) {
        return add(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(Enum<?> val) {
        return add(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(Boolean val) {
        return add(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(Date val) {
        return add(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(TypeSafeValue<?> val) {
        return add(val);
    }
    
    /**
     * Adds to the list and returns this to support method chaining.
     */
    private OnGoingGroupBy add(Object val) {
        values.add(query.toValue(val));
        return this;
    }

}
