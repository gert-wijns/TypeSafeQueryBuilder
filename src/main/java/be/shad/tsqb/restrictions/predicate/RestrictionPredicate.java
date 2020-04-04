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
package be.shad.tsqb.restrictions.predicate;

import static be.shad.tsqb.restrictions.predicate.CompositeRestrictionValuePredicate.composite;
import be.shad.tsqb.values.TypeSafeValue;

public interface RestrictionPredicate {

    /**
     * When any value within a restriction is not applicable,
     * the restriction is not applicable.
     *
     * @return when true, the restriction is still applicable
     */
    boolean isValueApplicable(TypeSafeValue<?> value);

    /**
     * Ignores restrictions which contain direct null values (isNull excluded).
     */
    RestrictionPredicate IGNORE_NULL = new IgnoreDirectNullPredicate();

    /**
     * Ignores restrictions which contain direct string values without content.
     */
    RestrictionPredicate IGNORE_EMPTY_STRING = composite(IGNORE_NULL, new IgnoreDirectEmptyStringPredicate());

    /**
     * Ignores restriction which contains a collection which is null or empty
     */
    RestrictionPredicate IGNORE_EMPTY_COLLECTION = composite(IGNORE_NULL, new IgnoreEmptyCollectionPredicate());

    /**
     * Composite of {@link #IGNORE_NULL}, {@link #IGNORE_EMPTY_STRING}, {@link #IGNORE_EMPTY_COLLECTION}.
     */
    RestrictionPredicate IGNORE_NULL_OR_EMPTY = composite(IGNORE_NULL,
            new IgnoreDirectEmptyStringPredicate(), new IgnoreEmptyCollectionPredicate());

    /**
     * @see be.shad.tsqb.restrictions.predicate.IgnoreNeverPredicate
     */
    RestrictionPredicate IGNORE_NEVER = new IgnoreNeverPredicate();

}
