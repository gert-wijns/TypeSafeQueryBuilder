package be.shad.tsqb.query;

import java.util.Collection;
import java.util.Date;

import be.shad.tsqb.grouping.OnGoingGroupBy;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.joins.TypeSafeQueryJoin;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.restrictions.OnGoingBooleanRestriction;
import be.shad.tsqb.restrictions.OnGoingDateRestriction;
import be.shad.tsqb.restrictions.OnGoingEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryDateRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryTextRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
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
public interface TypeSafeQuery {
	
	<T> T from(Class<T> fromClass);

	<T> T join(Collection<T> anyCollection);
	<T> T join(T anyObject);
	<T> T join(Collection<T> anyCollection, JoinType joinType);
	<T> T join(T anyObject, JoinType joinType);
	
	/**
	 * The object must be a TypeSafeQueryProxy, this will be validated at runtime.
	 * @return TypeSafeQueryJoin which can be configured further for join specific configuration
	 */
	<T> TypeSafeQueryJoin<T> getJoin(T obj);
	
	RestrictionChainable where();

	/**
	 * The general restrict by enum method. Anything which represents a number
	 * can be used with this method.
	 */
	<E extends Enum<E>> OnGoingEnumRestriction<E> wheree(TypeSafeValue<E> value);

	/**
	 * The general restrict by enum method. Anything which represents a number
	 * can be used with this method.
	 */
	<E extends Enum<E>> OnGoingSubQueryEnumRestriction<E> wheree(TypeSafeSubQuery<E> value);

	/**
	 * Restrict an enum value. This can be a direct value (an actual enum value),
	 * or a value of a TypeSafeQueryProxy getter.
	 */
	<E extends Enum<E>> OnGoingEnumRestriction<E> where(E value);
	
	/**
	 * The general restrict by boolean method. Anything which represents a boolean
	 * can be used with this method.
	 */
	OnGoingBooleanRestriction whereb(TypeSafeValue<Boolean> value);

	/**
	 * Restrict a boolean value. This can be a direct value (an actual boolean),
	 * or a value of a TypeSafeQueryProxy getter. 
	 */
	OnGoingBooleanRestriction where(Boolean value);

	/**
	 * The general restrict by number method. Anything which represents a number
	 * can be used with this method.
	 */
	OnGoingNumberRestriction wheren(TypeSafeValue<Number> value);

	/**
	 * Restrict starting with a subquery, more specific than {@link #restrictn(TypeSafeValue)},
	 * it has additional restrictions only available when subquerying.
	 */
	OnGoingSubQueryNumberRestriction wheren(TypeSafeSubQuery<Number> value);

	/**
	 * Restrict a number value. This can be a direct value (an actual number),
	 * or a value of a TypeSafeQueryProxy getter. 
	 */
	OnGoingNumberRestriction where(Number value);

	/**
	 * The general restrict by date method. Anything which represents a date
	 * can be used with this method.
	 */
	OnGoingDateRestriction whered(TypeSafeValue<Date> value);

	/**
	 * Restrict starting with a subquery, more specific than {@link #restrictd(TypeSafeValue)},
	 * it has additional restrictions only available when subquerying.
	 */
	OnGoingSubQueryDateRestriction whered(TypeSafeSubQuery<Date> value);

	/**
	 * Restrict a number value. This can be a direct value (an actual date),
	 * or a value of a TypeSafeQueryProxy getter. 
	 */
	OnGoingDateRestriction where(Date value);

	/**
	 * The general restrict by string method. Anything which represents a string
	 * can be used with this method.
	 */
	OnGoingTextRestriction wheret(TypeSafeValue<String> value);

	/**
	 * Restrict starting with a subquery, more specific than {@link #restrictt(TypeSafeValue)},
	 * it has additional restrictions only available when subquerying.
	 */
	OnGoingSubQueryTextRestriction wheret(TypeSafeSubQuery<String> value);

	/**
	 * Restrict a string value. This can be a direct value (an actual string),
	 * or a value of a TypeSafeQueryProxy getter. 
	 */
	OnGoingTextRestriction where(String value);
	
	OnGoingOrderBy order();
	
	OnGoingGroupBy groupBy(Object value);
	
	<T> TypeSafeSubQuery<T> subquery(Class<T> resultClass);
	
	HqlQuery toHqlQuery();
	
}
