package be.shad.tsqb;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.shad.tsqb.grouping.GroupByBase;
import be.shad.tsqb.grouping.OnGoingGroupBy;
import be.shad.tsqb.grouping.TypeSafeQueryGroupBys;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.ordering.OrderByBase;
import be.shad.tsqb.ordering.TypeSafeQueryOrderBys;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQueryProxyData;
import be.shad.tsqb.proxy.TypeSafeQueryProxyFactory;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.RestrictionBase;
import be.shad.tsqb.restrictions.TypeSafeQueryRestrictions;
import be.shad.tsqb.selection.TypeSafeQueryProjections;
import be.shad.tsqb.values.TypeSafeSubQuery;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeQuery {
	// entries at key=null are root entries
	private final TypeSafeQuery parent;
	private final TypeSafeQueryHelper helper;
	private final EntityAliasProvider aliasProvider;
	private final TypeSafeQueryProxyFactory proxyFactory;
	private final TypeSafeQueryInvocationQueue invocationQueue;
	private final Map<TypeSafeQueryProxyData, Map<String, TypeSafeQueryProxyData>> tree = new HashMap<>();
	private final Set<TypeSafeQueryProxyData> scope = new HashSet<TypeSafeQueryProxyData>();
	private final Map<TypeSafeQueryProxy, TypeSafeQueryProxyData> data = new HashMap<>();

	private final TypeSafeQueryProjections selections = new TypeSafeQueryProjections(this);
	private final TypeSafeQueryRestrictions restrictions = new TypeSafeQueryRestrictions(); 
	private final TypeSafeQueryGroupBys groupBys = new TypeSafeQueryGroupBys();
	private final TypeSafeQueryOrderBys orderBys = new TypeSafeQueryOrderBys();
	
	public TypeSafeQuery(TypeSafeQueryHelper helper) {
		this(helper, null, new EntityAliasProvider(), new TypeSafeQueryInvocationQueue());
	}
	
	protected TypeSafeQuery(TypeSafeQueryHelper helper, 
			TypeSafeQuery parent, 
			EntityAliasProvider aliasProvider,
			TypeSafeQueryInvocationQueue invocationQueue) {
		this.helper = helper;
		this.proxyFactory = helper.getTypeSafeProxyFactory();
		this.invocationQueue = invocationQueue;
		this.parent = parent;
		this.aliasProvider = aliasProvider;
	}

	public boolean isInScope(TypeSafeQueryProxyData data) {
		if( scope.contains(data) ) {
			return true;
		}
		if( parent != null ) {
			return parent.isInScope(data);
		}
		return false;
	}

	public TypeSafeQueryProxyData getData(TypeSafeQueryProxy proxy) {
		return data.get(proxy);
	}
	
	public TypeSafeQueryProxyData getData(TypeSafeQueryProxy parent, String propertyPath) {
		Map<String, TypeSafeQueryProxyData> map = tree.get(parent);
		if( map == null ) {
			return null;
		}
		return map.get(propertyPath);
	}
	
	public void add(TypeSafeQueryProxyData parent, TypeSafeQueryProxyData data) {
		this.data.put(data.getProxy(), data);
		this.scope.add(data);
		Map<String, TypeSafeQueryProxyData> map = this.tree.get(parent);
		if( map == null ) {
			map = new HashMap<>();
			this.tree.put(parent, map);
		}
		map.put(data.getPropertyPath(), data);
	}
	
	public void invocationWasMade(TypeSafeQueryProxyData data) {
		invocationQueue.invocationWasMade(data);
	}
	
	public List<TypeSafeQueryProxyData> dequeueInvocations() {
		return invocationQueue.dequeueInvocations();
	}
	
	public TypeSafeQuery subQuery() {
		return new TypeSafeSubQuery<>(helper, this, aliasProvider, invocationQueue);
	}
	
	public <T> T from(Class<T> fromClass) {
		T proxy = proxyFactory.getProxyInstance(fromClass);
		TypeSafeQueryProxyData data = new TypeSafeQueryProxyData(null,
				null, fromClass, (TypeSafeQueryProxy) proxy, 
				createEntityAlias());
		proxyFactory.setMethodListener(data, this);
		add(null, data); // add as root data
		return proxy;
	}
	
	public <T> T select(Class<T> resultClass) {
		T proxy = proxyFactory.getProxyInstance(resultClass);
		proxyFactory.setSelectIntoMethodListener((TypeSafeQueryProxy) proxy, this);
		selections.setResultClass(resultClass);
		return proxy;
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
		List<TypeSafeQueryProxyData> invocations = invocationQueue.dequeueInvocations();
		if( invocations.size() != 1 ) {
			throw new IllegalStateException(String.format("There are %d invocations pending. Only 1 should be pending. "
					+ "The one that was used to call join(value, joinType).", invocations.size()));
		}
		TypeSafeQueryProxy proxy = invocations.get(0).getProxy();
		data.get(proxy).setJoinType(joinType);
		return proxy;
	}
	
	public String createEntityAlias() {
		return aliasProvider.createNewEntityAlias();
	}
	
	public TypeSafeQueryRestrictions getRestrictions() {
		return restrictions;
	}
	
	public TypeSafeQueryProjections getProjections() {
		return selections;
	}
	
	public TypeSafeQueryOrderBys getOrderBys() {
		return orderBys;
	}

	public TypeSafeQueryGroupBys getGroupBys() {
		return groupBys;
	}

	public Collection<TypeSafeQueryProxyData> getChildren(TypeSafeQueryProxyData parent) {
		Map<String, TypeSafeQueryProxyData> map = tree.get(parent);
		if( map == null ) {
			return Collections.emptyList();
		} else {
			return map.values();
		}
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
	 * Compose a query object with the selections, from, wheres, group bys and order bys.
	 */
	public HqlQuery toHqlQuery() {
		HqlQuery query = new HqlQuery();
		
		// append select part:
		selections.appendTo(query);
		
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
	public String toString() {
		StringBuilder sb = new StringBuilder(format("\n"));
		sb.append(toString(0, getChildren(null)));
		sb.append("\n").append(restrictions.toString());
		return sb.toString();
	}

	private String toString(int depth, Collection<TypeSafeQueryProxyData> children) {
		if( children.isEmpty() ) {
			return "";
		}
		StringBuilder sb = new StringBuilder("\n");
		for(int i=0, n= depth; i < n; i++) {
			sb.append("  ");
		}
		String prefix = sb.toString();
		
		sb = new StringBuilder();
		for(TypeSafeQueryProxyData child: children) {
			if( child.getProxy() != null ) {
				String propertyType = child.getPropertyType().getSimpleName();
				sb.append(prefix).append("Class [").append(propertyType).append("]");
				if( child.getPropertyPath() != null ) {
					sb.append(" for path [").append(child.getPropertyPath()).append("]");
				}
				if( child.getJoinType() != null ) {
					sb.append(" with join [").append(child.getJoinType()).append("]");
				}
				sb.append(toString(depth+1, getChildren(child)));
			}
		}
		return sb.toString();
	}

}
