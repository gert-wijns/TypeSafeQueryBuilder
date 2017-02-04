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
import static be.shad.tsqb.restrictions.RestrictionOperator.EQUAL;
import static be.shad.tsqb.restrictions.RestrictionOperator.IN;
import static be.shad.tsqb.restrictions.RestrictionOperator.IS_NOT_NULL;
import static be.shad.tsqb.restrictions.RestrictionOperator.IS_NULL;
import static be.shad.tsqb.restrictions.RestrictionOperator.NOT_EQUAL;
import static be.shad.tsqb.restrictions.RestrictionOperator.NOT_IN;

import java.util.Collection;

import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.restrictions.named.CollectionNamedParameterBinder;
import be.shad.tsqb.restrictions.named.NamedParameterBinderImpl;
import be.shad.tsqb.restrictions.named.SingleNamedParameterBinder;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.values.CollectionTypeSafeValue;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Takes a partially built restriction and provides methods complete it.
 * Type specific methods are added in subclasses of this class.
 *
 * @see RestrictionImpl
 */
public abstract class OnGoingRestrictionImpl<VAL, CONTINUED extends ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL>,
            ORIGINAL extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>> extends RestrictionChainableDelegatingImpl
        implements OnGoingRestriction<VAL, CONTINUED, ORIGINAL>, ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL>, DirectValueProvider<VAL> {

    private final RestrictionNodeType restrictionNodeType;
    protected final TypeSafeValue<VAL> startValue;

    /*
     * NOTE: The restriction is kept around so that it is possible to 'save' the restriction to alter the query
     * without having to rebuild it completely.
     */
    private RestrictionImpl<VAL> restriction;
    private RestrictionPredicate predicate;

    public OnGoingRestrictionImpl(RestrictionsGroupInternal group, RestrictionNodeType restrictionNodeType, VAL argument) {
        super(group);
        this.restrictionNodeType = restrictionNodeType;
        this.startValue = toValue(argument, null);
    }

    public OnGoingRestrictionImpl(RestrictionsGroupInternal group, RestrictionNodeType restrictionNodeType, TypeSafeValue<VAL> argument) {
        super(group);
        this.restrictionNodeType = restrictionNodeType;
        this.startValue = argument;
    }

    /**
     * The required value class, which will be checked in the type safe values later.
     */
    protected abstract Class<VAL> getSupportedValueClass();

    /**
     * Delegates to subclass to create the correct type.
     */
    protected abstract CONTINUED createContinuedOnGoingRestriction(
            RestrictionNodeType restrictionNodeType,
            TypeSafeValue<VAL> previousValue);

    /**
     * Delegates to subclass to create the correct type.
     */
    protected abstract ORIGINAL createOriginalOnGoingRestriction(
            RestrictionNodeType restrictionNodeType,
            TypeSafeValue<VAL> previousValue);

    protected NamedParameterBinderImpl<VAL, CONTINUED, ORIGINAL> createNamedParameterBinder(
            TypeSafeValue<VAL> value, CONTINUED next) {
        return new NamedParameterBinderImpl<VAL, CONTINUED, ORIGINAL>(
                group.getQuery(), value, next);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED isNull() {
        return addRestrictionAndContinue(startValue, IS_NULL, null);
    }

    /**
     * The go-to method for to add the restriction to the restrictions group.
     */
    @SuppressWarnings("unchecked")
    protected <L extends VAL, R extends VAL> CONTINUED addRestrictionAndContinue(
            TypeSafeValue<L> left, RestrictionOperator operator, TypeSafeValue<R> right) {
        TypeSafeValue<VAL> leftVal = (TypeSafeValue<VAL>) left;
        TypeSafeValue<VAL> rightVal = (TypeSafeValue<VAL>) right;
        if (restriction == null) {
            restriction = new RestrictionImpl<>(group, predicate,
                    leftVal, operator, rightVal);
            if (restrictionNodeType == And) {
                group.and(restriction);
            } else {
                group.or(restriction);
            }
        } else {
            restriction.setLeft(leftVal);
            restriction.setOperator(operator);
            restriction.setRight(rightVal);
        }
        // continue with the next one assuming And, if #or() is called the instance
        // is simply discarded and one with Or is returned instead.
        return createContinuedOnGoingRestriction(And, leftVal == null ? rightVal: leftVal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED isNotNull() {
        return addRestrictionAndContinue(startValue, IS_NOT_NULL, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED in(TypeSafeValue<T> value) {
        return addRestrictionAndContinue(startValue, IN, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED in(Collection<T> values) {
        return in(values, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED in(Collection<T> values, Integer batchSize) {
        return in(values, null, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED notIn(TypeSafeValue<T> value) {
        return addRestrictionAndContinue(startValue, NOT_IN, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED notIn(Collection<T> values) {
        return notIn(values, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED notIn(Collection<T> values, Integer batchSize) {
        return notIn(values, null, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED eq(TypeSafeValue<VAL> value) {
        return addRestrictionAndContinue(startValue, EQUAL, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED eq(VAL value) {
        return eq(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED notEq(TypeSafeValue<VAL> value) {
        return addRestrictionAndContinue(startValue, NOT_EQUAL, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED notEq(VAL value) {
        return notEq(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleNamedParameterBinder<VAL, CONTINUED, ORIGINAL> eq() {
        DirectTypeSafeValue<VAL> value = createDummyDirectValue();
        return createNamedParameterBinder(value, eq(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleNamedParameterBinder<VAL, CONTINUED, ORIGINAL> notEq() {
        DirectTypeSafeValue<VAL> value = createDummyDirectValue();
        return createNamedParameterBinder(value, notEq(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionNamedParameterBinder<VAL, CONTINUED, ORIGINAL> in() {
        CollectionTypeSafeValue<VAL> value = createCollectionValue();
        return createNamedParameterBinder(value, in(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionNamedParameterBinder<VAL, CONTINUED, ORIGINAL> notIn() {
        CollectionTypeSafeValue<VAL> value = createCollectionValue();
        return createNamedParameterBinder(value, notIn(value));
    }

    /**
     * Create a new unbound collection value.
     */
    protected CollectionTypeSafeValue<VAL> createCollectionValue() {
        return new CollectionTypeSafeValue<>(group.getQuery(), getSupportedValueClass());
    }

    /**
     * Delegates to {@link TypeSafeQueryInternal#toValue(Object)}
     */
    protected TypeSafeValue<VAL> toValue(VAL value, RestrictionPredicate predicate) {
        this.predicate = predicate;
        return group.getQuery().toValue(value, this);
    }

    protected DirectTypeSafeValue<VAL> createDummyDirectValue() {
        return new DirectTypeSafeValue<>(group.getQuery(), getSupportedValueClass());
    }

    @Override
    public final DirectTypeSafeValue<VAL> createEmptyDirectValue(TypeSafeQueryInternal query) {
        if (predicate == null && group.getQuery().getDefaultRestrictionPredicate() == null) {
            throw new IllegalArgumentException("When using restrictions, don't use .eq(null), use .isNull() instead. "
                    + "An exception to this rule is when a predicate which can filter null values is used.");
        }
        return createDummyDirectValue();
    }

    @Override
    public ORIGINAL and() {
        return createOriginalOnGoingRestriction(And, startValue);
    }

    @Override
    public ORIGINAL or() {
        return createOriginalOnGoingRestriction(Or, startValue);
    }

    @Override
    public CONTINUED notEq(VAL value, RestrictionPredicate predicate) {
        return notEq(toValue(value, predicate));
    }

    @Override
    public <T extends VAL> CONTINUED notIn(Collection<T> values, RestrictionPredicate predicate) {
        return notIn(values, predicate, null);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends VAL> CONTINUED notIn(Collection<T> values, RestrictionPredicate predicate, Integer batchSize) {
        this.predicate = predicate;
        // suppressing warnings because we know T is a kind of VAL, and we won't be changing the collection internally
        return (CONTINUED) notIn(new CollectionTypeSafeValue<>(group.getQuery(), getSupportedValueClass(), (Collection) values, batchSize));
    }

    @Override
    public CONTINUED eq(VAL value, RestrictionPredicate predicate) {
        return eq(toValue(value, predicate));
    }

    @Override
    public <T extends VAL> CONTINUED in(Collection<T> values, RestrictionPredicate predicate) {
        return in(values, predicate, null);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends VAL> CONTINUED in(Collection<T> values, RestrictionPredicate predicate, Integer batchSize) {
        this.predicate = predicate;
        // suppressing warnings because we know T is a kind of VAL, and we won't be changing the collection internally
        return (CONTINUED) in(new CollectionTypeSafeValue<>(group.getQuery(), getSupportedValueClass(), (Collection) values, batchSize));
    }
}
