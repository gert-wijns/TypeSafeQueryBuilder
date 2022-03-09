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
import java.util.Date;
import java.util.Map;

import be.shad.tsqb.dao.TypeSafeQueryDao;
import be.shad.tsqb.joins.JoinParams;
import be.shad.tsqb.joins.MapJoin;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.restrictions.HavingRestrictions;
import be.shad.tsqb.restrictions.WhereRestrictions;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * TypeSafeQuery aims to be a type safe alternative to build hql queries.
 * <p>
 * TypeSafeQuery uses proxies of the entities being queried to provide
 * the developer a way to create queries without coding property paths
 * in strings.
 * <p>
 * When methods on the entities are refactored, the queries will either
 * automatically be refactored too, or they will show compile errors.
 * This should help with the maintanability of the created queries.
 * <p>
 * A type safe query is created in two ways.
 * <ul>
 * <li> By using {@link TypeSafeQueryDao#createQuery()}: This will create a root query, which is always the
 *          starting point to create a query to return data
 * <li> By using {@link #subquery(Class)}: This can be used to create a subquery,
 *          which will select a value of the Class type.
 * </ul>
 * After a TypeSafeQuery instance was obtained, a proxy of the entity being queried is obtained by calling
 * the {@link #from(Class)} method with the entity class.
 * <p>
 * This proxies' getters can be used to join entities, build the where clause, order and group by.
 * <p>
 * For an example, see {@link TypeSafeRootQuery}.
 */
public interface TypeSafeQuery extends TypeSafeQueryJoin, TypeSafeBaseQuery, HavingRestrictions {

    /**
     * Get a new proxy for the same entity to gain access to the subtype methods
     *
     * @throws IllegalArgumentException when hibernate doesn't know the subtype
     *         or the proxy is not a TypeSafeQueryProxy.
     */
    <S, T extends S> T getAsSubtype(S proxy, Class<T> subtype) throws IllegalArgumentException;

    /**
     * All getters of proxies will be assigned to the given join type
     * when using the join methods available on TypeSafeQuery.
     * <p>
     * Example: <code>join(JoinType.Left).join(person.getParent().getAdress())</code>
     * will join both the parent and address with JoinType.Left.
     * The regular <code>join(person.getParent().getAddress(), JoinType.Left)</code>
     * would inner join the parent and left join address.
     */
    TypeSafeQueryJoin join(JoinType joinType);

    /**
     * Delegates to {@link #join(Object, Class, ClassJoinType, String)}
     * <ul>
     * <li><code>ClassJoinType</code> is defaulted to Inner.</li>
     * <li><code>String</code> is defaulted to null.</li>
     * </ul>
     */
    <T> T join(Object parent, Class<T> entityClazz);

    /**
     * Delegates to {@link #join(Object, Class, ClassJoinType, String)}
     * <ul>
     * <li>String is defaulted to null.</li>
     * </ul>
     */
    <T> T join(Object parent, Class<T> entityClazz, ClassJoinType joinType);

    /**
     * Create a class join which is to be added to the query after
     * the parent object was from'ed or joined.
     *
     * @param name when name is not null, the created proxy will be named using the given name.
     *        (Remark: this is not an hql alias! It is a tag by which the proxy can be retrieved from the query)
     */
    <T> T join(Object parent, Class<T> entityClazz, ClassJoinType joinType, String name);

    /** Adds a join for the invoked getter on a from or join proxy. */
    <T> T join(Collection<T> anyCollection, JoinParams params);
    /** Adds a join for the invoked getter on a from or join proxy. */
    <T> T join(T anyObject, JoinParams params);
    /** Adds a join for the invoked getter on a from or join proxy. */
    <K, V> MapJoin<K, V> join(Map<K, V> anyMap, JoinParams params);

    /**
     * The object must be a TypeSafeQueryProxy, this will be validated at runtime.
     *
     * @return TypeSafeQueryJoin which can be configured further for join specific configuration
     * @throws IllegalArgumentException if the object is not an entity proxy.
     */
    <T> WhereRestrictions joinWith(T obj);

    /**
     * Get the orderBy, allowing to add descending and ascending order bys.
     */
    OnGoingOrderBy orderBy();

    /**
     * Groups by the value and returns the converted type safe value
     * so that it can be used for selection purposes.
     */
    <N extends Number> TypeSafeValue<N> groupBy(N val);

    /**
     * Groups by the value and returns the converted type safe value
     * so that it can be used for selection purposes.
     */
    TypeSafeValue<String> groupBy(String val);

    /**
     * Groups by the value and returns the converted type safe value
     * so that it can be used for selection purposes.
     */
    <E extends Enum<E>> TypeSafeValue<E> groupBy(E val);

    /**
     * Groups by the value and returns the converted type safe value
     * so that it can be used for selection purposes.
     */
    TypeSafeValue<Boolean> groupBy(Boolean val);

    /**
     * Groups by the value and returns the converted type safe value
     * so that it can be used for selection purposes.
     */
    TypeSafeValue<Date> groupBy(Date val);

    /**
     * Groups by the value and returns the converted type safe value
     * so that it can be used for selection purposes.
     */
    <T> TypeSafeValue<T> groupBy(TypeSafeValue<T> val);

    /**
     * Creates a subquery which will select a value of the <code>resultClass</code>.
     */
    <T> TypeSafeSubQuery<T> subquery(Class<T> resultClass);

    /**
     * Remembers a custom hql alias for a proxy.
     * The value may not be null and must be an instance of TypeSafeQueryProxy.
     * This means it must be an object which has been obtained by building the query.
     * <p>
     * The custom hql alias must be unique for the entire query (several subqueries can not use the same custom alias).
     * <p>
     * The custom hql alias will be used as alias in the final query, so take care when choosing an alias.
     *
     * @throws IllegalArgumentException if the alias was already registered for a different proxy.
     * @throws IllegalArgumentException if the value is not a TypeSafeQueryProxy or it is null and no invocation representing a proxy was queued.
     */
    void setHqlAlias(Object value, String alias);

    /**
     * Retrieves a previously registered proxy for the given alias.
     * Returns null if no proxy was registered with the custom alias.
     */
    <T> T getByHqlAlias(String alias);

    /**
     * The predicate to use if no more specific predicate was set on the restriction.
     * May be null when not applicable.
     */
    RestrictionPredicate getDefaultRestrictionPredicate();

    /**
     * Sets what is used by {@link #getDefaultRestrictionPredicate()}
     */
    void setDefaultRestrictionPredicate(RestrictionPredicate restrictionValuePredicate);

}
