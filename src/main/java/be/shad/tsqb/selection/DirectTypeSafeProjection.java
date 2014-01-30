package be.shad.tsqb.selection;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.hql.HqlQuery;

public class DirectTypeSafeProjection implements TypeSafeProjection {
	private final TypeSafeQueryProxyData selection;
	private final String propertyName;

	public DirectTypeSafeProjection(
			TypeSafeQueryProxyData selection,
			String propertyName) {
		this.selection = selection;
		this.propertyName = propertyName;
	}

	public TypeSafeQueryProxyData getSelection() {
		return selection;
	}

	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public void appendTo(HqlQuery query) {
		query.appendSelect(selection.getAlias() + " as " + propertyName);
	}
	
}
