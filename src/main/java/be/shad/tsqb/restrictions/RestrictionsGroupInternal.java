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

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.copy.Copyable;

/**
 * Extend to include extra interfaces
 */
public interface RestrictionsGroupInternal extends RestrictionsGroup, Restriction, RestrictionChainable, Copyable {

    /**
     * Get the join, for scope testing.
     */
    TypeSafeQueryProxyData getJoin();

    /**
     * Get the query, to convert none TypeSafeValue<VAL>s to them.
     */
    TypeSafeQueryInternal getQuery();

    /**
     * @return true if the group doesn't contain restrictions
     */
    boolean isEmpty();

    /**
     * Check if any of the restrictions in this group is the given restriction.
     */
    boolean contains(Restriction restriction);

}
