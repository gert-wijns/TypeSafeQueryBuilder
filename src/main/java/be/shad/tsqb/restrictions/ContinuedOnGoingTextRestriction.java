package be.shad.tsqb.restrictions;

/**
 * Binds ContinuedOnGoingTextRestriction and OnGoingTextRestriction together.
 * <p>
 * Also extends the original restriction to allow method chaining with implicit and().
 */
public interface ContinuedOnGoingTextRestriction 
    extends ContinuedOnGoingRestriction<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction>,
    OnGoingTextRestriction {

}
