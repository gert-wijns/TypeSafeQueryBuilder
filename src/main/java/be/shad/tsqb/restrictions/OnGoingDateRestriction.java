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
     * Generates: left <= dateRepresentative
     */
    ContinuedOnGoingDateRestriction notAfter(TypeSafeValue<Date> value);

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction notAfter(Date value);

    /**
     * Generates: left > dateRepresentative
     */
    ContinuedOnGoingDateRestriction after(TypeSafeValue<Date> value);

    /**
     * Generates: left > (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction after(Date value);

    /**
     * Generates: left < dateRepresentative
     */
    ContinuedOnGoingDateRestriction before(TypeSafeValue<Date> value);

    /**
     * Generates: left < (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction before(Date value);

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
