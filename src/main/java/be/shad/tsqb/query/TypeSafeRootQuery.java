package be.shad.tsqb.query;


public interface TypeSafeRootQuery extends TypeSafeQuery {

	<T> T select(Class<T> resultClass);

}
