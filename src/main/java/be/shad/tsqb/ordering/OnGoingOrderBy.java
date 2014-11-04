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
package be.shad.tsqb.ordering;

import java.util.Date;

import be.shad.tsqb.values.TypeSafeValue;

public interface OnGoingOrderBy {

    /**
     * Adds the custom order by to the order bys and continues the order by chain.
     */
    OnGoingOrderBy by(OrderBy orderBy);

    /**
     * Converts to a TypesafeValue and delegates to {@link #desc(TypeSafeValue)}
     */
    OnGoingOrderBy desc(Number val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #desc(TypeSafeValue)}
     */
    OnGoingOrderBy desc(String val);

    /**
     * Wraps the val with the hql 'upper' function and delegates to {@link #desc(TypeSafeValue)}.
     */
    OnGoingOrderBy descIgnoreCase(String val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #desc(TypeSafeValue)}
     */
    OnGoingOrderBy desc(Enum<?> val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #desc(TypeSafeValue)}
     */
    OnGoingOrderBy desc(Boolean val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #desc(TypeSafeValue)}
     */
    OnGoingOrderBy desc(Date val);

    /**
     * Adds the value to the list of order bys with the notion that it is ordered descending.
     */
    OnGoingOrderBy desc(TypeSafeValue<?> val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #asc(TypeSafeValue)}
     */
    OnGoingOrderBy asc(Number val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #asc(TypeSafeValue)}
     */
    OnGoingOrderBy asc(String val);

    /**
     * Wraps the val with the hql 'upper' function and delegates to {@link #asc(TypeSafeValue)}.
     */
    OnGoingOrderBy ascIgnoreCase(String val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #asc(TypeSafeValue)}
     */
    OnGoingOrderBy asc(Enum<?> val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #asc(TypeSafeValue)}
     */
    OnGoingOrderBy asc(Boolean val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #asc(TypeSafeValue)}
     */
    OnGoingOrderBy asc(Date val);

    /**
     * Adds the value to the list of order bys with the notion that it is ordered ascending.
     */
    OnGoingOrderBy asc(TypeSafeValue<?> val);

}
