package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionImpl.EXISTS;
import be.shad.tsqb.query.TypeSafeSubQuery;

/**
 * Adds the exists method when working with a subquery.
 */
public class OnGoingSubQueryEnumRestriction<E extends Enum<E>> extends OnGoingEnumRestriction<E> {
	private final RestrictionImpl restriction;

	public OnGoingSubQueryEnumRestriction(RestrictionImpl restriction,
			TypeSafeSubQuery<E> argument) {
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
