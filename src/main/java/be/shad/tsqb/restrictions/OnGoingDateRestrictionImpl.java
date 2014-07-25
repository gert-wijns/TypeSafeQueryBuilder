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

import static be.shad.tsqb.restrictions.RestrictionOperator.GREATER_THAN;
import static be.shad.tsqb.restrictions.RestrictionOperator.GREATER_THAN_EQUAL;
import static be.shad.tsqb.restrictions.RestrictionOperator.LESS_THAN;
import static be.shad.tsqb.restrictions.RestrictionOperator.LESS_THAN_EQUAL;

import java.util.Date;

import be.shad.tsqb.restrictions.named.SingleNamedParameterBinder;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for dates. Date specific restrictions are added here.
 * 
 * @see OnGoingRestrictionImpl
 */
public class OnGoingDateRestrictionImpl 
        extends OnGoingRestrictionImpl<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> 
        implements OnGoingDateRestriction, ContinuedOnGoingDateRestriction {
    
    public OnGoingDateRestrictionImpl(RestrictionsGroupInternal group, 
            RestrictionNodeType restrictionNodeType, Date argument) {
        super(group, restrictionNodeType, argument);
    }

    public OnGoingDateRestrictionImpl(RestrictionsGroupInternal group, 
            RestrictionNodeType restrictionNodeType, TypeSafeValue<Date> argument) {
        super(group, restrictionNodeType, argument);
    }
    
    @Override
    protected OnGoingDateRestrictionImpl createContinuedOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<Date> previousValue) {
        return new OnGoingDateRestrictionImpl(group, restrictionNodeType, previousValue);
    }

    @Override
    protected OnGoingDateRestriction createOriginalOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<Date> previousValue) {
        return createContinuedOnGoingRestriction(restrictionNodeType, previousValue);
    }
    
    @Override
    protected Class<Date> getSupportedValueClass() {
        return Date.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction beforeOrEq(Date value) {
        return notAfter(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction beforeOrEq(TypeSafeValue<Date> value) {
        return notAfter(value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction before(Date value) {
        return before(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction before(TypeSafeValue<Date> value) {
        return addRestrictionAndContinue(startValue, LESS_THAN, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction afterOrEq(Date value) {
        return notBefore(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction afterOrEq(TypeSafeValue<Date> value) {
        return notBefore(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction after(Date value) {
        return after(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction after(TypeSafeValue<Date> value) {
        return addRestrictionAndContinue(startValue, GREATER_THAN, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notAfter(Date value) {
        return notAfter(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notAfter(TypeSafeValue<Date> value) {
        return addRestrictionAndContinue(startValue, LESS_THAN_EQUAL, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notBefore(Date value) {
        return notBefore(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notBefore(TypeSafeValue<Date> value) {
        return addRestrictionAndContinue(startValue, GREATER_THAN_EQUAL, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> afterOrEq() {
        DirectTypeSafeValue<Date> value = createDummyDirectValue();
        return createNamedParameterBinder(value, afterOrEq(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> beforeOrEq() {
        DirectTypeSafeValue<Date> value = createDummyDirectValue();
        return createNamedParameterBinder(value, beforeOrEq(value));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> notBefore() {
        DirectTypeSafeValue<Date> value = createDummyDirectValue();
        return createNamedParameterBinder(value, notBefore(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> notAfter() {
        DirectTypeSafeValue<Date> value = createDummyDirectValue();
        return createNamedParameterBinder(value, notAfter(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> after() {
        DirectTypeSafeValue<Date> value = createDummyDirectValue();
        return createNamedParameterBinder(value, after(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> before() {
        DirectTypeSafeValue<Date> value = createDummyDirectValue();
        return createNamedParameterBinder(value, before(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notBefore(Date value, RestrictionPredicate predicate) {
        return notBefore(toValue(value, predicate));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notAfter(Date value, RestrictionPredicate predicate) {
        return notAfter(toValue(value, predicate));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction afterOrEq(Date value, RestrictionPredicate predicate) {
        return afterOrEq(toValue(value, predicate));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction after(Date value, RestrictionPredicate predicate) {
        return after(toValue(value, predicate));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction beforeOrEq(Date value, RestrictionPredicate predicate) {
        return beforeOrEq(toValue(value, predicate));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction before(Date value, RestrictionPredicate predicate) {
        return before(toValue(value, predicate));
    }

}
