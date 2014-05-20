package be.shad.tsqb.restrictions;

/**
 * Binds ContinuedOnGoingNumberRestriction and OnGoingNumberRestriction together.
 * <p>
 * Also extends the original restriction to allow method chaining with implicit and().
 */
public interface ContinuedOnGoingNumberRestriction 
    extends ContinuedOnGoingRestriction<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction>,
    OnGoingNumberRestriction {

}
