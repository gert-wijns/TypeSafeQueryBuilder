package be.shad.tsqb.restrictions;

/**
 * Binds ContinuedOnGoingObjectRestriction and OnGoingObjectRestriction together.
 * <p>
 * Also extends the original restriction to allow method chaining with implicit and().
 */
public interface ContinuedOnGoingObjectRestriction<VAL>
    extends ContinuedOnGoingRestriction<VAL, ContinuedOnGoingObjectRestriction<VAL>, OnGoingObjectRestriction<VAL>>,
    OnGoingObjectRestriction<VAL> {

}
