package be.shad.tsqb.restrictions;

import be.shad.tsqb.query.TypeSafeSubQuery;

public class OnGoingSubQueryEnumRestriction<E extends Enum<E>> extends OnGoingEnumRestriction<E> {
	private final static String EXISTS = "exists";
	private final RestrictionImpl restriction;

	public OnGoingSubQueryEnumRestriction(RestrictionImpl restriction,
			TypeSafeSubQuery<E> argument) {
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
