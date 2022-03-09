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

import be.shad.tsqb.restrictions.named.SingleNamedParameterBinder;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.values.TypeSafeValue;

public interface OnGoingLocalDateRestriction extends OnGoingRestriction<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> {

	/**
	 * Generates: left >= dateRepresentative
	 */
	ContinuedOnGoingLocalDateRestriction notBefore(TypeSafeValue<LocalDate> value);

	/**
	 * Generates: left >= (referencedValue or actualValue)
	 */
	ContinuedOnGoingLocalDateRestriction notBefore(LocalDate value);

	/**
	 * Same as {@link #notBefore(LocalDate)}, but the restriction will only be added to the
	 * resulting query when the value passes the predicate.
	 */
	ContinuedOnGoingLocalDateRestriction notBefore(LocalDate value, RestrictionPredicate predicate);

	/**
	 * Generates: left <= dateRepresentative
	 */
	ContinuedOnGoingLocalDateRestriction notAfter(TypeSafeValue<LocalDate> value);

	/**
	 * Generates: left <= (referencedValue or actualValue)
	 */
	ContinuedOnGoingLocalDateRestriction notAfter(LocalDate value);

	/**
	 * Same as {@link #notAfter(LocalDate)}, but the restriction will only be added to the
	 * resulting query when the value passes the predicate.
	 */
	ContinuedOnGoingLocalDateRestriction notAfter(LocalDate value, RestrictionPredicate predicate);

	/**
	 * Generates: left >= (referencedValue or actualValue)
	 */
	ContinuedOnGoingLocalDateRestriction afterOrEq(LocalDate value);

	/**
	 * Generates: left >= dateRepresentative
	 */
	ContinuedOnGoingLocalDateRestriction afterOrEq(TypeSafeValue<LocalDate> value);

	/**
	 * Same as {@link #afterOrEq(LocalDate)}, but the restriction will only be added to the
	 * resulting query when the value passes the predicate.
	 */
	ContinuedOnGoingLocalDateRestriction afterOrEq(LocalDate value, RestrictionPredicate predicate);

	/**
	 * Generates: left > dateRepresentative
	 */
	ContinuedOnGoingLocalDateRestriction after(TypeSafeValue<LocalDate> value);

	/**
	 * Generates: left > (referencedValue or actualValue)
	 */
	ContinuedOnGoingLocalDateRestriction after(LocalDate value);

	/**
	 * Same as {@link #after(LocalDate)}, but the restriction will only be added to the
	 * resulting query when the value passes the predicate.
	 */
	ContinuedOnGoingLocalDateRestriction after(LocalDate value, RestrictionPredicate predicate);

	/**
	 * Generates: left <= (referencedValue or actualValue)
	 */
	ContinuedOnGoingLocalDateRestriction beforeOrEq(LocalDate value);

	/**
	 * Same as {@link #beforeOrEq(LocalDate)}, but the restriction will only be added to the
	 * resulting query when the value passes the predicate.
	 */
	ContinuedOnGoingLocalDateRestriction beforeOrEq(LocalDate value, RestrictionPredicate predicate);

	/**
	 * Generates: left <= dateRepresentative
	 */
	ContinuedOnGoingLocalDateRestriction beforeOrEq(TypeSafeValue<LocalDate> value);

	/**
	 * Generates: left < dateRepresentative
	 */
	ContinuedOnGoingLocalDateRestriction before(TypeSafeValue<LocalDate> value);

	/**
	 * Generates: left < (referencedValue or actualValue)
	 */
	ContinuedOnGoingLocalDateRestriction before(LocalDate value);

	/**
	 * Same as {@link #before(LocalDate)}, but the restriction will only be added to the
	 * resulting query when the value passes the predicate.
	 */
	ContinuedOnGoingLocalDateRestriction before(LocalDate value, RestrictionPredicate predicate);

	/**
	 * @see #beforeOrEq(LocalDate)
	 * @return binder with a method to set an alias for the parameter
	 */
	SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> beforeOrEq();

	/**
	 * @see #afterOrEq(LocalDate)
	 * @return binder with a method to set an alias for the parameter
	 */
	SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> afterOrEq();

	/**
	 * @see #notBefore(LocalDate)
	 * @return binder with a method to set an alias for the parameter
	 */
	SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> notBefore();

	/**
	 * @see #notAfter(LocalDate)
	 * @return binder with a method to set an alias for the parameter
	 */
	SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> notAfter();

	/**
	 * @see #after(LocalDate)
	 * @return binder with a method to set an alias for the parameter
	 */
	SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> after();

	/**
	 * @see #before(LocalDate)
	 * @return binder with a method to set an alias for the parameter
	 */
	SingleNamedParameterBinder<LocalDate, ContinuedOnGoingLocalDateRestriction, OnGoingLocalDateRestriction> before();

}
