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

import be.shad.tsqb.restrictions.named.CollectionNamedParameterBinder;
import be.shad.tsqb.restrictions.named.SingleNamedParameterBinder;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Exposes the basic restrictions available for all value types.
 */
public interface OnGoingRestriction<VAL, CONTINUED extends ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL>, 
        ORIGINAL extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>> {
    
    /**
     * @see #not(VAL)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<VAL, CONTINUED, ORIGINAL> notEq();

    /**
     * @see #eq(VAL)
     * @return binder with a method to set an alias for the parameter
     */
    SingleNamedParameterBinder<VAL, CONTINUED, ORIGINAL> eq();
    
    /**
     * @see #notIn(Collection)
     * @return binder with a method to set an alias for the parameter
     */
    CollectionNamedParameterBinder<VAL, CONTINUED, ORIGINAL> notIn();
    
    /**
     * @see #in(Collection)
     * @return binder with a method to set an alias for the parameter
     */
    CollectionNamedParameterBinder<VAL, CONTINUED, ORIGINAL> in();
    
    /**
     * Generates: left <> (referencedValue or actualValue)
     */
    CONTINUED not(VAL value);

    /**
     * Generates: left <> valueRepresentative
     */
    CONTINUED notEq(TypeSafeValue<VAL> value);

    /**
     * Generates: left = (referencedValue or actualValue)
     */
    CONTINUED eq(VAL value);

    /**
     * Generates: left = valueRepresentative
     */
    CONTINUED eq(TypeSafeValue<VAL> value);

    /**
     * Generates: left not in ( actualValues )
     */
    <T extends VAL> CONTINUED notIn(Collection<T> values);

    /**
     * Generates: left not in ( valuesRepresentative )
     * <p>
     * Can be used with a TypeSafeSubQuery to check if
     * the left part is not in the subquery results.
     */
    <T extends VAL> CONTINUED notIn(TypeSafeValue<T> value);

    /**
     * Generates: left not in ( actualValues )
     */
    <T extends VAL> CONTINUED in(Collection<T> values);

    /**
     * Generates: left in ( valuesRepresentative )
     * <p>
     * Can be used with a TypeSafeSubQuery to check if
     * the left part is in the subquery results.
     */
    <T extends VAL> CONTINUED in(TypeSafeValue<T> value);

    /**
     * Generates: left is not null
     */
    CONTINUED isNotNull();

    /**
     * Generates: left is null
     */
    CONTINUED isNull();

}
