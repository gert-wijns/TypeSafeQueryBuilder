package be.shad.tsqb.restrictions;

/**
 * Binds ContinuedOnGoingEnumRestriction and OnGoingEnumRestriction together.
 * <p>
 * Also extends the original restriction to allow method chaining with implicit and().
 */
public interface ContinuedOnGoingEnumRestriction<E extends Enum<E>> 
    extends ContinuedOnGoingRestriction<E, ContinuedOnGoingEnumRestriction<E>, OnGoingEnumRestriction<E>>,
    OnGoingEnumRestriction<E> {

}
