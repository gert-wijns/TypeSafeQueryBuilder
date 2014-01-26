package be.shad.tsqb.query;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.shad.tsqb.grouping.GroupByBase;
import be.shad.tsqb.grouping.OnGoingGroupBy;
import be.shad.tsqb.grouping.TypeSafeQueryGroupBys;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.ordering.OrderByBase;
import be.shad.tsqb.ordering.TypeSafeQueryOrderBys;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQueryProxyData;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.RestrictionBase;
import be.shad.tsqb.restrictions.TypeSafeQueryRestrictions;
import be.shad.tsqb.selection.TypeSafeQueryProjections;
import be.shad.tsqb.values.TypeSafeValue;

public abstract class AbstractTypeSafeQuery implements TypeSafeQuery, TypeSafeQueryInternal {
	protected final TypeSafeQueryHelper helper;
	private TypeSafeRootQueryInternal rootQuery;

	private final Map<TypeSafeQueryProxyData, Map<String, TypeSafeQueryProxyData>> tree = new HashMap<>();

	private final TypeSafeQueryProjections projections = new TypeSafeQueryProjections(this); 
	private final TypeSafeQueryRestrictions restrictions = new TypeSafeQueryRestrictions(); 
	private final TypeSafeQueryGroupBys groupBys = new TypeSafeQueryGroupBys();
	private final TypeSafeQueryOrderBys orderBys = new TypeSafeQueryOrderBys();
	
	public AbstractTypeSafeQuery(TypeSafeQueryHelper helper) {
		this.tree.put(null, new HashMap<String, TypeSafeQueryProxyData>());
		this.helper = helper;
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

	/**
	 * Start restricting a text value.
	 */
	public OnGoingTextRestriction restrict(String value) {
		return new OnGoingTextRestriction(new RestrictionBase(this), value);
	}

	/**
	 * Start restricting a text value.
	 */
	public OnGoingTextRestriction restrictt(TypeSafeValue<String> value) {
		return new OnGoingTextRestriction(new RestrictionBase(this), value);
	}
	
	/**
	 * Start restricting a number value.
	 */
	public OnGoingNumberRestriction restrict(Number value) {
		return new OnGoingNumberRestriction(new RestrictionBase(this), value);
	}
	
	/**
	 * Start restricting a number value.
	 */
	public OnGoingNumberRestriction restrictn(TypeSafeValue<Number> value) {
		return new OnGoingNumberRestriction(new RestrictionBase(this), value);
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

	public boolean isInScope(TypeSafeQueryProxyData data) {
		if( tree.containsKey(data) ) {
			return true;
		}
		return false;
	}
	
	public TypeSafeQueryProjections getProjections() {
		return projections;
	}

	@Override
	public TypeSafeQueryRestrictions getRestrictions() {
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
		Collection<TypeSafeQueryProxyData> root = tree.get(null).values();
		for(TypeSafeQueryProxyData data: root) {
			query.appendFrom(createFrom(data));
		}
		
		// append where part:
		restrictions.appendTo(query);
		
		// append group part:
		groupBys.appendTo(query);
		
		// append order part:
		orderBys.appendTo(query);
		
		return query;
	}
	
	/**
	 * @return entityName + joins
	 */
	private String createFrom(TypeSafeQueryProxyData data) {
		StringBuilder from = new StringBuilder();
		from.append(helper.getEntityName(data.getPropertyType()));
		from.append(" ").append(data.getAlias()).append(" ");
		appendJoins(from, data);
		return from.toString();
	}
	
	/**
	 * Appends the joins with the correct join type/aliases to the from builder.
	 */
	private void appendJoins(StringBuilder from, TypeSafeQueryProxyData parent) {
		Collection<TypeSafeQueryProxyData> children = getChildren(parent);
		for(TypeSafeQueryProxyData child: children) {
			if( child.getProxy() != null && child.getJoinType() != JoinType.None ) {
				if( child.getJoinType() == null ) {
					throw new IllegalArgumentException("The getter for [" + child.getProxy() + "] was called, "
							+ "but it was not passed to query.join(object, jointype).");
				}
				// example: 'left join fetch' 'hobj1'.'propertyPath' 'hobj2' 
				from.append(format("%s %s.%s %s ", getJoinTypeString(child.getJoinType()), 
						parent.getAlias(), child.getPropertyPath(), child.getAlias()));
				appendJoins(from, child); // recursive append sub joins
			}
		}
	}

	public Collection<TypeSafeQueryProxyData> getChildren(TypeSafeQueryProxyData parent) {
		Map<String, TypeSafeQueryProxyData> map = tree.get(parent);
		if( map == null ) {
			return Collections.emptyList();
		} else {
			return map.values();
		}
	}

	private String getJoinTypeString(JoinType joinType) {
		switch (joinType) {
			case Fetch: return "join fetch";
			case Inner: return "join";
			case Left: return "left join";
			case LeftFetch: return "left join fetch";
			case Right: return "right join";
			default:
		}
		throw new IllegalArgumentException("JoinType " + joinType + " is no allowed.");
	}

	@Override
	public TypeSafeQueryProxyData getData(TypeSafeQueryProxy parent, String propertyPath) {
		Map<String, TypeSafeQueryProxyData> map = tree.get(parent);
		if( map == null ) {
			return null;
		}
		return map.get(propertyPath);
	}

	@Override
	public void addData(TypeSafeQueryProxyData parent, TypeSafeQueryProxyData data) {
		tree.get(parent).put(data.getPropertyPath(), data);
		if( !tree.containsKey(data) ) {
			tree.put(data, new HashMap<String, TypeSafeQueryProxyData>());
		}
	}
	
}
