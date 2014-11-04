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

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.selection.TypeSafeValueProjection;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Allows ordering by an alias which should also be part of the select string.
 */
public class OrderByProjection implements OrderBy {
    private final TypeSafeQuery query;
    private final String propertyPath;
    private boolean descending;

    public OrderByProjection(TypeSafeQuery query, String propertyPath, boolean descending) {
        this.query = query;
        this.propertyPath = propertyPath;
        this.descending = descending;
    }

    /**
     * Copy constructor
     */
    protected OrderByProjection(CopyContext context, OrderByProjection original) {
        this.query = context.get(original.query);
        this.propertyPath = original.propertyPath;
        this.descending = original.descending;
    }

    /**
     * Orders by the projection with the same alias using the index of the alias.
     * The order by uses the column index because ordering by the alias
     * doesn't work with hibernate (or at least this version) and ordering
     * by the hql of the projected value may also cause problems when it is a subquery.
     */
    @Override
    public void appendTo(HqlQuery hqlQuery, HqlQueryBuilderParams params) {
        //ascending is the default
        String order = descending ? " desc": "";

        // look for the projection with the correct alias and append it as order by:
        TypeSafeRootQueryInternal query = (TypeSafeRootQueryInternal) this.query;
        TypeSafeValue<?> value = query.getProjections().getTypeSafeValue(propertyPath,
                params.isBuildingForDisplay());

        String hqlString = null;
        if (value instanceof TypeSafeSubQuery<?>) {
            int aliasIndex = 1;
            for(TypeSafeValueProjection projection: query.getProjections().getProjections()) {
                if (propertyPath.equals(projection.getPropertyPath())) {
                    break;
                }
                aliasIndex++;
            }
            hqlString = Integer.toString(aliasIndex);
        } else {
            boolean previous = params.setRequiresLiterals(true);
            HqlQueryValue hqlValue = value.toHqlQueryValue(params);
            params.setRequiresLiterals(previous);
            hqlString = hqlValue.getHql();
        }
        hqlQuery.appendOrderBy(hqlString + order);
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new OrderByProjection(context, this);
    }

}
