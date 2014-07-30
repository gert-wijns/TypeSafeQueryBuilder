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
import static be.shad.tsqb.restrictions.RestrictionOperator.EXISTS;
import static be.shad.tsqb.restrictions.RestrictionOperator.NOT_EXISTS;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.TypeSafeValue;

public abstract class RestrictionChainableImpl implements RestrictionChainable {
    
    protected abstract RestrictionsGroupInternal getRestrictionsGroup();

    private Restriction exists(TypeSafeSubQuery<?> subquery) {
        return new RestrictionImpl<>(getRestrictionsGroup(), null, null, EXISTS, subquery);
    }
    
    private Restriction notExists(TypeSafeSubQuery<?> subquery) {
        return new RestrictionImpl<>(getRestrictionsGroup(), null, null, NOT_EXISTS, subquery);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable andExists(TypeSafeSubQuery<?> subquery) {
        return getRestrictionsGroup().and(exists(subquery));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable andNotExists(TypeSafeSubQuery<?> subquery) {
        return getRestrictionsGroup().and(notExists(subquery));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable orExists(TypeSafeSubQuery<?> subquery) {
        return getRestrictionsGroup().or(exists(subquery));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable orNotExists(TypeSafeSubQuery<?> subquery) {
        return getRestrictionsGroup().or(notExists(subquery));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> and(E value) {
        return new OnGoingEnumRestrictionImpl<E>(getRestrictionsGroup(), And, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> andEnum(TypeSafeValue<E> value) {
        return new OnGoingEnumRestrictionImpl<E>(getRestrictionsGroup(), And, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction and(String value) {
        return new OnGoingTextRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction andString(TypeSafeValue<String> value) {
        return new OnGoingTextRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction and(Boolean value) {
        return new OnGoingBooleanRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction andBoolean(TypeSafeValue<Boolean> value) {
        return new OnGoingBooleanRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction and(Number value) {
        return new OnGoingNumberRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> OnGoingNumberRestriction andNumber(TypeSafeValue<N> value) {
        return new OnGoingNumberRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction and(Date value) {
        return new OnGoingDateRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction andDate(TypeSafeValue<Date> value) {
        return new OnGoingDateRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction or(String value) {
        return new OnGoingTextRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction orString(TypeSafeValue<String> value) {
        return new OnGoingTextRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction or(Number value) {
        return new OnGoingNumberRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction orNumber(TypeSafeValue<Number> value) {
        return new OnGoingNumberRestrictionImpl(getRestrictionsGroup(), Or, value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction or(Boolean value) {
        return new OnGoingBooleanRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction or(Date value) {
        return new OnGoingDateRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction orBoolean(TypeSafeValue<Boolean> value) {
        return new OnGoingBooleanRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction orDate(TypeSafeValue<Date> value) {
        return new OnGoingDateRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionAndChainable and(ContinuedRestrictionChainable continuedRestrictionChainable) {
        return and(continuedRestrictionChainable.getRestrictionsGroup());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionAndChainable or(ContinuedRestrictionChainable continuedRestrictionChainable) {
        return or(continuedRestrictionChainable.getRestrictionsGroup());
    }
    
}
