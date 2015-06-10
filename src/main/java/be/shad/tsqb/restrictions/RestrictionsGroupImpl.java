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
package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionNodeType.And;
import static be.shad.tsqb.restrictions.RestrictionNodeType.Or;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.restrictions.predicate.RestrictionGuard;
import be.shad.tsqb.values.CustomTypeSafeValue;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Groups restrictions, either to restrict the where clause of a query or to restrict the with clause of a join.
 * <p>
 * A restriction group may be nested, to group a sequence of 'ors' in one part of a query for example.
 */
public class RestrictionsGroupImpl extends RestrictionChainableImpl implements RestrictionAndChainable, RestrictionsGroupInternal {
    private final TypeSafeQueryInternal query;
    private final TypeSafeQueryProxyData join;
    private final List<RestrictionNode> restrictions = new LinkedList<>();
    private RestrictionsGroupBracketsPolicy bracketsPolicy;

    public RestrictionsGroupImpl(TypeSafeQueryInternal query,
            TypeSafeQueryProxyData join,
            RestrictionsGroupBracketsPolicy bracketsPolicy) {
        this.query = query;
        this.join = join;
        this.bracketsPolicy = bracketsPolicy;
    }

    public RestrictionsGroupImpl(TypeSafeQueryInternal query,
            TypeSafeQueryProxyData join) {
        this(query, join, RestrictionsGroupBracketsPolicy.WhenMoreThanOne);
    }

    public RestrictionsGroupImpl(CopyContext context, RestrictionsGroupImpl original) {
        this.query = context.get(original.query);
        this.join = context.get(original.join);
        this.bracketsPolicy = original.bracketsPolicy;
        for(RestrictionNode node: original.restrictions) {
            restrictions.add(new RestrictionNode(context.get(node.getRestriction()), node.getType()));
        }
    }

    @Override
    public RestrictionsGroup or(RestrictionHolder first, RestrictionHolder... restrictions) {
        or(first.getRestriction());
        if (restrictions != null) {
            for(RestrictionHolder restriction: restrictions) {
                or(restriction.getRestriction());
            }
        }
        return this;
    }

    @Override
    public RestrictionsGroup and(RestrictionHolder first, RestrictionHolder... restrictions) {
        and(first.getRestriction());
        if (restrictions != null) {
            for(RestrictionHolder restriction: restrictions) {
                and(restriction.getRestriction());
            }
        }
        return this;
    }

    @Override
    public Restriction getRestriction() {
        return getRestrictions();
    }

    @Override
    public TypeSafeQueryInternal getQuery() {
        return query;
    }

    @Override
    public boolean isEmpty() {
        return restrictions.isEmpty();
    }

    @Override
    public RestrictionChainable where(HqlQueryValue hqlQueryvalue) {
        return and(hqlQueryvalue);
    }

    @Override
    public RestrictionChainable where(RestrictionsGroup group) {
        return and(group.getRestrictions());
    }

