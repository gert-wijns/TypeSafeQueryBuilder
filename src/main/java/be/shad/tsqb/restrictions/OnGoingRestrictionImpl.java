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
import static be.shad.tsqb.restrictions.RestrictionImpl.IN;
import static be.shad.tsqb.restrictions.RestrictionImpl.IS_NOT_NULL;
import static be.shad.tsqb.restrictions.RestrictionImpl.IS_NULL;
import static be.shad.tsqb.restrictions.RestrictionImpl.NOT_EQUAL;
import static be.shad.tsqb.restrictions.RestrictionImpl.NOT_IN;

import java.util.Collection;

import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.CollectionTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Takes a partially built restriction and provides methods complete it.
 * Type specific methods are added in subclasses of this class.
 * 
 * @see RestrictionImpl
 */
public abstract class OnGoingRestrictionImpl<VAL, CONTINUED extends ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL>, 
            ORIGINAL extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>> 
        implements OnGoingRestriction<VAL, CONTINUED, ORIGINAL> {

    protected final RestrictionImpl restriction;

    public OnGoingRestrictionImpl(RestrictionImpl restriction, VAL argument) {
        this.restriction = restriction;
        restriction.setLeft(toValue(argument));
    }

    public OnGoingRestrictionImpl(RestrictionImpl restriction, TypeSafeValue<VAL> argument) {
        this.restriction = restriction;
        restriction.setLeft(argument);
    }

    /**
     * Delegates to {@link #createContinuedOnGoingRestriction(RestrictionsGroupInternal, TypeSafeValue)}
     * withe the current restriction group and restriction left value or the right value if the left value is null.
     */
    @SuppressWarnings("unchecked")
    protected final CONTINUED createContinuedOnGoingRestriction() {
        TypeSafeValue<VAL> previous = (TypeSafeValue<VAL>) restriction.getLeft();
        if (previous == null) {
            previous = (TypeSafeValue<VAL>) restriction.getRight();
        }
        return createContinuedOnGoingRestriction(restriction.getRestrictionsGroup(), previous);
    }

    /**
     * Delegates to subclass to create the correct type.
     */
    protected abstract CONTINUED createContinuedOnGoingRestriction(
            RestrictionsGroupInternal group, TypeSafeValue<VAL> previousValue);

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED isNull() {
        restriction.setOperator(IS_NULL);
        return createContinuedOnGoingRestriction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED isNotNull() {
        restriction.setOperator(IS_NOT_NULL);
        return createContinuedOnGoingRestriction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED in(TypeSafeValue<T> value) {
        restriction.setOperator(IN);
        restriction.setRight(value);
        return createContinuedOnGoingRestriction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED in(Collection<T> values) {
        return in(new CollectionTypeSafeValue<>(restriction.getQuery(), values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED notIn(TypeSafeValue<T> value) {
        restriction.setOperator(NOT_IN);
        restriction.setRight(value);
        return createContinuedOnGoingRestriction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED notIn(Collection<T> values) {
        return notIn(new CollectionTypeSafeValue<>(restriction.getQuery(), values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED eq(TypeSafeValue<VAL> value) {
        restriction.setOperator(EQUAL);
        restriction.setRight(value);
        return createContinuedOnGoingRestriction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED eq(VAL value) {
        return eq(toValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED not(TypeSafeValue<VAL> value) {
        restriction.setOperator(NOT_EQUAL);
        restriction.setRight(value);
        return createContinuedOnGoingRestriction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED not(VAL value) {
        return not(toValue(value));
    }

    /**
     * Delegates to {@link TypeSafeQueryInternal#toValue(Object)}
     */
    protected TypeSafeValue<VAL> toValue(VAL value) {
        return restriction.getQuery().toValue(value);
    }

}
