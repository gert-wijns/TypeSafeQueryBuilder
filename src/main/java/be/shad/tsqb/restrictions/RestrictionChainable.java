package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.TypeSafeValue;

public interface RestrictionChainable {

	OnGoingNumberRestriction andn(TypeSafeValue<Number> value);

	OnGoingNumberRestriction and(Number value);

	OnGoingTextRestriction andt(TypeSafeValue<String> value);

	OnGoingTextRestriction and(String value);

}
