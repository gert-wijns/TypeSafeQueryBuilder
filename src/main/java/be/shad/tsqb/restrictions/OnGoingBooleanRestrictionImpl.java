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

import static be.shad.tsqb.restrictions.RestrictionOperator.EQUAL;

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
    private final boolean isContinued;

    public OnGoingBooleanRestrictionImpl(RestrictionsGroupInternal group,
            RestrictionNodeType restrictionNodeType, Boolean argument) {
        super(group, restrictionNodeType, argument);
        this.isContinued = false;
    }

    public OnGoingBooleanRestrictionImpl(RestrictionsGroupInternal group,
            RestrictionNodeType restrictionNodeType, TypeSafeValue<Boolean> argument) {
        this(group, restrictionNodeType, false, argument);
    }

    private OnGoingBooleanRestrictionImpl(RestrictionsGroupInternal group,
            RestrictionNodeType restrictionNodeType,
            boolean isContinued, TypeSafeValue<Boolean> startValue) {
        super(group, restrictionNodeType, startValue);
        this.isContinued = isContinued;
    }

    @Override
    protected OnGoingBooleanRestrictionImpl createContinuedOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<Boolean> startValue) {
        return new OnGoingBooleanRestrictionImpl(group, restrictionNodeType, true, startValue);
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

    @Override
    public RestrictionChainable isFalse() {
        TypeSafeValue<Boolean> falseValue = new DirectTypeSafeValue<>(group.getQuery(), Boolean.FALSE);
        addRestrictionAndContinue(startValue, EQUAL, falseValue);
        return getRestrictionsGroup();
    }

    @Override
    public RestrictionChainable isTrue() {
        TypeSafeValue<Boolean> falseValue = new DirectTypeSafeValue<>(group.getQuery(), Boolean.TRUE);
        addRestrictionAndContinue(startValue, EQUAL, falseValue);
        return getRestrictionsGroup();
    }

    @Override
    public ContinuedOnGoingBooleanRestriction isNamed(String alias) {
        DirectTypeSafeValue<Boolean> value = new DirectTypeSafeValue<>(group.getQuery(), Boolean.class);
        ContinuedOnGoingBooleanRestriction continued = addRestrictionAndContinue(startValue, EQUAL, value);
        createNamedParameterBinder(value, continued).named(alias);
        return continued;
    }

    /**
     * When automatic true, (restriction is continued without providing a value),
     * then only actually add as true. Otherwise skip automatic true.
     */
    private RestrictionChainable isAutomaticTrue() {
        if (isContinued) {
            return getRestrictionsGroup();
        }
        return isTrue();
    }

    @Override
    public RestrictionChainable andExists(TypeSafeSubQuery<?> subquery) {
        return isAutomaticTrue().andExists(subquery);
    }

    @Override
    public RestrictionChainable orExists(TypeSafeSubQuery<?> subquery) {
        return isAutomaticTrue().orExists(subquery);
    }

    @Override
    public RestrictionChainable and(HqlQueryValue restriction) {
        return isAutomaticTrue().and(restriction);
    }

    @Override
    public RestrictionChainable or(HqlQueryValue restriction) {
        return isAutomaticTrue().or(restriction);
    }

    @Override
    public RestrictionChainable and(Restriction restriction) {
        return isAutomaticTrue().and(restriction);
    }

    @Override
    public RestrictionChainable or(Restriction restriction) {
        return isAutomaticTrue().or(restriction);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> andEnum(TypeSafeValue<E> value) {
        return isAutomaticTrue().andEnum(value);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> and(E value) {
        return isAutomaticTrue().and(value);
    }

    @Override
    public OnGoingBooleanRestriction andBoolean(TypeSafeValue<Boolean> value) {
        return isAutomaticTrue().andBoolean(value);
    }

    @Override
    public OnGoingBooleanRestriction and(Boolean value) {
        return isAutomaticTrue().and(value);
    }

    @Override
    public <N extends Number> OnGoingNumberRestriction andNumber(TypeSafeValue<N> value) {
        return isAutomaticTrue().andNumber(value);
    }

    @Override
    public OnGoingNumberRestriction and(Number value) {
        return isAutomaticTrue().and(value);
    }

    @Override
    public OnGoingDateRestriction andDate(TypeSafeValue<Date> value) {
        return isAutomaticTrue().andDate(value);
    }

    @Override
    public OnGoingDateRestriction and(Date value) {
        return isAutomaticTrue().and(value);
    }

    @Override
    public OnGoingTextRestriction andString(TypeSafeValue<String> value) {
        return isAutomaticTrue().andString(value);
    }

    @Override
    public OnGoingTextRestriction and(String value) {
        return isAutomaticTrue().and(value);
    }

    @Override
    public OnGoingDateRestriction or(Date value) {
        return isAutomaticTrue().or(value);
    }

    @Override
    public OnGoingDateRestriction orDate(TypeSafeValue<Date> value) {
        return isAutomaticTrue().orDate(value);
    }

    @Override
    public OnGoingBooleanRestriction or(Boolean value) {
        return isAutomaticTrue().or(value);
    }

    @Override
    public OnGoingBooleanRestriction orBoolean(TypeSafeValue<Boolean> value) {
        return isAutomaticTrue().orBoolean(value);
    }

    @Override
    public OnGoingNumberRestriction orNumber(TypeSafeValue<Number> value) {
        return isAutomaticTrue().orNumber(value);
    }

    @Override
    public OnGoingNumberRestriction or(Number value) {
        return isAutomaticTrue().or(value);
    }

    @Override
    public OnGoingTextRestriction orString(TypeSafeValue<String> value) {
        return isAutomaticTrue().orString(value);
    }

    @Override
    public OnGoingTextRestriction or(String value) {
        return isAutomaticTrue().or(value);
    }

    @Override
    public RestrictionAndChainable and(RestrictionsGroup group) {
        return isAutomaticTrue().and(group);
    }

    @Override
    public RestrictionAndChainable or(RestrictionsGroup group) {
        return isAutomaticTrue().or(group);
    }

}
