package be.shad.tsqb.restrictions;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.TypeSafeValue;

public interface RestrictionChainable {

	OnGoingSubQueryNumberRestriction andn(TypeSafeSubQuery<Number> value);
	
	OnGoingNumberRestriction andn(TypeSafeValue<Number> value);

	OnGoingNumberRestriction and(Number value);

	OnGoingSubQueryTextRestriction andt(TypeSafeSubQuery<String> value);
	
	OnGoingTextRestriction andt(TypeSafeValue<String> value);

	OnGoingTextRestriction and(String value);

}
