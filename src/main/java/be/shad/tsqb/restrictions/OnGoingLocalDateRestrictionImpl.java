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

import java.time.LocalDate;

import be.shad.tsqb.restrictions.named.SingleNamedParameterBinder;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for local dates. LocalDate specific restrictions are added here.
 *
 * @see OnGoingRestrictionImpl
 */
public class OnGoingLocalDateRestrictionImpl
        extends OnGoingRestrictionImpl<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction>
        implements OnGoingLocalDateRestriction, ContinuedOnGoingLocalDateRestriction {

    public OnGoingLocalDateRestrictionImpl(RestrictionsGroupInternal group,
                                           RestrictionNodeType restrictionNodeType, LocalDate argument) {
        super(group, restrictionNodeType, argument);
    }

    public OnGoingLocalDateRestrictionImpl(RestrictionsGroupInternal group,
                                           RestrictionNodeType restrictionNodeType, TypeSafeValue<LocalDate> argument) {
        super(group, restrictionNodeType, argument);
    }

    @Override
    protected OnGoingLocalDateRestrictionImpl createContinuedOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<LocalDate> previousValue) {
        return new OnGoingLocalDateRestrictionImpl(group, restrictionNodeType, previousValue);
    }

    @Override
    protected OnGoingLocalDateRestriction createOriginalOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<LocalDate> previousValue) {
        return createContinuedOnGoingRestriction(restrictionNodeType, previousValue);
    }

    @Override
    protected Class<LocalDate> getSupportedValueClass() {
        return LocalDate.class;
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction beforeOrEq(LocalDate value) {
        return notAfter(value);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction beforeOrEq(TypeSafeValue<LocalDate> value) {
        return notAfter(value);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction before(LocalDate value) {
        return before(value, null);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction before(TypeSafeValue<LocalDate> value) {
        return addRestrictionAndContinue(startValue, LESS_THAN, value);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction afterOrEq(LocalDate value) {
        return notBefore(value);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction afterOrEq(TypeSafeValue<LocalDate> value) {
        return notBefore(value);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction after(LocalDate value) {
        return after(value, null);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction after(TypeSafeValue<LocalDate> value) {
        return addRestrictionAndContinue(startValue, GREATER_THAN, value);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction notAfter(LocalDate value) {
        return notAfter(value, null);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction notAfter(TypeSafeValue<LocalDate> value) {
        return addRestrictionAndContinue(startValue, LESS_THAN_EQUAL, value);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction notBefore(LocalDate value) {
        return notBefore(value, null);
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction notBefore(TypeSafeValue<LocalDate> value) {
        return addRestrictionAndContinue(startValue, GREATER_THAN_EQUAL, value);
    }

    @Override
    public SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> afterOrEq() {
        DirectTypeSafeValue<LocalDate> value = createDummyDirectValue();
        return createNamedParameterBinder(value, afterOrEq(value));
    }

    @Override
    public SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> beforeOrEq() {
        DirectTypeSafeValue<LocalDate> value = createDummyDirectValue();
        return createNamedParameterBinder(value, beforeOrEq(value));
    }

    @Override
    public SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> notBefore() {
        DirectTypeSafeValue<LocalDate> value = createDummyDirectValue();
        return createNamedParameterBinder(value, notBefore(value));
    }

    @Override
    public SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> notAfter() {
        DirectTypeSafeValue<LocalDate> value = createDummyDirectValue();
        return createNamedParameterBinder(value, notAfter(value));
    }

    @Override
    public SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> after() {
        DirectTypeSafeValue<LocalDate> value = createDummyDirectValue();
        return createNamedParameterBinder(value, after(value));
    }

    @Override
    public SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> before() {
        DirectTypeSafeValue<LocalDate> value = createDummyDirectValue();
        return createNamedParameterBinder(value, before(value));
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction notBefore(LocalDate value, RestrictionPredicate predicate) {
        return notBefore(toValue(value, predicate));
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction notAfter(LocalDate value, RestrictionPredicate predicate) {
        return notAfter(toValue(value, predicate));
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction afterOrEq(LocalDate value, RestrictionPredicate predicate) {
        return afterOrEq(toValue(value, predicate));
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction after(LocalDate value, RestrictionPredicate predicate) {
        return after(toValue(value, predicate));
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction beforeOrEq(LocalDate value, RestrictionPredicate predicate) {
        return beforeOrEq(toValue(value, predicate));
    }

    @Override
    public ContinuedOnGoingLocalDateRestriction before(LocalDate value, RestrictionPredicate predicate) {
        return before(toValue(value, predicate));
    }

}
