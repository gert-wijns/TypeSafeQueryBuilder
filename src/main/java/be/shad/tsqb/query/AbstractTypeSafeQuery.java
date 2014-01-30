package be.shad.tsqb.query;

import java.util.Collection;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQueryProxyDataTree;
import be.shad.tsqb.grouping.GroupByBase;
import be.shad.tsqb.grouping.OnGoingGroupBy;
import be.shad.tsqb.grouping.TypeSafeQueryGroupBys;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.joins.TypeSafeQueryJoin;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.ordering.OrderByBase;
import be.shad.tsqb.ordering.TypeSafeQueryOrderBys;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryTextRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.selection.TypeSafeQueryProjections;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

public abstract class AbstractTypeSafeQuery implements TypeSafeQuery, TypeSafeQueryInternal {
	protected final TypeSafeQueryHelper helper;
	private TypeSafeRootQueryInternal rootQuery;

	private final TypeSafeQueryProxyDataTree dataTree;
	private final TypeSafeQueryProjections projections = new TypeSafeQueryProjections(this); 
	private final RestrictionsGroup restrictions = new RestrictionsGroup(this, null); 
	private final TypeSafeQueryGroupBys groupBys = new TypeSafeQueryGroupBys();
	private final TypeSafeQueryOrderBys orderBys = new TypeSafeQueryOrderBys();
	
	public AbstractTypeSafeQuery(TypeSafeQueryHelper helper) {
		this.helper = helper;
		this.dataTree = new TypeSafeQueryProxyDataTree(helper, this);
	}

	@Override
	public TypeSafeQueryProxyDataTree getDataTree() {
		return dataTree;
	}
	
	@Override
	public TypeSafeRootQueryInternal getRootQuery() {
		return rootQuery;
	}
	
	public void setRootQuery(TypeSafeRootQueryInternal rootQuery) {
		this.rootQuery = rootQuery;
	}

	public <T> T from(Class<T> fromClass) {
		return helper.createTypeSafeFromProxy(this, fromClass);
	}
	

	public <T> T join(Collection<T> anyCollection) {
		return join(anyCollection, JoinType.Inner);
	}

	public <T> T join(T anyObject) {
		return join(anyObject, JoinType.Inner);
	}

	@SuppressWarnings("unchecked")
	public <T> T join(Collection<T> anyCollection, JoinType joinType) {
		return (T) join(joinType);
	}

	@SuppressWarnings("unchecked")
	public <T> T join(T anyObject, JoinType joinType) {
		return (T) join(joinType);
	}
	
	@Override
	public <T> TypeSafeQueryJoin<T> getJoin(T obj) {
		if(!(obj instanceof TypeSafeQueryProxy)) {
			throw new IllegalArgumentException("Can only get the join using a TypeSafeQueryProxy instance.");
		}
		return dataTree.getJoin(((TypeSafeQueryProxy) obj).getTypeSafeProxyData());
	}

	private TypeSafeQueryProxy join(JoinType joinType) {
		List<TypeSafeQueryProxyData> invocations = rootQuery.dequeueInvocations();
		if( invocations.size() != 1 ) {
			throw new IllegalStateException(String.format("There are %d invocations pending. Only 1 should be pending. "
					+ "The one that was used to call join(value, joinType).", invocations.size()));
		}
		TypeSafeQueryProxyData data = invocations.get(0);
		data.setJoinType(joinType);
		return data.getProxy();
	}

	@Override
	public RestrictionChainable where() {
		return restrictions;
	}
	
	/**
	 * Delegate to restrictions.
	 */
	@Override
	public OnGoingNumberRestriction where(Number value) {
		return restrictions.and(value);
	}

	/**
	 * Delegate to restrictions.
	 */
	@Override
	public OnGoingTextRestriction where(String value) {
		return restrictions.and(value);
	}

	/**
	 * Delegate to restrictions.
	 */
	@Override
	public OnGoingSubQueryNumberRestriction wheren(
			TypeSafeSubQuery<Number> value) {
		return restrictions.andn(value);
	}

	/**
	 * Delegate to restrictions.
	 */
	@Override
	public OnGoingNumberRestriction wheren(TypeSafeValue<Number> value) {
		return restrictions.andn(value);
	}

	/**
	 * Delegate to restrictions.
	 */
	@Override
	public OnGoingSubQueryTextRestriction wheret(TypeSafeSubQuery<String> value) {
		return restrictions.andt(value);
	}

	/**
	 * Delegate to restrictions.
	 */
	@Override
	public OnGoingTextRestriction wheret(TypeSafeValue<String> value) {
		return restrictions.andt(value);
	}
	
	/**
	 * Kicks off order by's. Use desc/asc afterwards to order by something.
	 */
	public OnGoingOrderBy order() {
		return new OrderByBase(this);
	}
	
	public OnGoingGroupBy groupBy(Object value) {
		return new GroupByBase(this, value);
	}

	public boolean isInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join) {
		return dataTree.isInScope(data, join);
	}
	
	public TypeSafeQueryProjections getProjections() {
		return projections;
	}

	@Override
	public RestrictionsGroup getRestrictions() {
		return restrictions;
	}
	
	@Override
	public TypeSafeQueryGroupBys getGroupBys() {
		return groupBys;
	}
	
	@Override
	public TypeSafeQueryOrderBys getOrderBys() {
		return orderBys;
	}
	
	@Override
	public <T> TypeSafeSubQuery<T> subquery(Class<T> clazz) {
		return new TypeSafeSubQueryImpl<>(helper, this);
	}

	/**
	 * Compose a query object with the selections, from, wheres, group bys and order bys.
	 */
	public HqlQuery toHqlQuery() {
		HqlQuery query = new HqlQuery();
		
		// append select part:
		projections.appendTo(query);
		
		// append from part + their joins:
		dataTree.appendTo(query);
		
		// append where part:
		HqlQueryValue hqlRestrictions = restrictions.toHqlQueryValue();
		query.appendWhere(hqlRestrictions.getHql());
		query.addParams(hqlRestrictions.getParams());
		
		// append group part:
		groupBys.appendTo(query);
		
		// append order part:
		orderBys.appendTo(query);
		
		return query;
	}
	
}
