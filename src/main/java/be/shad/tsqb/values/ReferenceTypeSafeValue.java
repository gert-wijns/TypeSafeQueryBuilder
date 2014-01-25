package be.shad.tsqb.values;

import be.shad.tsqb.proxy.TypeSafeQueryProxyData;

public class ReferenceTypeSafeValue<T> implements TypeSafeValue<T> {
	private final TypeSafeQueryProxyData data;
	
	public ReferenceTypeSafeValue(TypeSafeQueryProxyData data) {
		this.data = data;
	}

	@Override
	public HqlQueryValue toHqlQueryValue() {
		return new HqlQueryValueImpl(data.getAlias());
	}

}
