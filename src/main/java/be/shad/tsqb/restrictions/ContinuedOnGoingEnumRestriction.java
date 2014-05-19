package be.shad.tsqb.restrictions;


public interface ContinuedOnGoingEnumRestriction<E extends Enum<E>> 
    extends ContinuedOnGoingRestriction<E, ContinuedOnGoingEnumRestriction<E>, OnGoingEnumRestriction<E>>,
    OnGoingEnumRestriction<E>{

}
