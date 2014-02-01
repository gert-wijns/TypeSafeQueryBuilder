package be.shad.tsqb.query;

import be.shad.tsqb.selection.TypeSafeQueryProjections;

public interface TypeSafeRootQueryInternal extends TypeSafeRootQuery, TypeSafeQueryInternal {

	TypeSafeQueryProjections getProjections();

	/**
	 * Lets the root query know a value of a subquery was selected 
	 * using the getValue on the subquery. This can only be used
	 * when selecting the value into a resultDto.
	 */
	void queueSubqueryValueRetrieved(TypeSafeSubQuery<?> selected);
	
	/**
	 * Return the last subquery value retrieval and clear the value.
	 */
	TypeSafeSubQuery<?> dequeueSubqueryValueRetrieval();
	
}
