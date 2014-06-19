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
package be.shad.tsqb.restrictions.named;

import java.util.Collection;

import be.shad.tsqb.restrictions.ContinuedOnGoingRestriction;
import be.shad.tsqb.restrictions.OnGoingRestriction;

public interface CollectionNamedParameterBinder<VAL, CONTINUED extends 
        ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL>, 
        ORIGINAL extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>> {

    /**
     * Creates a QueryParameter with an alias and leaves the value blank.
     */
    CONTINUED named(String alias);

    /**
     * Creates a QueryParameter with an alias and sets the value to values.
     */
    <T extends VAL> CONTINUED named(String alias, Collection<T> values);
    
}
