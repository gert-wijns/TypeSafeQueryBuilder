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

import be.shad.tsqb.restrictions.named.SingleNamedParameterBinder;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Exposes Number related restrictions in addition to the basic restrictions.
 */
public interface OnGoingNumberRestriction extends OnGoingRestriction<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction> {

    /**
     * Generates: left >= numberRepresentative
     */
    ContinuedOnGoingNumberRestriction gte(TypeSafeValue<Number> value);

    /**
     * Generates: left >= (referencedValue or actualValue)
     */
    ContinuedOnGoingNumberRestriction gte(Number value);

    /**
     * Same as {@link #gte(Number)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingNumberRestriction gte(Number value, RestrictionPredicate predicate);

    /**
     * Generates: left <= numberRepresentative
     */
    ContinuedOnGoingNumberRestriction lte(TypeSafeValue<Number> value);

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    ContinuedOnGoingNumberRestriction lte(Number value);

    /**
     * Same as {@link #lte(Number)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingNumberRestriction lte(Number value, RestrictionPredicate predicate);

    /**
     * Generates: left > numberRepresentative
     */
    ContinuedOnGoingNumberRestriction gt(TypeSafeValue<Number> value);

    /**
     * Generates: left > (referencedValue or actualValue)
     */
    ContinuedOnGoingNumberRestriction gt(Number value);

    /**
     * Same as {@link #gt(Number)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingNumberRestriction gt(Number value, RestrictionPredicate predicate);

    /**
     * Generates: left < numberRepresentative
     */
    ContinuedOnGoingNumberRestriction lt(TypeSafeValue<Number> value);

    /**
     * Generates: left < (referencedValue or actualValue)
     */
    ContinuedOnGoingNumberRestriction lt(Number value);

    /**
     * Same as {@link #lt(Number)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingNumberRestriction lt(Number value, RestrictionPredicate predicate);

    /**
     * Generates: left >= numberRepresentative
     */
    ContinuedOnGoingNumberRestriction nlt(TypeSafeValue<Number> value);

    /**
     * Generates: left >= (referencedValue or actualValue)
     */
    ContinuedOnGoingNumberRestriction nlt(Number value);

    /**
     * Same as {@link #nlt(Number)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingNumberRestriction nlt(Number value, RestrictionPredicate predicate);

    /**
     * Generates: left <= numberRepresentative
     */
    ContinuedOnGoingNumberRestriction ngt(TypeSafeValue<Number> value);

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    ContinuedOnGoingNumberRestriction ngt(Number value);

    /**
     * Same as {@link #ngt(Number)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingNumberRestriction ngt(Number value, RestrictionPredicate predicate);

    /**
     * Generates: left > 0
     */
    ContinuedOnGoingNumberRestriction isPositive();

    /**
     * Generates: left < 0
     */
    ContinuedOnGoingNumberRestriction isNegative();

    /**
     * Generates: left = 0
     */
    ContinuedOnGoingNumberRestriction isZero();

    /**
     * @see #nlt(Number)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction> nlt();

    /**
     * @see #ngt(Number)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction> ngt();

    /**
     * @see #gte(Number)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction> gte();

    /**
     * @see #lte(Number)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction> lte();

    /**
     * @see #gt(Number)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction> gt();

    /**
     * @see #lt(Number)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction> lt();

}
