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

import static be.shad.tsqb.restrictions.RestrictionImpl.EQUAL;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for booleans, boolean specific restrictions are added here.
 * 
 * @see OnGoingRestrictionImpl
 */
public class OnGoingBooleanRestrictionImpl 
        extends OnGoingRestrictionImpl<Boolean, ContinuedOnGoingBooleanRestriction, OnGoingBooleanRestriction> 
        implements OnGoingBooleanRestriction, ContinuedOnGoingBooleanRestriction {

    public OnGoingBooleanRestrictionImpl(RestrictionsGroupInternal group, 
            RestrictionNodeType restrictionNodeType, Boolean argument) {
        super(group, restrictionNodeType, argument);
    }

    public OnGoingBooleanRestrictionImpl(RestrictionsGroupInternal group, 
            RestrictionNodeType restrictionNodeType, TypeSafeValue<Boolean> argument) {
        super(group, restrictionNodeType, argument);
    }

    @Override
    protected OnGoingBooleanRestrictionImpl createContinuedOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<Boolean> startValue) {
        return new OnGoingBooleanRestrictionImpl(group, restrictionNodeType, startValue);
    }
    
    @Override
    protected OnGoingBooleanRestriction createOriginalOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<Boolean> startValue) {
        return createContinuedOnGoingRestriction(restrictionNodeType, startValue);
    }
    
    @Override
    protected Class<Boolean> getSupportedValueClass() {
        return Boolean.class;
    }
    
    /**
     * {@inheritDoc}
     */
    public RestrictionChainable isFalse() {
        TypeSafeValue<Boolean> falseValue = new DirectTypeSafeValue<>(group.getQuery(), Boolean.FALSE);
        addRestrictionAndContinue(startValue, EQUAL, falseValue);
        return getRestrictionsGroup();
    }

    /**
     * {@inheritDoc}
     */
    public RestrictionChainable isTrue() {
        TypeSafeValue<Boolean> falseValue = new DirectTypeSafeValue<>(group.getQuery(), Boolean.TRUE);
        addRestrictionAndContinue(startValue, EQUAL, falseValue);
        return getRestrictionsGroup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingBooleanRestriction isNamed(String alias) {
        DirectTypeSafeValue<Boolean> value = new DirectTypeSafeValue<>(group.getQuery(), Boolean.class);
        ContinuedOnGoingBooleanRestriction continued = addRestrictionAndContinue(startValue, EQUAL, value);
        createNamedParameterBinder(value.getParameter(), continued).named(alias);
        return continued;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable andExists(TypeSafeSubQuery<?> subquery) {
        return isTrue().andExists(subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable orExists(TypeSafeSubQuery<?> subquery) {
        return isTrue().orExists(subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable and(HqlQueryValue restriction) {
        return isTrue().and(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable or(HqlQueryValue restriction) {
        return isTrue().or(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable and(Restriction restriction) {
        return isTrue().and(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable or(Restriction restriction) {
        return isTrue().or(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> andEnum(TypeSafeValue<E> value) {
        return isTrue().andEnum(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> and(E value) {
        return isTrue().and(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction andBoolean(TypeSafeValue<Boolean> value) {
        return isTrue().andBoolean(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction and(Boolean value) {
        return isTrue().and(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> OnGoingNumberRestriction andNumber(TypeSafeValue<N> value) {
        return isTrue().andNumber(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction and(Number value) {
        return isTrue().and(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction andDate(TypeSafeValue<Date> value) {
        return isTrue().andDate(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction and(Date value) {
        return isTrue().and(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction andString(TypeSafeValue<String> value) {
        return isTrue().andString(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction and(String value) {
        return isTrue().and(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction or(Date value) {
        return isTrue().or(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction orDate(TypeSafeValue<Date> value) {
        return isTrue().orDate(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction or(Boolean value) {
        return isTrue().or(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction orBoolean(TypeSafeValue<Boolean> value) {
        return isTrue().orBoolean(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction orNumber(TypeSafeValue<Number> value) {
        return isTrue().orNumber(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction or(Number value) {
        return isTrue().or(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction orString(TypeSafeValue<String> value) {
        return isTrue().orString(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction or(String value) {
        return isTrue().or(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionAndChainable and(RestrictionsGroup group) {
        return isTrue().and(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionAndChainable or(RestrictionsGroup group) {
        return isTrue().or(group);
    }
    
}
