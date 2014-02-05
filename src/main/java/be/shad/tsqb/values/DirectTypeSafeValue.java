package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQueryInternal;


public class DirectTypeSafeValue<T> extends TypeSafeValueImpl<T> {
	private T value;
	
	@SuppressWarnings("unchecked")
	public DirectTypeSafeValue(TypeSafeQueryInternal query, T value) {
		super(query, (Class<T>) value.getClass());
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public HqlQueryValueImpl toHqlQueryValue() {
		return new HqlQueryValueImpl("?", value);
	}

}
