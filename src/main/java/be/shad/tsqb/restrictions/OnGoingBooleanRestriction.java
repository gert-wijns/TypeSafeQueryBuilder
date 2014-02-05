package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionImpl.EQUAL;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for booleans, boolean specific restrictions are added here.
 * 
 * @see OnGoingRestriction
 */
public class OnGoingBooleanRestriction extends OnGoingRestriction<Boolean> {

	public OnGoingBooleanRestriction(RestrictionImpl restriction, Boolean argument) {
		super(restriction, argument);
	}

	public OnGoingBooleanRestriction(RestrictionImpl restriction, TypeSafeValue<Boolean> argument) {
		super(restriction, argument);
	}

	/**
	 * Generates: left = false
	 */
	public Restriction isFalse() {
		restriction.setOperator(EQUAL);
		restriction.setRight(new DirectTypeSafeValue<>(restriction.getQuery(), Boolean.FALSE));
		return restriction;
	}

	/**
	 * Generates: left = true
	 */
	public Restriction isTrue() {
		restriction.setOperator(EQUAL);
		restriction.setRight(new DirectTypeSafeValue<>(restriction.getQuery(), Boolean.TRUE));
		return restriction;
	}
	
}
