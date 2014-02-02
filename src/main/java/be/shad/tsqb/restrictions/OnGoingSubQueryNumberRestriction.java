package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionImpl.EXISTS;
import be.shad.tsqb.query.TypeSafeSubQuery;

public class OnGoingSubQueryNumberRestriction extends OnGoingNumberRestriction {
	private final RestrictionImpl restriction;

	public OnGoingSubQueryNumberRestriction(RestrictionImpl restriction,
			TypeSafeSubQuery<Number> argument) {
		super(restriction, argument);
		this.restriction = restriction;
	}

	public Restriction exists() {
		restriction.setRight(restriction.getLeft());
		restriction.setLeft(null);
		restriction.setOperator(EXISTS);
		return restriction;
	}
}
