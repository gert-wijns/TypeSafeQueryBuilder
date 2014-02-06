package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for enums, enum specific restrictions can be added here.
 * 
 * @see OnGoingRestriction
 */
public class OnGoingEnumRestriction<E extends Enum<E>> extends OnGoingRestriction<E> {

    public OnGoingEnumRestriction(RestrictionImpl restriction, E argument) {
        super(restriction, argument);
    }

    public OnGoingEnumRestriction(RestrictionImpl restriction, TypeSafeValue<E> argument) {
        super(restriction, argument);
    }

}
