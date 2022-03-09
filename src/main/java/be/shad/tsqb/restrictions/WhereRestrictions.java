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

import java.time.LocalDate;
import java.util.Date;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

public interface WhereRestrictions {

    /**
     * Adds the restrictions as 'and' to the where restrictions
     */
    RestrictionChainable and(RestrictionHolder restriction, RestrictionHolder... restrictions);

    /**
     * Adds the restrictions as 'and' to the where restrictions
     */
    RestrictionChainable or(RestrictionHolder restriction, RestrictionHolder... restrictions);

    /**
     * In case part of the restrictions were already built, but no reference was kept
     * to the last restriction chainable, this method can be used to get the last
     * one to be able to continue with 'or' on an already partially built restrictions group.
     */
    RestrictionChainable where();

    /**
     * Adds a custom restriction as and to the existing where clause.
     */
    RestrictionChainable where(HqlQueryValue hqlQueryvalue);

    /**
     * Adds a restriction group as and to the existing where clause.
     */
    RestrictionChainable where(RestrictionsGroup group);

    /**
     * Adds a restriction as and to the existing where clause.
     */
    RestrictionChainable where(Restriction restriction);

    /**
     * The general restrict by enum method. Anything which represents a number
     * can be used with this method.
     */
    <E extends Enum<E>> OnGoingEnumRestriction<E> whereEnum(TypeSafeValue<E> value);

    /**
     * Restrict an enum value. This can be a direct value (an actual enum value),
     * or a value of a TypeSafeQueryProxy getter.
     */
    <E extends Enum<E>> OnGoingEnumRestriction<E> where(E value);

    /**
     * The general restrict by boolean method. Anything which represents a boolean
     * can be used with this method.
     */
    OnGoingBooleanRestriction whereBoolean(TypeSafeValue<Boolean> value);

    /**
     * Restrict a boolean value. This can be a direct value (an actual boolean),
     * or a value of a TypeSafeQueryProxy getter.
     */
    OnGoingBooleanRestriction where(Boolean value);

    /**
     * The general restrict by number method. Anything which represents a number
     * can be used with this method.
     */
    <N extends Number> OnGoingNumberRestriction whereNumber(TypeSafeValue<N> value);

    /**
     * Restrict a number value. This can be a direct value (an actual number),
     * or a value of a TypeSafeQueryProxy getter.
     */
    OnGoingNumberRestriction where(Number value);

    /**
     * The general restrict by date method. Anything which represents a date
     * can be used with this method.
     */
    OnGoingDateRestriction whereDate(TypeSafeValue<Date> value);

    /**
     * Restrict a number value. This can be a direct value (an actual date),
     * or a value of a TypeSafeQueryProxy getter.
     */
    OnGoingDateRestriction where(Date value);

    /**
     * The general restrict by LocalDate method. Anything which represents a LocalDate
     * can be used with this method.
     */
    OnGoingLocalDateRestriction whereLocalDate(TypeSafeValue<LocalDate> value);

    /**
     * Restrict a local date value. This can be a direct value (an actual LocalDate),
     * or a value of a TypeSafeQueryProxy getter.
     */
    OnGoingLocalDateRestriction where(LocalDate value);

    /**
     * The general restrict by string method. Anything which represents a string
     * can be used with this method.
     */
    OnGoingTextRestriction whereString(TypeSafeValue<String> value);

    /**
     * Restrict a string value. This can be a direct value (an actual string),
     * or a value of a TypeSafeQueryProxy getter.
     */
    OnGoingTextRestriction where(String value);

    /**
     * Adds an exists restriction.
     */
    RestrictionChainable whereExists(TypeSafeSubQuery<?> subquery);

    /**
     * Adds a not exists restriction.
     */
    RestrictionChainable whereNotExists(TypeSafeSubQuery<?> subquery);

    /**
     * Restrict an object value. This can be a direct value (an actual enum value),
     * or a value of a TypeSafeQueryProxy getter.
     */
    <T> OnGoingObjectRestriction<T> where(TypeSafeValue<T> value);
}
