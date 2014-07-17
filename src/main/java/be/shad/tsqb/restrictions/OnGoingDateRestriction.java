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

import be.shad.tsqb.restrictions.named.SingleNamedParameterBinder;
import be.shad.tsqb.restrictions.predicate.RestrictionValuePredicate;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Exposes Date related restrictions in addition to the basic restrictions.
 */
public interface OnGoingDateRestriction extends OnGoingRestriction<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> {

    /**
     * Generates: left >= dateRepresentative
     */
    ContinuedOnGoingDateRestriction notBefore(TypeSafeValue<Date> value);

    /**
     * Generates: left >= (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction notBefore(Date value);

    /**
     * Same as {@link #notBefore(Date)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingDateRestriction notBefore(Date value, RestrictionValuePredicate predicate);
    
    /**
     * Generates: left <= dateRepresentative
     */
    ContinuedOnGoingDateRestriction notAfter(TypeSafeValue<Date> value);

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction notAfter(Date value);

    /**
     * Same as {@link #notAfter(Date)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingDateRestriction notAfter(Date value, RestrictionValuePredicate predicate);

    /**
     * Generates: left >= (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction afterOrEq(Date value);

    /**
     * Generates: left >= dateRepresentative
     */
    ContinuedOnGoingDateRestriction afterOrEq(TypeSafeValue<Date> value);

    /**
     * Same as {@link #afterOrEq(Date)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingDateRestriction afterOrEq(Date value, RestrictionValuePredicate predicate);

    /**
     * Generates: left > dateRepresentative
     */
    ContinuedOnGoingDateRestriction after(TypeSafeValue<Date> value);

    /**
     * Generates: left > (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction after(Date value);

    /**
     * Same as {@link #after(Date)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingDateRestriction after(Date value, RestrictionValuePredicate predicate);

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction beforeOrEq(Date value);

    /**
     * Same as {@link #beforeOrEq(Date)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingDateRestriction beforeOrEq(Date value, RestrictionValuePredicate predicate);

    /**
     * Generates: left <= dateRepresentative
     */
    ContinuedOnGoingDateRestriction beforeOrEq(TypeSafeValue<Date> value);

    /**
     * Generates: left < dateRepresentative
     */
    ContinuedOnGoingDateRestriction before(TypeSafeValue<Date> value);

    /**
     * Generates: left < (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction before(Date value);

    /**
     * Same as {@link #before(Date)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingDateRestriction before(Date value, RestrictionValuePredicate predicate);

    /**
     * @see #beforeOrEq(Date)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> beforeOrEq();
    
    /**
     * @see #afterOrEq(Date)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> afterOrEq();
    
    /**
     * @see #notBefore(Date)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> notBefore();

    /**
     * @see #notAfter(Date)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> notAfter();

    /**
     * @see #after(Date)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> after();

    /**
     * @see #before(Date)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> before();

}
