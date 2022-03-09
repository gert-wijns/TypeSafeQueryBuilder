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

import java.time.LocalDate;
import java.util.Date;

import be.shad.tsqb.exceptions.EqualsNotAllowedException;
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

    @Override
    public RestrictionChainable andExists(TypeSafeSubQuery<?> subquery) {
        return getRestrictionsGroup().and(exists(subquery));
    }

    @Override
    public RestrictionChainable andNotExists(TypeSafeSubQuery<?> subquery) {
        return getRestrictionsGroup().and(notExists(subquery));
    }

    @Override
    public RestrictionChainable orExists(TypeSafeSubQuery<?> subquery) {
        return getRestrictionsGroup().or(exists(subquery));
    }

    @Override
    public RestrictionChainable orNotExists(TypeSafeSubQuery<?> subquery) {
        return getRestrictionsGroup().or(notExists(subquery));
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> and(E value) {
        return new OnGoingEnumRestrictionImpl<>(getRestrictionsGroup(), And, value);
    }

    @Override
    public <T> OnGoingObjectRestriction<T> and(TypeSafeValue<T> value) {
        return new OnGoingObjectRestrictionImpl<>(getRestrictionsGroup(), And, value);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> andEnum(TypeSafeValue<E> value) {
        return new OnGoingEnumRestrictionImpl<>(getRestrictionsGroup(), And, value);
    }

    @Override
    public OnGoingTextRestriction and(String value) {
        return new OnGoingTextRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    @Override
    public OnGoingTextRestriction andString(TypeSafeValue<String> value) {
        return new OnGoingTextRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    @Override
    public OnGoingBooleanRestriction and(Boolean value) {
        return new OnGoingBooleanRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    @Override
    public OnGoingBooleanRestriction andBoolean(TypeSafeValue<Boolean> value) {
        return new OnGoingBooleanRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    @Override
    public OnGoingNumberRestriction and(Number value) {
        return new OnGoingNumberRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    @Override
    public <N extends Number> OnGoingNumberRestriction andNumber(TypeSafeValue<N> value) {
        return new OnGoingNumberRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    @Override
    public OnGoingDateRestriction and(Date value) {
        return new OnGoingDateRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    @Override
    public OnGoingDateRestriction andDate(TypeSafeValue<Date> value) {
        return new OnGoingDateRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    @Override
    public OnGoingLocalDateRestriction and(LocalDate value) {
        return new OnGoingLocalDateRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    @Override
    public OnGoingLocalDateRestriction andLocalDate(TypeSafeValue<LocalDate> value) {
        return new OnGoingLocalDateRestrictionImpl(getRestrictionsGroup(), And, value);
    }

    @Override
    public <T> OnGoingObjectRestriction<T> or(TypeSafeValue<T> value) {
        return new OnGoingObjectRestrictionImpl<>(getRestrictionsGroup(), Or, value);
    }

    @Override
    public OnGoingTextRestriction or(String value) {
        return new OnGoingTextRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    @Override
    public OnGoingTextRestriction orString(TypeSafeValue<String> value) {
        return new OnGoingTextRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    @Override
    public OnGoingNumberRestriction or(Number value) {
        return new OnGoingNumberRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    @Override
    public OnGoingNumberRestriction orNumber(TypeSafeValue<Number> value) {
        return new OnGoingNumberRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    @Override
    public OnGoingBooleanRestriction or(Boolean value) {
        return new OnGoingBooleanRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    @Override
    public OnGoingDateRestriction or(Date value) {
        return new OnGoingDateRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    @Override
    public OnGoingBooleanRestriction orBoolean(TypeSafeValue<Boolean> value) {
        return new OnGoingBooleanRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    @Override
    public OnGoingDateRestriction orDate(TypeSafeValue<Date> value) {
        return new OnGoingDateRestrictionImpl(getRestrictionsGroup(), Or, value);
    }

    @Override
    public RestrictionAndChainable and(ContinuedRestrictionChainable continuedRestrictionChainable) {
        return and(continuedRestrictionChainable.getRestrictionsGroup());
    }

    @Override
    public RestrictionAndChainable or(ContinuedRestrictionChainable continuedRestrictionChainable) {
        return or(continuedRestrictionChainable.getRestrictionsGroup());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        throw EqualsNotAllowedException.create(getRestrictionsGroup().getQuery(), obj, "Restriction");
    }

}
