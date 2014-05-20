package be.shad.tsqb.restrictions;

/**
 * Binds ContinuedOnGoingBooleanRestriction and OnGoingBooleanRestriction together.
 * <p>
 * Also extends the original restriction to allow method chaining with implicit and().
 */
public interface ContinuedOnGoingBooleanRestriction
    extends ContinuedOnGoingRestriction<Boolean, ContinuedOnGoingBooleanRestriction, OnGoingBooleanRestriction>,
    OnGoingBooleanRestriction {

}
