package be.shad.tsqb.query;

import java.util.Collection;

import be.shad.tsqb.grouping.OnGoingGroupBy;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.values.TypeSafeValue;

public interface TypeSafeQuery {

	<T> T from(Class<T> fromClass);

	<T> T join(Collection<T> anyCollection);
	<T> T join(T anyObject);
	<T> T join(Collection<T> anyCollection, JoinType joinType);
	<T> T join(T anyObject, JoinType joinType);
	
	OnGoingTextRestriction restrict(String value);
	OnGoingTextRestriction restrictt(TypeSafeValue<String> value);
	
	OnGoingNumberRestriction restrict(Number value);
	OnGoingNumberRestriction restrictn(TypeSafeValue<Number> value);
	
	OnGoingOrderBy order();
	
	OnGoingGroupBy groupBy(Object value);
	
	<T> TypeSafeSubQuery<T> subquery(Class<T> resultClass);
	
	HqlQuery toHqlQuery();
	
}
