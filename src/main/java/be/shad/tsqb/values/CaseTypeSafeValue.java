package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQueryInternal;

public class CaseTypeSafeValue<T> extends TypeSafeValueImpl<T> {

	protected CaseTypeSafeValue(TypeSafeQueryInternal query, Class<T> valueType) {
		super(query, valueType);
	}

	@Override
	public HqlQueryValue toHqlQueryValue() {
		return null;
	}

}
