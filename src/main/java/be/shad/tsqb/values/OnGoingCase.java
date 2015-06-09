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
import be.shad.tsqb.restrictions.OnGoingBooleanRestriction;
import be.shad.tsqb.restrictions.OnGoingDateRestriction;
import be.shad.tsqb.restrictions.OnGoingEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingObjectRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.Restriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.restrictions.WhereRestrictions;

public interface OnGoingCase<T> {

    /**
     * Add restrictions to the when case.
     */
    RestrictionChainable when();

    /**
     * @see WhereRestrictions#where(HqlQueryValue)
     */
    RestrictionChainable when(HqlQueryValue hqlQueryvalue);

    /**
     * @see WhereRestrictions#where(RestrictionsGroup)
     */
    RestrictionChainable when(RestrictionsGroup group);

    /**
     * @see WhereRestrictions#where(Restriction)
     */
    RestrictionChainable when(Restriction restriction);

    /**
     * @see WhereRestrictions#whereEnum(TypeSafeValue)
     */
    <E extends Enum<E>> OnGoingEnumRestriction<E> whenEnum(TypeSafeValue<E> value);

    /**
     * @see WhereRestrictions#where(Enum)
     */
    <E extends Enum<E>> OnGoingEnumRestriction<E> when(E value);

    /**
     * @see WhereRestrictions#whereBoolean(TypeSafeValue)
     */
    OnGoingBooleanRestriction whenBoolean(TypeSafeValue<Boolean> value);

    /**
     * @see WhereRestrictions#where(Boolean)
     */
    OnGoingBooleanRestriction when(Boolean value);

    /**
     * @see WhereRestrictions#whereNumber(TypeSafeValue)
     */
    <N extends Number> OnGoingNumberRestriction whenNumber(TypeSafeValue<N> value);

    /**
     * @see WhereRestrictions#where(Number)
     */
    OnGoingNumberRestriction when(Number value);

    /**
     * @see WhereRestrictions#whereDate(TypeSafeValue)
     */
    OnGoingDateRestriction whenDate(TypeSafeValue<Date> value);

    /**
     * @see WhereRestrictions#where(Date)
     */
    OnGoingDateRestriction when(Date value);

    /**
     * @see WhereRestrictions#whereString(TypeSafeValue)
     */
    OnGoingTextRestriction whenString(TypeSafeValue<String> value);

    /**
     * @see WhereRestrictions#where(String)
     */
    OnGoingTextRestriction when(String value);

    /**
     * @see WhereRestrictions#whereExists(TypeSafeSubQuery)
     */
    RestrictionChainable whenExists(TypeSafeSubQuery<?> subquery);

    /**
     * @see WhereRestrictions#whereNotExists(TypeSafeSubQuery)
     */
    RestrictionChainable whenNotExists(TypeSafeSubQuery<?> subquery);

    /**
     * @see WhereRestrictions#where(TypeSafeValue)
     */
    <U> OnGoingObjectRestriction<U> when(TypeSafeValue<U> value);

    /**
     * Defines the value to return in the else part of a (case when ... then ... (else ...) end).
     * The end() is implicitely called.
     */
    TypeSafeValue<T> otherwise();

}
