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
package be.shad.tsqb.data;

import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.joins.TypeSafeQueryJoin;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;

public class TypeSafeQueryFrom implements HqlQueryBuilder {

    private final TypeSafeQueryHelper helper;
    private final TypeSafeQueryProxyData root;
    private List<TypeSafeQueryJoin<?>> joins = new LinkedList<>();

    public TypeSafeQueryFrom(TypeSafeQueryHelper helper,
            TypeSafeQueryProxyData root) {
        this.helper = helper;
        this.root = root;
    }

    public List<TypeSafeQueryJoin<?>> getJoins() {
        return joins;
    }

    public TypeSafeQueryProxyData getRoot() {
        return root;
    }

    public void addJoin(TypeSafeQueryJoin<?> join) {
        joins.add(join);
    }

    @Override
    public void appendTo(HqlQuery query, HqlQueryBuilderParams params) {
        HqlQueryValueImpl from = new HqlQueryValueImpl();
        from.appendHql(helper.getEntityName(root.getPropertyType()));
        from.appendHql(" ").append(root.getAlias());
        for(TypeSafeQueryJoin<?> join: joins) {
            TypeSafeQueryProxyData data = join.getData();
            if (data.getProxy() == null) {
                throw new IllegalStateException(format("Data [%s] was added as a join, but does not have a proxy.", data));
            }
            if (data.getEffectiveJoinType() == null) {
                throw new IllegalArgumentException("The getter for [" + data.getProxy() + "] was called, "
                        + "but it was not passed to query.join(object, jointype).");
            }
            if (data.getEffectiveJoinType() != JoinType.None) {
                // example: 'left join fetch' 'hobj1'.'propertyPath' 'hobj2'
                from.appendHql(format(" %s %s.%s %s", getJoinTypeString(data.getEffectiveJoinType()),
                        data.getParent().getAlias(), data.getPropertyPath(), data.getAlias()));
                HqlQueryValue hqlQueryValue = join.getRestrictions().toHqlQueryValue(params);
                String withHql = hqlQueryValue.getHql();
                if (withHql.length() > 0) {
                    from.appendHql(" with ").append(withHql);
                    from.addParams(hqlQueryValue.getParams());
                }
            }
        }
        query.appendFrom(from.getHql());
        query.addParams(from.getParams());
    }

    /**
     * Convert the join type to a string.
     */
    private String getJoinTypeString(JoinType joinType) {
        switch (joinType) {
            case Fetch: return "join fetch";
            case Inner: return "join";
            case Left: return "left join";
            case LeftFetch: return "left join fetch";
            case Right: return "right join";
            default:
        }
        throw new IllegalArgumentException("JoinType " + joinType + " is no allowed.");
    }

}
