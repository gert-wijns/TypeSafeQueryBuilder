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

import static be.shad.tsqb.restrictions.RestrictionImpl.EXISTS;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.TypeSafeValue;

public abstract class RestrictionChainableImpl implements RestrictionChainable, RestrictionProvider {

    private RestrictionChainable exists(RestrictionImpl restriction, TypeSafeSubQuery<?> subquery) {
        restriction.setRight(subquery);
        restriction.setLeft(null);
        restriction.setOperator(EXISTS);
        return restriction;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable andExists(TypeSafeSubQuery<?> subquery) {
        return exists(and(), subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable orExists(TypeSafeSubQuery<?> subquery) {
        return exists(or(), subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> and(E value) {
        return new OnGoingEnumRestriction<E>(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> andEnum(TypeSafeValue<E> value) {
        return new OnGoingEnumRestriction<E>(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction and(String value) {
        return new OnGoingTextRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction andString(TypeSafeValue<String> value) {
        return new OnGoingTextRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction and(Boolean value) {
        return new OnGoingBooleanRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction andBoolean(TypeSafeValue<Boolean> value) {
        return new OnGoingBooleanRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction and(Number value) {
        return new OnGoingNumberRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction andNumber(TypeSafeValue<Number> value) {
        return new OnGoingNumberRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction and(Date value) {
        return new OnGoingDateRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction andDate(TypeSafeValue<Date> value) {
        return new OnGoingDateRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction or(String value) {
        return new OnGoingTextRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction orString(TypeSafeValue<String> value) {
        return new OnGoingTextRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction or(Number value) {
        return new OnGoingNumberRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction orNumber(TypeSafeValue<Number> value) {
        return new OnGoingNumberRestriction(or(), value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction or(Boolean value) {
        return new OnGoingBooleanRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction or(Date value) {
        return new OnGoingDateRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction orBoolean(TypeSafeValue<Boolean> value) {
        return new OnGoingBooleanRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction orDate(TypeSafeValue<Date> value) {
        return new OnGoingDateRestriction(or(), value);
    }
    
}
