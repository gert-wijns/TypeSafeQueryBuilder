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

import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Delegates all calls to a new whereGroup.
 */
public class RestrictionsGroupFactoryImpl implements RestrictionsGroupFactory {
    private final TypeSafeQueryInternal query;

    public RestrictionsGroupFactoryImpl(TypeSafeQueryInternal query) {
        this.query = query;
    }

    @Override
    public RestrictionsGroup createRestrictionsGroup() {
        return new RestrictionsGroupImpl(query, null);
    }

    @Override
    public RestrictionsGroup or(RestrictionHolder restriction, RestrictionHolder... restrictions) {
        return createRestrictionsGroup().or(restriction, restrictions);
    }

    @Override
    public RestrictionsGroup and(RestrictionHolder restriction, RestrictionHolder... restrictions) {
        return createRestrictionsGroup().and(restriction, restrictions);
    }

    @Override
    public RestrictionChainable where() {
        return createRestrictionsGroup().where();
    }

    @Override
    public RestrictionChainable where(HqlQueryValue restriction) {
        return createRestrictionsGroup().where(restriction);
    }

    @Override
    public RestrictionChainable where(RestrictionsGroup group) {
        return createRestrictionsGroup().where(group);
    }

    @Override
    public RestrictionChainable where(Restriction restriction) {
        return createRestrictionsGroup().where(restriction);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> whereEnum(TypeSafeValue<E> value) {
        return createRestrictionsGroup().whereEnum(value);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> where(E value) {
        return createRestrictionsGroup().where(value);
    }

    @Override
    public OnGoingBooleanRestriction whereBoolean(TypeSafeValue<Boolean> value) {
        return createRestrictionsGroup().whereBoolean(value);
    }

    @Override
    public OnGoingBooleanRestriction where(Boolean value) {
        return createRestrictionsGroup().where(value);
    }

    @Override
    public <N extends Number> OnGoingNumberRestriction whereNumber(TypeSafeValue<N> value) {
        return createRestrictionsGroup().whereNumber(value);
    }

    @Override
    public OnGoingNumberRestriction where(Number value) {
        return createRestrictionsGroup().where(value);
    }

    @Override
    public OnGoingDateRestriction whereDate(TypeSafeValue<Date> value) {
        return createRestrictionsGroup().whereDate(value);
    }

    @Override
    public OnGoingDateRestriction where(Date value) {
        return createRestrictionsGroup().where(value);
    }

    @Override
    public OnGoingTextRestriction whereString(TypeSafeValue<String> value) {
        return createRestrictionsGroup().whereString(value);
    }

    @Override
    public OnGoingTextRestriction where(String value) {
        return createRestrictionsGroup().where(value);
    }

    @Override
    public RestrictionChainable whereExists(TypeSafeSubQuery<?> subquery) {
        return createRestrictionsGroup().whereExists(subquery);
    }

    @Override
    public RestrictionChainable whereNotExists(TypeSafeSubQuery<?> subquery) {
        return createRestrictionsGroup().whereNotExists(subquery);
    }

    @Override
    public <T> OnGoingObjectRestriction<T> where(TypeSafeValue<T> value) {
        return createRestrictionsGroup().where(value);
    }
}
