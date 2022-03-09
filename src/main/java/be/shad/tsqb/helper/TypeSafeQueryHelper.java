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
package be.shad.tsqb.helper;

import java.util.Collection;

import org.hibernate.query.Query;

import be.shad.tsqb.NamedParameter;
import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupInternal;
import be.shad.tsqb.values.TypeSafeValue;

public interface TypeSafeQueryHelper {

    /**
     * Retrieves the entity name from hibernate. Used to construct the from clause.
     */
    String getEntityName(Class<?> entityClass);

    /**
     * Check if the clazz is representing an entity.
     */
    boolean isEntity(Class<?> clazz);

    /**
     * Uses the hibernate metadata to determine the ID class for the given entityClass.
     */
    Class<?> getEntityIdClass(Class<?> entityClass);

    /**
     * Uses the hibernate metadata to get the ID of the given entity
     */
    Object getIdentifier(Object entity);

    /**
     * Uses the type safe query factory and adds method handling to delegate
     * calls to the given query.
     */
    <T> T createTypeSafeFromProxy(TypeSafeQueryInternal query, Class<T> clazz);

    /**
     * Get a new proxy for the same entity to gain access to the subtype methods
     *
     * @throws IllegalArgumentException when hibernate doesn't know the subtype
     *         or the proxy is not a TypeSafeQueryProxy.
     */
    <S, T extends S> T createTypeSafeSubtypeProxy(TypeSafeQueryInternal query, S proxy, Class<T> subtype) throws IllegalArgumentException;

    /**
     * Uses the type safe query factory and adds method handling to delegate
     * calls to the given query.
     */
    <SB, SR> SB createTypeSafeSelectProxy(TypeSafeRootQueryInternal query,
            Class<SB> clazz, TypeSafeQuerySelectionGroupInternal<SB, SR> group);

    /**
     * Creates a proxy, adds it to the query' dataTree and sets its method listener.
     */
    TypeSafeQueryProxyData createTypeSafeJoinProxy(TypeSafeQueryInternal query,
            TypeSafeQueryProxyData parent, String propertyName, Class<?> targetClass);

    /**
     * Detemines the property on the child entity which references the parent entity.
     */
    String getMappedByProperty(TypeSafeQueryProxyData child);

    /**
     * Return function which can resolve an interface class to a concrete class.
     */
    ConcreteDtoClassResolver getConcreteDtoClassResolver();

    /**
     * Convert a value to a string. This is only used when hibernate would fail if params are used.
     * <p>
     * Uses the hibernate Type object to convert to a literal.
     */
    String toLiteral(Object value);

    /**
     * Uses the type resolver to get the name of the type for the given class.
     * Example: class <code>java.lang.Long</code> will resolve to <code>long</code>.
     */
    String getResolvedTypeName(Class<?> javaType);

    /**
     * return a random value, (but take primitives into account to prevent NPEs)
     */
    <T> T getDummyValue(Class<T> clazz);

    /**
     * Create a CollectionTypeSafeValue for the given query
     */
    <VAL> TypeSafeValue<VAL> createCollectionTypeSafeValue(TypeSafeQueryInternal query,
            Class<VAL> supportedValueClass, Collection<VAL> values, Integer batchSize);

    /**
     * Set the param on the query based on the kind of named param.
     */
    void bindNamedParameter(Query<Object[]> query, NamedParameter param);

    /**
     * Creates a builder spec containing all info about the builder class.
     */
    <SB, SR> SelectionBuilderSpec<SB, SR> createSelectionBuilderSpec(Class<SB> selectionBuilderClass);

    /**
     * Transforms the query to the sql query with parameters included (if logged, this query
     * can just be executed using copy/paste in a db query tool).
     */
	String toFormattedSqlQuery(TypeSafeRootQuery query);
}
