package be.shad.tsqb.values;

/**
 * Wraps a value in a function.
 */
public class WrappedTypeSafeValue<T extends Object> implements TypeSafeValue<T> {
	private String function; // sum/max/min/trim/count/...
	private TypeSafeValue<T> value;
	
	public WrappedTypeSafeValue(String function, TypeSafeValue<T> value) {
		this.function = function;
		this.value = value;
	}
	
	@Override
	public HqlQueryValue toHqlQueryValue() {
		HqlQueryValue value = this.value.toHqlQueryValue();
		return new HqlQueryValueImpl(function + "("+value.getHql()+")", value.getParams());
	}
	
}
