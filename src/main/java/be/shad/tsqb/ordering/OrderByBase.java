package be.shad.tsqb.ordering;

import java.util.List;

import be.shad.tsqb.HqlQuery;
import be.shad.tsqb.proxy.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

public class OrderByBase implements OrderBy, OnGoingOrderBy {
	private final TypeSafeQueryInternal query;
	private TypeSafeValue<?> value;
	private boolean ascending;

	public OrderByBase(TypeSafeQueryInternal query) {
		this.query = query;
	}
	
	public TypeSafeValue<?> getValue() {
		return value;
	}
	
	public void setValue(TypeSafeValue<?> value) {
		this.value = value;
	}
	
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}
	
	public boolean isAscending() {
		return ascending;
	}

	@Override
	public OnGoingOrderBy desc(Object obj) {
		ascending = false;
		return registerOrderBy(obj);
	}

	@Override
	public OnGoingOrderBy asc(Object obj) {
		ascending = true;
		return registerOrderBy(obj);
	}

	/**
	 * Set the value and add this to the orderBys.
	 */
	private OnGoingOrderBy registerOrderBy(Object obj) {
		value = toValue(obj);
		query.getOrderBys().addOrderBy(this);
		return new OrderByBase(query);
	}

	private TypeSafeValue<?> toValue(Object value) {
		if( value instanceof TypeSafeValue<?> ) {
			return (TypeSafeValue<?>) value;
		} else {
			List<TypeSafeQueryProxyData> invocations = query.dequeueInvocations();
			return new ReferenceTypeSafeValue<>(invocations.get(0));
		}
	}

	@Override
	public void appendTo(HqlQuery query) {
		query.appendOrderBy(value.toHqlQueryValue().getHql() + (ascending ? " asc": " desc"));
	}
}
