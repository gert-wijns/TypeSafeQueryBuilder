package be.shad.tsqb.query;


public interface TypeSafeRootQuery extends TypeSafeQuery {
	
	/**
	 * Can be used when not selecting into a result type,
	 * or when selecting a single value in a subquery.
	 * <p>
	 * This is not the preferred way to select when
	 * working with a root query.
	 * <p>
	 * The selects will not receive an alias.
	 */
	void selectValue(Object value);
	
	<T> T select(Class<T> resultClass);

}
