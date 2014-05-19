package be.shad.tsqb.restrictions;

public interface ContinuedOnGoingRestriction<VAL, CONTINUED extends ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL>, 
    ORIGINAL extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>> 
    extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>, ContinuedRestrictionChainable<VAL>, Restriction {

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
