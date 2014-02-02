package be.shad.tsqb.restrictions;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.TypeSafeValue;

public abstract class RestrictionChainableImpl implements RestrictionChainable, RestrictionProvider {
	
	public abstract RestrictionImpl and();

	public abstract RestrictionImpl or();
	
	@Override
	public <E extends Enum<E>> OnGoingEnumRestriction<E> and(E value) {
		return new OnGoingEnumRestriction<E>(and(), value);
	}
	
	@Override
	public <E extends Enum<E>> OnGoingSubQueryEnumRestriction<E> ande(TypeSafeSubQuery<E> value) {
		return new OnGoingSubQueryEnumRestriction<E>(and(), value);
	}
	
	@Override
	public <E extends Enum<E>> OnGoingEnumRestriction<E> ande(TypeSafeValue<E> value) {
		return new OnGoingEnumRestriction<E>(and(), value);
	}
	
	@Override
	public OnGoingTextRestriction and(String value) {
		return new OnGoingTextRestriction(and(), value);
	}

	@Override
	public OnGoingTextRestriction andt(TypeSafeValue<String> value) {
		return new OnGoingTextRestriction(and(), value);
	}

	@Override
	public OnGoingSubQueryTextRestriction andt(TypeSafeSubQuery<String> value) {
		return new OnGoingSubQueryTextRestriction(and(), value);
	}

	@Override
	public OnGoingBooleanRestriction and(Boolean value) {
		return new OnGoingBooleanRestriction(and(), value);
	}
	
	@Override
	public OnGoingBooleanRestriction andb(TypeSafeValue<Boolean> value) {
		return new OnGoingBooleanRestriction(and(), value);
	}
	
	@Override
	public OnGoingNumberRestriction and(Number value) {
		return new OnGoingNumberRestriction(and(), value);
	}
	
	@Override
	public OnGoingNumberRestriction andn(TypeSafeValue<Number> value) {
		return new OnGoingNumberRestriction(and(), value);
	}
	
	@Override
	public OnGoingSubQueryNumberRestriction andn(TypeSafeSubQuery<Number> value) {
		return new OnGoingSubQueryNumberRestriction(and(), value);
	}

	@Override
	public OnGoingTextRestriction or(String value) {
		return new OnGoingTextRestriction(or(), value);
	}

	@Override
	public OnGoingTextRestriction ort(TypeSafeValue<String> value) {
		return new OnGoingTextRestriction(or(), value);
	}

	@Override
	public OnGoingSubQueryTextRestriction ort(TypeSafeSubQuery<String> value) {
		return new OnGoingSubQueryTextRestriction(or(), value);
	}

	@Override
	public OnGoingNumberRestriction or(Number value) {
		return new OnGoingNumberRestriction(or(), value);
	}
	
	@Override
	public OnGoingNumberRestriction orn(TypeSafeValue<Number> value) {
		return new OnGoingNumberRestriction(or(), value);
	}
	
	@Override
	public OnGoingSubQueryNumberRestriction orn(TypeSafeSubQuery<Number> value) {
		return new OnGoingSubQueryNumberRestriction(or(), value);
	}
	
}
