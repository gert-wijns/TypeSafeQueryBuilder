package be.shad.tsqb.grouping;

import java.util.List;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.proxy.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

public class GroupByBase implements GroupBy, OnGoingGroupBy {

	private final TypeSafeQueryInternal query;
	private final TypeSafeValue<?> value;

	public GroupByBase(TypeSafeQueryInternal query, Object value) {
		this.query = query;
		if( value instanceof TypeSafeValue<?> ) {
			this.value = (TypeSafeValue<?>) value;
		} else {
			List<TypeSafeQueryProxyData> invocations = query.dequeueInvocations();
			this.value = new ReferenceTypeSafeValue<>(invocations.get(0));
		}
		this.query.getGroupBys().addGroupBy(this);
	}

	@Override
	public OnGoingGroupBy and(Object obj) {
		return new GroupByBase(query, obj);
	}
	
	@Override
	public void appendTo(HqlQuery query) {
		query.appendGroupBy(value.toHqlQueryValue().getHql());
	}


}