    @Override
    public RestrictionChainable where(Restriction restriction) {
        return and(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionsGroupImpl getRestrictionsGroup() {
        return this;
    }

    /**
     * Creates a new group which is used to restrict the where part of a query.
     */
    public static RestrictionsGroup group(TypeSafeQuery query) {
        return group(query, null);
    }

    /**
     * Creates a new group which is used to restrict the with part of a join.
     * The join data is kept in order to be able to validate if the used values are in scope.
     */
    public static RestrictionsGroup group(TypeSafeQuery query, TypeSafeQueryProxyData join) {
        return new RestrictionsGroupImpl((TypeSafeQueryInternal) query, join);
    }

    /**
     * The joined entity proxy data, might be null if this group is not used in a with (restrictions) clause.
     */
    public TypeSafeQueryProxyData getJoin() {
        return join;
    }

    /**
     * Loops the restrictions and links them together with ands and ors.
     */
    @Override
    public HqlQueryValueImpl toHqlQueryValue(HqlQueryBuilderParams params) {
        HqlQueryValueImpl value = new HqlQueryValueImpl();
        boolean hasValue = false;
        for(RestrictionNode item: restrictions) {
            Restriction restriction = item.getRestriction();
            if (isRestrictionApplicable(restriction)) {
                HqlQueryValue nextValue = restriction.toHqlQueryValue(params);
                String nextValueHql = nextValue.getHql();
                // check length, if a restriction was not applicable or a group
                // had no applicable restrictions, the hql will be empty:
                if (nextValueHql.length() > 0) {
                    if (hasValue) {
                        if (item.getType() == RestrictionNodeType.And) {
                            value.appendHql(" and ");
                        } else if (item.getType() == RestrictionNodeType.Or) {
                            value.appendHql(" or ");
                        } // else null, root
                    }
                    value.appendHql(nextValue.getHql());
                    value.addParams(nextValue.getParams());
                    hasValue = true;
                }
            }
        }
        if (!hasValue || !isAddBrackets()) {
            return value;
        }
        return new HqlQueryValueImpl("(" + value.getHql() + ")", value.getParams());
    }

    private boolean isRestrictionApplicable(Restriction restriction) {
        if (restriction instanceof RestrictionGuard) {
            return ((RestrictionGuard) restriction).isRestrictionApplicable();
        }
        return true;
    }

    /**
     * Evaluates brackets policy to decide whether to add brackets or not.
     */
    private boolean isAddBrackets() {
        switch (bracketsPolicy) {
            case WhenMoreThanOne: return restrictions.size() > 1;
            case Never:           return false;
            case Always:
            default:              return true;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Restrictions [");
        for(RestrictionNode item: restrictions) {
            sb.append("\n").append(item.getRestriction());
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable where() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionAndChainable and(Restriction restriction) {
        return add(restriction, RestrictionNodeType.And);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionAndChainable and(RestrictionsGroup group) {
        return and(group.getRestrictions());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionAndChainable or(RestrictionsGroup group) {
        return or(group.getRestrictions());
    }

    /**
     * {@inheritDoc}
     */
    public RestrictionAndChainable or(Restriction restriction) {
        return add(restriction, Or);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable and(HqlQueryValue customValue) {
        return add(customValue, And);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable or(HqlQueryValue customValue) {
        return add(customValue, Or);
    }

    private RestrictionChainable add(HqlQueryValue customValue, RestrictionNodeType type) {
        TypeSafeValue<Object> value = new CustomTypeSafeValue<Object>(query, Object.class, customValue);
        return add(new RestrictionImpl<>(this, null, value, null, null), type);
    }

    /**
     * Wraps the restriction in a group if its group was different.
     * This happens when a group was created to group 'ors' for example,
     * and this group is added to this group to add it to the query restrictions.
     * <p>
     * Example:
     * <pre>
     * Person person = query.from(Person.class);
     *  query.where(person.isMarried()).isTrue().
     *          and(RestrictionsGroup.group(query).
     *              and(person.getName()).startsWith("Jef").
     *               or(person.getName()).startsWith("John"));
     * </pre>
     * The RestrictionsGroup is created, and restrictions are added using the chaining methods.
     * At the end, when all the chaining finishes, the last restriction of the chain is returned
     * and add to this group. At this point it is detected that the restriction was part
     * of a sub-group because its restriction group is different.
     */
    private RestrictionAndChainable add(Restriction restriction, RestrictionNodeType type) {
        Restriction addRestriction = restriction;
        if (restriction.getRestrictionsGroup() != this) {
            addRestriction = restriction.getRestrictionsGroup().getRestrictions();
        } else if (restriction == this) {
            throw new IllegalArgumentException("Attempting to add a restriction group to itself. "
                    + "Did you nest query.where inside a query.where?");
        }
        restrictions.add(new RestrictionNode(addRestriction, restrictions.isEmpty() ? null: type));
        return this;
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> whereEnum(TypeSafeValue<E> value) {
        return andEnum(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> where(E value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public <VAL> OnGoingObjectRestriction<VAL> where(TypeSafeValue<VAL> value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingBooleanRestriction whereBoolean(TypeSafeValue<Boolean> value) {
        return andBoolean(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingBooleanRestriction where(Boolean value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public <N extends Number> OnGoingNumberRestriction whereNumber(TypeSafeValue<N> value) {
        return andNumber(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingNumberRestriction where(Number value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingDateRestriction whereDate(TypeSafeValue<Date> value) {
        return andDate(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingDateRestriction where(Date value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingTextRestriction whereString(TypeSafeValue<String> value) {
        return andString(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingTextRestriction where(String value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public RestrictionChainable whereExists(TypeSafeSubQuery<?> subquery) {
        return andExists(subquery);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public RestrictionChainable whereNotExists(TypeSafeSubQuery<?> subquery) {
        return andNotExists(subquery);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public Restriction getRestrictions() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBracketsPolicy(RestrictionsGroupBracketsPolicy bracketsPolicy) {
        this.bracketsPolicy = bracketsPolicy;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new RestrictionsGroupImpl(context, this);
    }

}
