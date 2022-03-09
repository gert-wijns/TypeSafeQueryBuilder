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
package be.shad.tsqb.values;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.restrictions.OnGoingBooleanRestriction;
import be.shad.tsqb.restrictions.OnGoingDateRestriction;
import be.shad.tsqb.restrictions.OnGoingEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingObjectRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.Restriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.restrictions.RestrictionChainableDelegatingImpl;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.restrictions.RestrictionsGroupInternal;

public class OnGoingCaseImpl<T> extends RestrictionChainableDelegatingImpl implements OnGoingCase<T>, Copyable {
    private final TypeSafeValue<T> value;

    public OnGoingCaseImpl(RestrictionsGroupInternal group, TypeSafeValue<T> value) {
        super(group);
        this.value = value;
    }

    /**
     * Copy constructor
     */
    protected OnGoingCaseImpl(CopyContext context, OnGoingCaseImpl<T> original) {
        super(context, original);
        this.value = context.get(original.value);
    }

    public TypeSafeValue<T> getValue() {
        return value;
    }

    @Override
    public RestrictionChainable when() {
        return getRestrictionsGroup();
    }

    @Override
    public RestrictionChainable when(HqlQueryValue restriction) {
        return and(restriction);
    }

    @Override
    public RestrictionChainable when(RestrictionsGroup group) {
        return and(group.getRestrictions());
    }

    @Override
    public RestrictionChainable when(Restriction restriction) {
        return and(restriction);
    }

    @Override
    public RestrictionChainable whenExists(TypeSafeSubQuery<?> subquery) {
        return andExists(subquery);
    }

    @Override
    public RestrictionChainable whenNotExists(TypeSafeSubQuery<?> subquery) {
        return andNotExists(subquery);
    }

    @Override
    public <U> OnGoingObjectRestriction<U> when(TypeSafeValue<U> value) {
        return and(value);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> when(E value) {
        return and(value);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> whenEnum(TypeSafeValue<E> value) {
        return andEnum(value);
    }

    @Override
    public OnGoingBooleanRestriction when(Boolean value) {
        return and(value);
    }

    @Override
    public OnGoingBooleanRestriction whenBoolean(TypeSafeValue<Boolean> value) {
        return andBoolean(value);
    }

    @Override
    public OnGoingNumberRestriction when(Number value) {
        return and(value);
    }

    @Override
    public OnGoingTextRestriction when(String value) {
        return and(value);
    }

    @Override
    public <N extends Number> OnGoingNumberRestriction whenNumber(TypeSafeValue<N> value) {
        return andNumber(value);
    }

    @Override
    public OnGoingTextRestriction whenString(TypeSafeValue<String> value) {
        return andString(value);
    }

    @Override
    public OnGoingDateRestriction when(Date value) {
        return and(value);
    }

    @Override
    public OnGoingDateRestriction whenDate(TypeSafeValue<Date> value) {
        return andDate(value);
    }

    @Override
    public TypeSafeValue<T> otherwise() {
        return value;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new OnGoingCaseImpl<>(context, this);
    }

}
