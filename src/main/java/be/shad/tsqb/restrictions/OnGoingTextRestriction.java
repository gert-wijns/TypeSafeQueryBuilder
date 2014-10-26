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

import java.util.Collection;

import be.shad.tsqb.restrictions.named.SingleNamedParameterBinder;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Exposes String related restrictions in addition to the basic restrictions.
 */
public interface OnGoingTextRestriction extends OnGoingRestriction<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> {

    /**
     * @see #endsWith(String)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> endsWith();

    /**
     * @see #startsWith(String)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> startsWith();

    /**
     * @see #contains(String)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> contains();

    /**
     * Delegates to {@link #notIn(Collection)} after converting the values to a collection
     */
    ContinuedOnGoingTextRestriction notIn(String[] values);

    /**
     * Delegates to {@link #notIn(Collection, RestrictionPredicate)} after converting the values to a collection
     */
    ContinuedOnGoingTextRestriction notIn(String[] values, RestrictionPredicate predicate);

    /**
     * Delegates to {@link #in(Collection)} after converting the values to a collection
     */
    ContinuedOnGoingTextRestriction in(String[] values);

    /**
     * Delegates to {@link #in(Collection, RestrictionPredicate)} after converting the values to a collection
     */
    ContinuedOnGoingTextRestriction in(String[] values, RestrictionPredicate predicate);

    /**
     * Generates: left like ? with (? = '%value')
     */
    ContinuedOnGoingTextRestriction endsWith(String value);

    /**
     * Same as {@link #endsWith(String)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingTextRestriction endsWith(String value, RestrictionPredicate predicate);

    /**
     * Generates: left like ? with (? = 'value%')
     */
    ContinuedOnGoingTextRestriction startsWith(String value);

    /**
     * Same as {@link #startsWith(String)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingTextRestriction startsWith(String value, RestrictionPredicate predicate);
    
    /**
     * Generates: left like ? with (? = '%value%')
     */
    ContinuedOnGoingTextRestriction contains(String value);
    
    /**
     * Same as {@link #contains(String)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingTextRestriction contains(String value, RestrictionPredicate predicate);

    /**
     * Generates: left like ? with (? = 'value') (will require value to contain wildcards to be useful)
     */
    ContinuedOnGoingTextRestriction like(String value);

    /**
     * Same as {@link #like(String)}, but the restriction will only be added to the
     * resulting query when the value passes the predicate.
     */
    ContinuedOnGoingTextRestriction like(String value, RestrictionPredicate predicate);
    
    /**
     * Generates: left like stringRepresentative
     */
    ContinuedOnGoingTextRestriction like(TypeSafeValue<String> value);

}
