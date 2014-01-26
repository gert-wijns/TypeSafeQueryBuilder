package be.shad.tsqb.query;

import java.util.List;

import be.shad.tsqb.grouping.TypeSafeQueryGroupBys;
import be.shad.tsqb.ordering.TypeSafeQueryOrderBys;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQueryProxyData;
import be.shad.tsqb.restrictions.TypeSafeQueryRestrictions;

public interface TypeSafeQueryInternal {

	/**
	 * @return the parent in case this is a subquery.
	 */
	TypeSafeRootQueryInternal getRootQuery();
	
	/**
	 * Clears the queue and returns all pending invocations.
	 */
	List<TypeSafeQueryProxyData> dequeueInvocations();
	
	/**
	 * Same as dequeueInvocations, but immediately validates
	 * that there are no or only one pending invocations.
	 */
	TypeSafeQueryProxyData dequeueInvocation();

	/**
	 * Enqueues an invocation.
	 */
	void invocationWasMade(TypeSafeQueryProxyData data);
	
	/**
	 * Generates a new entity alias.
	 */
	String createEntityAlias();
	
	/**
	 * Checks if the data is available in the query or one of its parents.
	 */
	boolean isInScope(TypeSafeQueryProxyData data);
	
	/**
	 * @return the known restrictions for this query.
	 */
	TypeSafeQueryRestrictions getRestrictions();
	
	/**
	 * @return the known order bys for this query.
	 */
	TypeSafeQueryOrderBys getOrderBys();
	
	/**
	 * @return the known group bys for this query.
	 */
	TypeSafeQueryGroupBys getGroupBys();

	/**
	 * Get the child data, linked to the property called on the proxy.
	 */
	TypeSafeQueryProxyData getData(TypeSafeQueryProxy proxy, String property);

	/**
	 * Adds the child as child of the data.
	 */
	void addData(TypeSafeQueryProxyData data, TypeSafeQueryProxyData child);
	
}
