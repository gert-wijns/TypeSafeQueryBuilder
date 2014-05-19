package be.shad.tsqb.restrictions;

public interface OnGoingEnumRestriction<E extends Enum<E>> extends OnGoingRestriction<E, ContinuedOnGoingEnumRestriction<E>, OnGoingEnumRestriction<E>> {

}
