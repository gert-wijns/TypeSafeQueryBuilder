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
package be.shad.tsqb.ordering;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.proxy.TypeSafeQuerySelectionProxy;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeQueryOrderBys implements OnGoingOrderBy, HqlQueryBuilder, Copyable {
    private final List<OrderBy> orderBys = new LinkedList<>();
    private final TypeSafeQueryInternal query;

    public TypeSafeQueryOrderBys(TypeSafeQueryInternal query) {
        this.query = query;
    }

    protected TypeSafeQueryOrderBys(CopyContext context, TypeSafeQueryOrderBys original) {
        this.query = context.get(original.query);
        for(OrderBy orderBy: original.orderBys) {
            orderBys.add(context.get(orderBy));
        }
    }

    private OnGoingOrderBy orderBy(Object val, boolean desc) {
        if (query instanceof TypeSafeRootQueryInternal) {
            // if the last invoked projection path was set, then the value
            // is a result of a getter of the selection dto.
            TypeSafeRootQueryInternal query = (TypeSafeRootQueryInternal) this.query;
            String lastInvokedProjectionPath = query.dequeueInvokedProjectionPath();
            if (lastInvokedProjectionPath != null) {
                return by(new OrderByProjection(query, lastInvokedProjectionPath, desc));
            }
        }
        if (val instanceof TypeSafeQuerySelectionProxy) {
            throw new IllegalArgumentException("Ordering by a selection proxy "
                    + "is not allowed. This was attempted for proxy: " + val);
        }
        return by(new OrderByImpl(query.toValue(val), desc));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy by(OrderBy orderBy) {
        orderBys.add(orderBy);
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(Number val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(String val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(Enum<?> val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(Boolean val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(Date val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(TypeSafeValue<?> val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(Number val) {
        return orderBy(val, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(String val) {
        return orderBy(val, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(Enum<?> val) {
        return orderBy(val, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(Boolean val) {
        return orderBy(val, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(Date val) {
        return orderBy(val, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(TypeSafeValue<?> val) {
        return orderBy(val, false);
    }
    
    @Override
    public void appendTo(HqlQuery query, HqlQueryBuilderParams params) {
        for(OrderBy orderBy: orderBys) {
            orderBy.appendTo(query, params);
        }
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeQueryOrderBys(context, this);
    }

}
