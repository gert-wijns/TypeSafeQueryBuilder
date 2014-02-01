package be.shad.tsqb.query;

import be.shad.tsqb.values.TypeSafeValue;

public interface TypeSafeSubQuery<T extends Object> extends TypeSafeValue<T>, TypeSafeQuery {
	
	/**
	 * Set the value to select
	 */
	void select(T value);
	
	/**
	 * Set the value to select
	 */
	void select(TypeSafeValue<T> value);

	/**
	 * Get the value to select into a result dto.@return
	 */
	T getValue();
	
}
