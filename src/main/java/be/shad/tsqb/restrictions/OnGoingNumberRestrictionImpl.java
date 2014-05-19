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

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for numbers. Number specific restrictions are added here.
 * 
 * @see OnGoingRestrictionImpl
 */
public class OnGoingNumberRestrictionImpl 
        extends OnGoingRestrictionImpl<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction> 
        implements OnGoingNumberRestriction {
    
    private final static String LESS_THAN_EQUAL = "<=";
    private final static String LESS_THAN = "<";
    private final static String GREATER_THAN = ">";
    private final static String GREATER_THAN_EQUAL = ">=";

    public OnGoingNumberRestrictionImpl(RestrictionImpl restriction, Number argument) {
        super(restriction, argument);
    }
    
    @SuppressWarnings("unchecked")
    public OnGoingNumberRestrictionImpl(RestrictionImpl restriction, TypeSafeValue<? extends Number> argument) {
        super(restriction, (TypeSafeValue<Number>) argument);
    }

    @Override
    protected ContinuedOnGoingNumberRestrictionImpl createContinuedOnGoingRestriction(
            RestrictionsGroupInternal group, TypeSafeValue<Number> previousValue) {
        return new ContinuedOnGoingNumberRestrictionImpl(group, previousValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction lt(Number value) {
        return lt(toValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction lt(TypeSafeValue<Number> value) {
        restriction.setOperator(LESS_THAN);
        restriction.setRight(value);
        return createContinuedOnGoingRestriction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction gt(Number value) {
        return gt(toValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction gt(TypeSafeValue<Number> value) {
        restriction.setOperator(GREATER_THAN);
        restriction.setRight(value);
        return createContinuedOnGoingRestriction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction lte(Number value) {
        return lte(toValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction lte(TypeSafeValue<Number> value) {
        restriction.setOperator(LESS_THAN_EQUAL);
        restriction.setRight(value);
        return createContinuedOnGoingRestriction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction gte(Number value) {
        return gte(toValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction gte(TypeSafeValue<Number> value) {
        restriction.setOperator(GREATER_THAN_EQUAL);
        restriction.setRight(value);
        return createContinuedOnGoingRestriction();
    }

}
