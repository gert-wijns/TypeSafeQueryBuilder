package be.shad.tsqb.restrictions;

/**
 * Exposes enum related restrictions in addition to the basic restrictions.
 */
public interface OnGoingEnumRestriction<E extends Enum<E>> extends OnGoingRestriction<E, ContinuedOnGoingEnumRestriction<E>, OnGoingEnumRestriction<E>> {

}
