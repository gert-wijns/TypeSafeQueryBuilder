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
package be.shad.tsqb.query;

import be.shad.tsqb.joins.JoinParams;

public enum JoinType implements JoinParams {
    /**
     * The entity table(s) is left joined in sql, but its fields are not
     * fetched to create an object.
     *
     * Use when selecting values instead of entities.
     */
    Left,

    /**
     * The entity table(s) is right joined in sql, but its fields are not
     * fetched to create an object.
     *
     * Use when selecting values instead of entities.
     */
    Right,

    /**
     * The entity table(s) is inner joined in sql, but its fields are not
     * fetched to create an object.
     *
     * Use when selecting values instead of entities.
     */
    Inner,

    /**
     * The entity table(s) are left joined, and its fields will be fetched
     * to create an object.
     */
    LeftFetch,

    /**
     * The entity table(s) are inner joined, and its fields will be fetched
     * to create an object.
     */
    Fetch,

    /**
     * No join is explicitly made. Use this to be able to use the foreign
     * key value in the query directly.
     * <p>
     * Instead of the query 'from A a join a.b b where b.id = ?' in which the join is
     * not actually necessary to filter on the id of B,
     * the query 'from A a where a.b.id = ?' will be generated.
     */
    None,

    /**
     * The default join type is <code>Inner</code> when more than the identity fields
     * have been used in the query. And <code>None</code> when only the identity field was used.
     */
    Default;

    @Override
    public JoinType getJoinType() {
        return this;
    }
}
