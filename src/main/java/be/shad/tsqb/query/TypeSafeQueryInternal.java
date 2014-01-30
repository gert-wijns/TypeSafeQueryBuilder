package be.shad.tsqb.query;

import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQueryProxyDataTree;
import be.shad.tsqb.grouping.TypeSafeQueryGroupBys;
import be.shad.tsqb.ordering.TypeSafeQueryOrderBys;
import be.shad.tsqb.restrictions.RestrictionsGroup;

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
	 * And before <code>join<code> in case <code>join</code> is not null.
	 */
	boolean isInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join);
	
	/**
	 * @return the known restrictions for this query.
	 */
	RestrictionsGroup getRestrictions();
	
	/**
	 * @return the known order bys for this query.
	 */
	TypeSafeQueryOrderBys getOrderBys();
	
	/**
	 * @return the known group bys for this query.
	 */
	TypeSafeQueryGroupBys getGroupBys();
	
	/**
	 * Data tree, contains all proxy data related to this query.
	 * The joins are constructed using this tree.
	 */
	TypeSafeQueryProxyDataTree getDataTree();
	
}
