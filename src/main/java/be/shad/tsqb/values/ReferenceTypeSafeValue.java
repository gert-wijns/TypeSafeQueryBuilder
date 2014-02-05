package be.shad.tsqb.values;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;

public class ReferenceTypeSafeValue<T> extends TypeSafeValueImpl<T> {
	private final TypeSafeQueryProxyData data;
	
	@SuppressWarnings("unchecked")
	public ReferenceTypeSafeValue(TypeSafeQueryInternal query, TypeSafeQueryProxyData data) {
		super(query, (Class<T>) data.getPropertyType());
		this.data = data;
	}
	
	public TypeSafeQueryProxyData getData() {
		return data;
	}
	
	@Override
	public HqlQueryValue toHqlQueryValue() {
		return new HqlQueryValueImpl(data.getAlias());
	}

}
