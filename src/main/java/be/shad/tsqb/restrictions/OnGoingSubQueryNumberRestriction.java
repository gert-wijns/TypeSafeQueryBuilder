package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.TypeSafeValue;

public class OnGoingSubQueryNumberRestriction extends OnGoingNumberRestriction {
	private final static String EXISTS = "exists";
	private final RestrictionBase restriction;

	public OnGoingSubQueryNumberRestriction(RestrictionBase restriction,
			TypeSafeValue<Number> argument) {
		super(restriction, argument);
		this.restriction = restriction;
	}

	public RestrictionChainable exists() {
		restriction.setRight(restriction.getLeft());
		restriction.setLeft(null);
		restriction.setOperator(EXISTS);
		return restriction;
	}
}
