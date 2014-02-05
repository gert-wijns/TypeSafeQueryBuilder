package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQueryInternal;

public abstract class TypeSafeValueImpl<T> implements TypeSafeValue<T> {
	protected final TypeSafeQueryInternal query;
	private final Class<T> valueType;
	
	protected TypeSafeValueImpl(TypeSafeQueryInternal query, Class<T> valueType) {
		this.query = query;
		this.valueType = valueType;
	}

	@Override
	public Class<T> getValueClass() {
		return valueType;
	}

	@Override
	public T select() {
		return query.getRootQuery().queueValueSelected(this);
	}

}
