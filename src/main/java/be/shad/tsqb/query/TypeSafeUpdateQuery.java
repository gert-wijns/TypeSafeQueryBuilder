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

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Implementation represents an update query.
 */
public interface TypeSafeUpdateQuery extends TypeSafeBaseQuery {

    /**
     * Use nullValue when setting a property to null.
     */
    <VAL> VAL nullValue();

    /**
     * Delegates to {@link #asReference(Class, TypeSafeValue)} using query.toValue(entityId).
     */
    <E> TypeSafeValue<E> asReference(Class<E> entityClass, Object entityId);

    /**
     * Use to get a TypeSafeValue which represents the entityClass with the given ID value.
     * This can be used so object.setChildObject(query.asReference(ChildObject.class, IDrepresentattive) can be used.
     *
     * @throws IllegalArgumentException if the entityId valueClass doesn't match the entityClass ID-type.
     */
    <E> TypeSafeValue<E> asReference(Class<E> entityClass, TypeSafeValue<Object> entityIdValue);
}
