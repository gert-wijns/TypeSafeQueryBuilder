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
            JoinType effectiveJoinType = data.getEffectiveJoinType();
            if (effectiveJoinType == null) {
                throw new IllegalArgumentException("The getter for [" + data.getProxy() + "] was called, "
                        + "but it was not passed to query.join(object, jointype).");
            }
            if (data.getPropertyPath() == null) {
                appendClassJoin(from, join, params);
            } else if (effectiveJoinType != JoinType.None) {
                if (isChildOfClassJoin(join)) {
                    appendPropertyAsClassJoin(from, join, params);
                } else {
                    appendPropertyJoin(from, join, params);
                }
            }
        }
        query.appendFrom(from.getHql());
        query.addParams(from.getParams());
    }

    /**
     * Check if one of the parents in the hierarchy for this
     * join is a propertyPath-less parent before reaching the root.
     * <p>
     * In Hibernate 5.1.0, the hql is not properly converted to sql
     * when such a case exists.
     */
    private boolean isChildOfClassJoin(TypeSafeQueryJoin<?> join) {
        TypeSafeQueryProxyData data = join.getData();
        while (data.getParent() != null) {
            TypeSafeQueryProxyData parent = data.getParent();
            if (parent.getParent() == null) {
                // root found, no class join in between
                return false;
            } else if (parent.getPropertyPath() == null) {
                // property path -less parent found before root
                // this is a class join
                return true;
            }
            data = parent;
        }
        return false;
    }

    /**
     * Add class join while validating an on case was specified.
     */
    private void appendClassJoin(HqlQueryValueImpl from, TypeSafeQueryJoin<?> join, HqlQueryBuilderParams params) {
        // example: 'left join Product hobj1 on ...'
        TypeSafeQueryProxyData data = join.getData();
        from.appendHql(new StringBuilder(" ")
            .append(getJoinTypeString(data.getEffectiveJoinType()))
            .append(" ").append(helper.getEntityName(data.getPropertyType()))
            .append(" ").append(data.getAlias()).toString());

        // add the user specified on restrictions, there should at least but some restriction:
        if (!appendAdditionalRestrictions(from, join, " on ", params)) {
            if (params.isBuildingForDisplay()) {
                from.appendHql(" on ????? ");
            } else {
                throw new IllegalStateException(String.format(
                        "Class join without on restriction exists for class [%s]",
                        data.getPropertyType()));
            }
        }
    }

    /**
     * Create a join on for a property joined entity which is the child of a class joined entity.
     * This is a work around because hibernate doesn't properly convert the hql to sql in this case.
     * (the hql looks fine, but the sql throws an SqlGrammarException and the sql property join is missing)
     */
    private void appendPropertyAsClassJoin(HqlQueryValueImpl from, TypeSafeQueryJoin<?> join, HqlQueryBuilderParams params) {
        // example: 'left join Product hobj1 on ...'
        TypeSafeQueryProxyData data = join.getData();
        String alias = data.getAlias();
        StringBuilder joinSB = new StringBuilder(" ")
            .append(getJoinTypeString(data.getEffectiveJoinType()))
            .append(" ").append(helper.getEntityName(data.getPropertyType()))
            .append(" ").append(alias).append(" on ");
        if (data.getProxyType().isCollection()) {
            String parentId = data.getParent().getIdentifierPath();
            // parent.parentIdentifier = child.parentPath.parentIdentifier
            String mappedByProperty = helper.getMappedByProperty(data);
            joinSB.append(data.getParent().getAlias())
                .append(".").append(parentId)
                .append(" = ").append(alias)
                .append(".").append(mappedByProperty)
                .append(".").append(parentId);
        } else {
            String childId = data.getIdentifierPath();
            // child.childIdentifier = parent.childPath.childIdentifier
            joinSB.append(alias).append(".").append(childId)
                .append(" = ").append(data.getParent().getAlias())
                .append(".").append(data.getPropertyPath())
                .append(".").append(childId);
        }
        from.appendHql(joinSB.toString());

        // append additional "with" restrictions if such exist (continue after identity restriction):
        appendAdditionalRestrictions(from, join, " and ", params);
    }

    /**
     * Adds additional restrictions to the existing from statement, continuing with <code>glue</code> if such restrictions exist.
     */
    private boolean appendAdditionalRestrictions(HqlQueryValueImpl from, TypeSafeQueryJoin<?> join, String glue, HqlQueryBuilderParams params) {
        HqlQueryValue hqlQueryValue = join.getRestrictions().toHqlQueryValue(params);
        String withHql = hqlQueryValue.getHql();
        if (withHql.length() > 0) {
            from.appendHql(glue).append(withHql);
            from.addParams(hqlQueryValue.getParams());
            return true;
        }
        return false;
    }

    /**
     *
     */
    private void appendPropertyJoin(HqlQueryValueImpl from, TypeSafeQueryJoin<?> join, HqlQueryBuilderParams params) {
        TypeSafeQueryProxyData data = join.getData();
        // example: 'left join fetch' 'hobj1'.'propertyPath' 'hobj2'
        from.appendHql(new StringBuilder(" ")
            .append(getJoinTypeString(data.getEffectiveJoinType()))
            .append(" ").append(data.getParent().getAlias())
            .append(".").append(data.getPropertyPath())
            .append(" ").append(data.getAlias()).toString());

        // append additional "with" restrictions if such exist:
        appendAdditionalRestrictions(from, join, " with ", params);
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
