package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionImpl.EXISTS;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeSubQuery;

/**
 * Adds the exists method when working with a subquery.
 */
public class OnGoingSubQueryDateRestriction extends OnGoingDateRestriction {
	private final RestrictionImpl restriction;

	public OnGoingSubQueryDateRestriction(RestrictionImpl restriction,
			TypeSafeSubQuery<Date> argument) {
		super(restriction, argument);
		this.restriction = restriction;
	}

	/**
	 * Check if the left value exists.
	 */
	public Restriction exists() {
		restriction.setRight(restriction.getLeft());
		restriction.setLeft(null);
		restriction.setOperator(EXISTS);
		return restriction;
	}
}
