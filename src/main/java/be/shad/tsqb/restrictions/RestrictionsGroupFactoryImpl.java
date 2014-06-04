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
    public RestrictionsGroup or(RestrictionHolder... restrictions) {
        return query.whereGroup().or(restrictions);
    }

    @Override
    public RestrictionsGroup and(RestrictionHolder... restrictions) {
        return query.whereGroup().and(restrictions);
    }
    
    @Override
    public RestrictionChainable where() {
        return query.whereGroup().where();
    }

    @Override
    public RestrictionChainable where(HqlQueryValue restriction) {
        return query.whereGroup().where(restriction);
    }

    @Override
    public RestrictionChainable where(RestrictionsGroup group) {
        return query.whereGroup().where(group);
    }

    @Override
    public RestrictionChainable where(Restriction restriction) {
        return query.whereGroup().where(restriction);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> whereEnum(TypeSafeValue<E> value) {
        return query.whereGroup().whereEnum(value);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> where(E value) {
        return query.whereGroup().where(value);
    }

    @Override
    public OnGoingBooleanRestriction whereBoolean(TypeSafeValue<Boolean> value) {
        return query.whereGroup().whereBoolean(value);
    }

    @Override
    public OnGoingBooleanRestriction where(Boolean value) {
        return query.whereGroup().where(value);
    }

    @Override
    public <N extends Number> OnGoingNumberRestriction whereNumber(TypeSafeValue<N> value) {
        return query.whereGroup().whereNumber(value);
    }

    @Override
    public OnGoingNumberRestriction where(Number value) {
        return query.whereGroup().where(value);
    }

    @Override
    public OnGoingDateRestriction whereDate(TypeSafeValue<Date> value) {
        return query.whereGroup().whereDate(value);
    }

    @Override
    public OnGoingDateRestriction where(Date value) {
        return query.whereGroup().where(value);
    }

    @Override
    public OnGoingTextRestriction whereString(TypeSafeValue<String> value) {
        return query.whereGroup().whereString(value);
    }

    @Override
    public OnGoingTextRestriction where(String value) {
        return query.whereGroup().where(value);
    }

    @Override
    public RestrictionChainable whereExists(TypeSafeSubQuery<?> subquery) {
        return query.whereGroup().whereExists(subquery);
    }

}
