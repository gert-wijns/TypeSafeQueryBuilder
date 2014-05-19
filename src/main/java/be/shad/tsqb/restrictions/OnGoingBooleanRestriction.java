package be.shad.tsqb.restrictions;

public interface OnGoingBooleanRestriction extends RestrictionChainable, OnGoingRestriction<Boolean, ContinuedOnGoingBooleanRestriction, OnGoingBooleanRestriction> {

    /**
     * Generates: left == false
     */
    Restriction isFalse();

    /**
     * Generates: left == true
     */
    Restriction isTrue();
    
}
