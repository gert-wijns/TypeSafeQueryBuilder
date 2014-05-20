package be.shad.tsqb.restrictions;

/**
 * Exposes boolean related restrictions in addition to the basic restrictions.
 */
public interface OnGoingBooleanRestriction 
    extends RestrictionChainable, OnGoingRestriction<Boolean, ContinuedOnGoingBooleanRestriction, OnGoingBooleanRestriction> {

    /**
     * Generates: left == false
     */
    RestrictionChainable isFalse();

    /**
     * Generates: left == true
     */
    RestrictionChainable isTrue();
    
}
