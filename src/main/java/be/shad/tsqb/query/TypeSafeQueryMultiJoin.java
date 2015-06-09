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

import java.util.Collection;

/**
 * This class is used to distinguish between direct calls
 * to the TypeSafeQuery from calls to join after using join(JoinType).
 * <p>
 * This is not needed for correct behavior, but it is needed
 * for better validation to prevent users from writing bugs.
 */
final class TypeSafeQueryMultiJoin implements TypeSafeQueryJoin {
    private final TypeSafeQueryInternal query;
    private final JoinType joinType;
    private boolean joined;
    
    TypeSafeQueryMultiJoin(TypeSafeQueryInternal query, JoinType joinType) {
        this.query = query;
        this.joinType = joinType;
        this.joined = false;
    }

    private void validateAndResetMultiJoinType() {
        if (joined) {
            throw new IllegalStateException("Illegal use of the query.join(JoinType) method. "
                    + "The returned object cannot be kept around to be used multiple times.");
        }
        this.joined = true;
        query.resetActiveMultiJoinType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T join(Collection<T> anyCollection) {
        validateAndResetMultiJoinType();
        return query.join(anyCollection, joinType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T join(Collection<T> anyCollection, String name) {
        validateAndResetMultiJoinType();
        return query.join(anyCollection, joinType, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T join(T anyObject) {
        validateAndResetMultiJoinType();
        return query.join(anyObject, joinType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T join(T anyObject, String name) {
        validateAndResetMultiJoinType();
        return query.join(anyObject, joinType, name);
    }

}
