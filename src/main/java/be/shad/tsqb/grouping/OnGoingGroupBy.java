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
package be.shad.tsqb.grouping;

import java.util.Date;

import be.shad.tsqb.values.TypeSafeValue;

public interface OnGoingGroupBy {

    /**
     * Converts to a TypeSafeValue and delegates to {@link #and(TypeSafeValue)}.
     */
    OnGoingGroupBy and(Number val);

    /**
     * Converts to a TypeSafeValue and delegates to {@link #and(TypeSafeValue)}.
     */
    OnGoingGroupBy and(String val);

    /**
     * Converts to a TypeSafeValue and delegates to {@link #and(TypeSafeValue)}.
     */
    OnGoingGroupBy and(Enum<?> val);

    /**
     * Converts to a TypeSafeValue and delegates to {@link #and(TypeSafeValue)}.
     */
    OnGoingGroupBy and(Boolean val);

    /**
     * Converts to a TypeSafeValue and delegates to {@link #and(TypeSafeValue)}.
     */
    OnGoingGroupBy and(Date val);

    /**
     * Adds the value to the list of values to group by.
     */
    OnGoingGroupBy and(TypeSafeValue<?> val);
    
}
