package be.shad.tsqb.selection;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeSubQuery;

public class SubQueryTypeSafeProjection implements TypeSafeProjection {
	private final TypeSafeSubQuery<?> subQuery;
	private final String propertyName;

	public SubQueryTypeSafeProjection(
			TypeSafeSubQuery<?> subQuery,
			String propertyName) {
		this.subQuery = subQuery;
		this.propertyName = propertyName;
	}

	public TypeSafeSubQuery<?> getSubQuery() {
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
