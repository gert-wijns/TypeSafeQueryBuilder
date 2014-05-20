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

import java.util.Date;

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for dates. Date specific restrictions are added here.
 * 
 * @see OnGoingRestrictionImpl
 */
public class OnGoingDateRestrictionImpl 
        extends OnGoingRestrictionImpl<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> 
        implements OnGoingDateRestriction, ContinuedOnGoingDateRestriction {
    
    private final static String LESS_THAN_EQUAL = "<=";
    private final static String LESS_THAN = "<";
    private final static String GREATER_THAN = ">";
    private final static String GREATER_THAN_EQUAL = ">=";

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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction before(Date value) {
        return before(toValue(value));
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
    public ContinuedOnGoingDateRestriction after(Date value) {
        return after(toValue(value));
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
        return notAfter(toValue(value));
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
        return notBefore(toValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notBefore(TypeSafeValue<Date> value) {
        return addRestrictionAndContinue(startValue, GREATER_THAN_EQUAL, value);
    }

}
