package be.shad.tsqb.selection;

import be.shad.tsqb.HqlQuery;
import be.shad.tsqb.TypeSafeQuery;

public class SubQueryTypeSafeProjection implements TypeSafeProjection {
	private final TypeSafeQuery subQuery;
	private final String propertyName;

	public SubQueryTypeSafeProjection(
			TypeSafeQuery subQuery,
			String propertyName) {
		this.subQuery = subQuery;
		this.propertyName = propertyName;
	}

	public TypeSafeQuery getSubQuery() {
		return subQuery;
	}
	
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public void appendTo(HqlQuery query) {
		HqlQuery hqlQuery = subQuery.toHqlQuery();
		query.appendSelect("( " + hqlQuery.getHql() + " ) as " + propertyName);
		query.addParams(hqlQuery.getParams());
	}

}
