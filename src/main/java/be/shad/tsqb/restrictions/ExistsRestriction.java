package be.shad.tsqb.restrictions;

import be.shad.tsqb.HqlQuery;
import be.shad.tsqb.values.TypeSafeSubQuery;

public class ExistsRestriction implements Restriction {
	private TypeSafeSubQuery<?> subQuery;

	public ExistsRestriction(TypeSafeSubQuery<?> subQuery) {
		this.subQuery = subQuery;
	}

	@Override
	public void appendTo(HqlQuery query) {
		HqlQuery hqlQuery = subQuery.toHqlQuery();
		StringBuilder where = new StringBuilder();
		where.append("exists (").append(hqlQuery.getHql()).append(")");
		query.appendWhere(where.toString());
		query.addParams(hqlQuery.getParams());
	}
	
}
