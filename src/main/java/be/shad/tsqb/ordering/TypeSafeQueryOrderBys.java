package be.shad.tsqb.ordering;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;

public class TypeSafeQueryOrderBys implements HqlQueryBuilder {
	private List<OrderBy> orderBys = new LinkedList<>();

	public List<OrderBy> getOrderBys() {
		return orderBys;
	}
	
	public void addOrderBy(OrderBy orderBy) {
		orderBys.add(orderBy);
	}
	
	@Override
	public void appendTo(HqlQuery query) {
		for(OrderBy orderBy: orderBys) {
			orderBy.appendTo(query);
		}
	}

}
