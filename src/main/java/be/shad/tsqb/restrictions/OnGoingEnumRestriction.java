package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.TypeSafeValue;

public class OnGoingEnumRestriction<E extends Enum<E>> extends OnGoingRestriction<E> {

	public OnGoingEnumRestriction(RestrictionImpl restriction, E argument) {
		super(restriction, argument);
	}

	public OnGoingEnumRestriction(RestrictionImpl restriction, TypeSafeValue<E> argument) {
		super(restriction, argument);
	}

}
