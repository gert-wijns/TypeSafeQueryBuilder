package be.shad.tsqb.restrictions;

/**
 * Exposes methods to continue chaining for the same left value (or right if left was null) 
 * of the previous restriction.
 * <p>
 * When using methods of the OnGoingRestriction, the and() is implicit.
 */
public interface ContinuedOnGoingRestriction<VAL, CONTINUED extends ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL>, 
    ORIGINAL extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>> 
    extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>, ContinuedRestrictionChainable {

    /**
     * Creates a new OnGoingRestriction of the original type where the
     * left value is pre-filled with the previous left value.
     * <p>
     * This restriction is added as and restriction.
     */
    ORIGINAL and();

    /**
     * Creates a new OnGoingRestriction of the original type where the
     * left value is pre-filled with the previous left value.
     * <p>
     * This restriction is added as or restriction.
     */
    ORIGINAL or();
    
}
