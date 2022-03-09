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

import static be.shad.tsqb.restrictions.RestrictionOperator.EXISTS;
import static be.shad.tsqb.restrictions.RestrictionOperator.NOT_EXISTS;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.restrictions.predicate.RestrictionGuard;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.selection.TypeSafeQueryProjections;
import be.shad.tsqb.values.CastTypeSafeValue;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.OperatorAwareValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * The restriction exists of three parts.
 * <ul>
 * <li>Left: the part left of an operator
 * <li>Operator: the operator
 * <li>Right: the part right of an operator
 * </ul>
 * Depending on the used operator, either left, right, or both parts
 * must not be null to get a valid restriction.
 * <p>
 * The <b>exists</b> can be used without a left part.<br>
 * The <b>is_null</b>, <b>is_not_null</b> can be used without a right part.<br>
 * The rest requires both parts.
 */
public class RestrictionImpl<VAL> implements Restriction, RestrictionGuard {

    private final RestrictionsGroupInternal group;
    private final TypeSafeQueryInternal query;

    private final RestrictionPredicate predicate;
    private TypeSafeValue<VAL> left;
    private RestrictionOperator operator;
    private TypeSafeValue<VAL> right;

    public RestrictionImpl(RestrictionsGroupInternal group,
            RestrictionPredicate predicate,
            TypeSafeValue<VAL> left,
            RestrictionOperator operator,
            TypeSafeValue<VAL> right) {
        this.group = group;
        this.query = group.getQuery();
        this.predicate = predicate;
        setLeft(left);
        setOperator(operator);
        setRight(right);
    }

    /**
     * Copy constructor
     */
    public RestrictionImpl(CopyContext context, RestrictionImpl<VAL> original) {
        context.put(original, this);
        this.group = context.get(original.group);
        this.query = context.get(original.query);
        this.left = context.get(original.left);
        this.right = context.get(original.right);
        this.predicate = context.get(original.predicate);
        this.operator = original.operator;
    }

    public void setOperator(RestrictionOperator operator) {
        this.operator = operator;
    }

    @Override
    public RestrictionsGroup getRestrictionsGroup() {
        return group;
    }

    public TypeSafeValue<?> getLeft() {
        return left;
    }

    public void setLeft(TypeSafeValue<VAL> left) {
        this.left = left;
        validateInScope(left);
    }

    public TypeSafeValue<VAL> getRight() {
        return right;
    }

    public void setRight(TypeSafeValue<VAL> right) {
        this.right = right;
        validateInScope(right);
    }

    private void validateInScope(TypeSafeValue<VAL> value) {
        query.validateInScope(value, group.getJoin());
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        HqlQueryValueImpl value = new HqlQueryValueImpl();
        if (left != null) {
            HqlQueryValue hqlQueryValue;
            if (leftSideRequiresLiterals() && !params.isRequiresLiterals()) {
                boolean previous = params.setRequiresLiterals(true);
                hqlQueryValue = left.toHqlQueryValue(params);
                params.setRequiresLiterals(previous);
            } else {
                hqlQueryValue = left.toHqlQueryValue(params);
            }
            value.appendHql(hqlQueryValue.getHql());
            value.addParams(hqlQueryValue.getParams());
        }
        if (operator != null) {
            addOperator(value, operator);
        }
        if (right != null) {
            HqlQueryValue hqlQueryValue;
            if (rightSideRequiresLiterals() && !params.isRequiresLiterals()) {
                boolean previous = params.setRequiresLiterals(true);
                hqlQueryValue = right.toHqlQueryValue(params);
                params.setRequiresLiterals(previous);
            } else {
                hqlQueryValue = right.toHqlQueryValue(params);
            }
            value.appendHql(hqlQueryValue.getHql());
            value.addParams(hqlQueryValue.getParams());
        }
        return value;
    }

    private void addOperator(HqlQueryValueImpl value, RestrictionOperator operator) {
        if (left != null) {
            value.appendHql(" ");
        }
        if (right instanceof OperatorAwareValue) {
            value.appendHql(((OperatorAwareValue) right).getOperator(operator).getOperator());
        } else {
            value.appendHql(operator.getOperator());
        }
        if (right != null) {
            value.appendHql(" ");
        }
        if ((operator == EXISTS || operator == NOT_EXISTS)
                && right instanceof TypeSafeSubQuery<?>) {
            addDummySubQuerySelectIfMissing((TypeSafeSubQuery<?>) right);
        }
    }

    /**
     * Adds a dummy 'select 1' to subqueries in case of exists/not exists.
     * This is the easiest way to allow validating the user selected value
     * in case the subquery was not used for exists/not exists.
     */
    private void addDummySubQuerySelectIfMissing(TypeSafeSubQuery<?> subquery) {
        TypeSafeQueryProjections projections = ((TypeSafeQueryInternal) subquery).getProjections();
        if (projections.getProjections().isEmpty()) {
            projections.project(1L, null);
        }
    }

    @Override
    public Restriction getRestriction() {
        return this;
    }

    /**
     * Hibernate will validate the left side parameter type is exactly
     * the same as the right side during the parameter binding phase for some reason
     * this doesn't seem to be a database restriction however.
     * <p>
     * By using literals instead, this problem is avoided:
     */
    private boolean leftSideRequiresLiterals() {
        if (right != null && left instanceof DirectTypeSafeValue<?>) {
            Class<VAL> valueClass = left.getValueClass();
            if (Number.class.isAssignableFrom(valueClass)) {
                Object value = ((DirectTypeSafeValue<?>) left).getValue();
                return !right.getValueClass().isInstance(value);
            }
        }
        return right instanceof CastTypeSafeValue<?>;
    }

    /**
     * See remark {@link #leftSideRequiresLiterals()}
     */
    private boolean rightSideRequiresLiterals() {
        if (left != null && right instanceof DirectTypeSafeValue<?>) {
            Class<VAL> valueClass = right.getValueClass();
            if (Number.class.isAssignableFrom(valueClass)) {
                Object value = ((DirectTypeSafeValue<?>) right).getValue();
                return !left.getValueClass().isInstance(value);
            }
        }
        return left instanceof CastTypeSafeValue<?>;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new RestrictionImpl<>(context, this);
    }

    @Override
    public boolean isRestrictionApplicable() {
        RestrictionPredicate filter = this.predicate;
        if (filter == null) {
            filter = query.getDefaultRestrictionPredicate();
        }
        if (filter != null) {
            if (left != null && !filter.isValueApplicable(left)) {
                return false;
            }
            if (right != null && !filter.isValueApplicable(right)) {
                return false;
            }
        }
        return true;
    }

}
