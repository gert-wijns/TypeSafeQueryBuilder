package be.shad.tsqb.query;

import java.util.Collection;

import be.shad.tsqb.grouping.OnGoingGroupBy;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.joins.TypeSafeQueryJoin;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryTextRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.values.TypeSafeValue;

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
	 * Restrict a number value. This can be a direct value (an actual string),
	 * or a value of a TypeSafeQueryProxy getter. 
	 */
	OnGoingNumberRestriction where(Number value);

	/**
	 * The general restrict by number method. Anything which represents a number
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
