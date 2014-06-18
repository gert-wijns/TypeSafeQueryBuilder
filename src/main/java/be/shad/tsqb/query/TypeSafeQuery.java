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

import be.shad.tsqb.factories.TypeSafeQueryFactories;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.joins.TypeSafeQueryJoin;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.restrictions.WhereRestrictions;
import be.shad.tsqb.values.TypeSafeValue;
import be.shad.tsqb.values.TypeSafeValueFunctions;

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
 * <li> By using {@link TypeSafeQueryHelper#createQuery()}: This will create a root query, which is always the 
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
public interface TypeSafeQuery extends WhereRestrictions {
    
    /**
     * Creates a proxy for the given fromClass.
     * <p>
     * Multiple calls are allowed to create from clauses with multiple entities.
     * This may be useful when the queries have no direct relation in hibernate,
     * but the relation can be expressed in the restrictions afterwards.
     */
    <T> T from(Class<T> fromClass);

    /**
     * Get a new proxy for the same entity to gain access to the subtype methods
     * 
     * @throws IllegalArgumentException when hibernate doesn't know the subtype 
     *         or the proxy is not a TypeSafeQueryProxy.
     */
    <S, T extends S> T getAsSubtype(S proxy, Class<T> subtype) throws IllegalArgumentException;
    
    /**
     * Delegates to {@link #join(Collection, JoinType)} with {@link JoinType#Inner}
     */
    <T> T join(Collection<T> anyCollection);
    
    /**
     * Delegates to {@link #join(Object, JoinType)} with {@link JoinType#Inner}
     */
    <T> T join(T anyObject);
    
    /**
     * Delegates to {@link #join(Collection, JoinType, boolean)} with false as create.
     */
    <T> T join(Collection<T> anyCollection, JoinType joinType);
    
    /**
     * Joins an entity collection, returns a proxy of the joined entity type.
     * The method calls of the proxy will be captured to assist with the query building.
     * <p>
     * The type is fetched from hibernate.
     * 
     * @param createAdditionalJoin explicitly force the creation of an additional join.
     *        this is only useful when the same object relation needs to be joined more than once.
     *        Otherwise the existing joined proxy is reused instead.
     */
    <T> T join(Collection<T> anyCollection, JoinType joinType, boolean createAdditionalJoin);
    
    /**
     * Delegates to {@link #join(Object, JoinType, boolean)} with false as create.
     */
    <T> T join(T anyObject, JoinType joinType);

    /**
     * Joins an entity, returns a proxy of the joined entity type.
     * The method calls of the proxy will be captured to assist with the query building.
     * <p>
     * The type is fetched from hibernate.
     * 
     * @param createAdditionalJoin explicitly force the creation of an additional join.
     *        this is only useful when the same object relation needs to be joined more than once.
     *        Otherwise the existing joined proxy is reused instead.
     */
    <T> T join(T anyObject, JoinType joinType, boolean createAdditionalJoin);
    
    /**
     * The object must be a TypeSafeQueryProxy, this will be validated at runtime.
     * 
     * @return TypeSafeQueryJoin which can be configured further for join specific configuration
     * @throws IllegalArgumentException if the object is not an entity proxy.
     */
    <T> TypeSafeQueryJoin<T> getJoin(T obj);

    /**
     * Creates a subgroup for this query. This group is not added to the query
     * where until it is added using the {@link RestrictionChainable#and(be.shad.tsqb.restrictions.Restriction) and(restriction)} 
     * or the {@link RestrictionChainable#or(be.shad.tsqb.restrictions.Restriction) or(restriction)}.
     * <p>
     * The group will not be added to the existing restrictions automatically.
     * This must be done separately.
     */
    RestrictionsGroup whereGroup();
    
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
     * Build a value using a function. Use this method to create TypeSafeValue objects fluently.
     */
    TypeSafeValueFunctions function();
    
    /**
     * Retrieve the various factories available for restrictions or value building.
     */
    TypeSafeQueryFactories factories();
    
    /**
     * Converts this query to an hqlQuery. 
     * <p>
     * The hqlQuery can be used to get the hql and the 
     * params to create a hibernate query object.
     */
    HqlQuery toHqlQuery();
    
    /**
     * Remembers a custom alias for a proxy.
     * The value may not be null and must be an instance of TypeSafeQueryProxy.
     * This means it must be an object which has been obtained by building the query.
     * <p>
     * The custom alias must be unique for the entire query (several subqueries can not use the same custom alias).
     * <p>
     * The custom alias will be used as alias in the final query, so take care when choosing an alias.
     * 
     * @throws IllegalArgumentException if the alias was already registered for a different proxy.
     * @throws IllegalArgumentException if the value is not a TypeSafeQueryProxy or it is null and no invocation representing a proxy was queued.
     */
    void registerCustomAliasForProxy(Object value, String alias);
    
    /**
     * Retrieves a previously registered proxy for the given alias.
     * Returns null if no proxy was registered with the custom alias.
     */
    <T> T getProxyByCustomEntityAlias(String alias);
    
    /**
     * Sets the value of a named parameter.
     * The value will be checked with the value type required
     * by the named param, unless it is null.
     * <p>
     * paramAlias can be set by using where(entity.getName()).eq().named("paramAlias");
     * 
     * @param paramAlias must be one which was set before calling this method
     */
    void namedValue(String paramAlias, Object value);
    
}
