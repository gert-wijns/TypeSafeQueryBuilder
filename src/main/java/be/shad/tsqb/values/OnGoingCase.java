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
     * @see WhereRestrictions
     */
    RestrictionChainable when(HqlQueryValue restriction);

    /**
     * @see WhereRestrictions
     */
    RestrictionChainable when(RestrictionsGroup group);

    /**
     * @see WhereRestrictions
     */
    Restriction when(Restriction restriction);

    /**
     * @see WhereRestrictions
     */
    <E extends Enum<E>> OnGoingEnumRestriction<E> whenEnum(TypeSafeValue<E> value);

    /**
     * @see WhereRestrictions
     */
    <E extends Enum<E>> OnGoingEnumRestriction<E> when(E value);

    /**
     * @see WhereRestrictions
     */
    OnGoingBooleanRestriction whenBoolean(TypeSafeValue<Boolean> value);

    /**
     * @see WhereRestrictions
     */
    OnGoingBooleanRestriction when(Boolean value);

    /**
     * @see WhereRestrictions
     */
    <N extends Number> OnGoingNumberRestriction whenNumber(TypeSafeValue<N> value);

    /**
     * @see WhereRestrictions
     */
    OnGoingNumberRestriction when(Number value);

    /**
     * @see WhereRestrictions
     */
    OnGoingDateRestriction whenDate(TypeSafeValue<Date> value);

    /**
     * @see WhereRestrictions
     */
    OnGoingDateRestriction when(Date value);

    /**
     * @see WhereRestrictions
     */
    OnGoingTextRestriction whenString(TypeSafeValue<String> value);

    /**
     * @see WhereRestrictions
     */
    OnGoingTextRestriction when(String value);

    /**
     * @see WhereRestrictions
     */
    RestrictionChainable whenExists(TypeSafeSubQuery<?> subquery);
    
    /**
     * Defines the value to return in the else part of a (case when ... then ... (else ...) end).
     * The end() is implicitely called.
     */
    TypeSafeValue<T> otherwise();
    
}
