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

import be.shad.tsqb.dao.TypeSafeQueryDao;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.restrictions.HavingRestrictions;
import be.shad.tsqb.restrictions.RestrictionsGroupFactory;
import be.shad.tsqb.restrictions.WhereRestrictions;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.values.CaseTypeSafeValue;
import be.shad.tsqb.values.CustomTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;
import be.shad.tsqb.values.TypeSafeValueFunctions;
import be.shad.tsqb.values.arithmetic.ArithmeticTypeSafeValueFactory;

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
public interface TypeSafeQuery extends WhereRestrictions, HavingRestrictions {
    
    /**
     * Delegates to {@link #from(Class, String)} with name = null.
     */
    <T> T from(Class<T> fromClass);

    /**
     * Creates a proxy for the given fromClass.
     * <p>
     * Multiple calls are allowed to create from clauses with multiple entities.
     * This may be useful when the queries have no direct relation in hibernate,
     * but the relation can be expressed in the restrictions afterwards.
     * 
     * @param name when name is not null, the created proxy will be named using the given name.
     *        (Remark: this is not an hql alias! It is a tag by which the proxy can be retrieved from the query)
     */
    <T> T from(Class<T> fromClass, String name);

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
     * Delegates to {@link #join(Collection, JoinType, String)} with {@link JoinType#Inner}
     */
    <T> T join(Collection<T> anyCollection, String name);
    
    /**
     * Delegates to {@link #join(Object, JoinType)} with {@link JoinType#Inner}
     */
    <T> T join(T anyObject);

    /**
     * Delegates to {@link #join(Object, JoinType, String)} with {@link JoinType#Inner}
     */
    <T> T join(T anyObject, String name);
    
    /**
     * Delegates to {@link #join(Collection, JoinType, boolean)} with false as create.
     */
    <T> T join(Collection<T> anyCollection, JoinType joinType);
    
    /**
     * Delegates to {@link #join(Collection, JoinType, String, boolean)} with false as create.
     */
    <T> T join(Collection<T> anyCollection, JoinType joinType, String name);

    /**
     * Delegates to {@link #join(Collection, JoinType, String, boolean)} with false as create.
     */
    <T> T join(Collection<T> anyCollection, JoinType joinType, boolean createAdditionalJoin);

    /**
     * Joins an entity collection, returns a proxy of the joined entity type.
     * The method calls of the proxy will be captured to assist with the query building.
     * <p>
     * The type is fetched from hibernate.
     * 
     * @param name when name is not null, the created proxy will be named using the given name.
     *        (Remark: this is not an hql alias! It is a tag by which the proxy can be retrieved from the query)
     *        
     * @param createAdditionalJoin explicitly force the creation of an additional join.
     *        this is only useful when the same object relation needs to be joined more than once.
     *        Otherwise the existing joined proxy is reused instead.
     */
    <T> T join(Collection<T> anyCollection, JoinType joinType, String name, boolean createAdditionalJoin);
    
    /**
     * Delegates to {@link #join(Object, JoinType, boolean)} with false as create.
     */
    <T> T join(T anyObject, JoinType joinType);

    /**
     * Delegates to {@link #join(Object, JoinType, String, boolean)} with false as create.
     */
    <T> T join(T anyObject, JoinType joinType, String name);

    /**
     * Delegates to {@link #join(Object, JoinType, String, boolean)} with false as create.
     */
    <T> T join(T anyObject, JoinType joinType, boolean createAdditionalJoin);

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
    <T> T join(T anyObject, JoinType joinType, String name, boolean createAdditionalJoin);
    
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
     * Build a value using a function. Use this method to create TypeSafeValue objects fluently.
     */
    TypeSafeValueFunctions hqlFunction();
    
    /**
     * @return instance which can be used to build an arithmetic value
     */
    ArithmeticTypeSafeValueFactory getArithmeticsBuilder();
    
    /**
     * @return instance which can be used to build restriction groups
     */
    RestrictionsGroupFactory getGroupedRestrictionsBuilder();

    /**
     * Provide methods related to named objects.
     */
    TypeSafeNameds named();

    /**
     * @return a custom value, the hql will be injected into the query where the value is used.
     */
    <VAL> CustomTypeSafeValue<VAL> customValue(Class<VAL> valueClass, String hql, Object... params);
    
    /**
     * @return a case value which must be built using the is(result).when(conditions).
     */
    <VAL> CaseTypeSafeValue<VAL> caseWhenValue(Class<VAL> valueClass);
    
    /**
     * Dequeues pending invocations:
     * <ul>
     * <li>If a pending invocation exists, returns a value representing this invocation.</li>
     * <li>If no pending invocation exists, returns a direct value.</li>
     * <li>IllegalStateException when more than one pending invocation.</li>
     * </ul>
     * In general, pending invocations are added when methods of proxied entities or typesare called,
     * except when the method returns another proxy.
     * <p>
     * Using the query restrictions clause building will automatically use the toValue method behind the scenes.
     * The use of this method in custom code is probably a rare thing and is not really encouraged,
     * but there may be cases when this can be useful.
     * <p>
     * An example when this can be used externally is when the value is when grouping by a custom hibernate Type.
     * 
     * @throws IllegalStateException when more than one invocation is pending
     */
    <VAL> TypeSafeValue<VAL> toValue(VAL val);

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
