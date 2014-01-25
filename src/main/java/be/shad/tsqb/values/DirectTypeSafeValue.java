package be.shad.tsqb.values;


public class DirectTypeSafeValue<T> implements TypeSafeValue<T> {
	private T value;
	
	public DirectTypeSafeValue(T value) {
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
