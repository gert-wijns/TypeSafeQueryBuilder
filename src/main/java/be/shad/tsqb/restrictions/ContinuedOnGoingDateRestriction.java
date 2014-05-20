package be.shad.tsqb.restrictions;

import java.util.Date;

/**
 * Binds ContinuedOnGoingRestriction and OnGoingDateRestriction together.
 * <p>
 * Also extends the original restriction to allow method chaining with implicit and().
 */
public interface ContinuedOnGoingDateRestriction 
    extends ContinuedOnGoingRestriction<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction>,
    OnGoingDateRestriction {

}
