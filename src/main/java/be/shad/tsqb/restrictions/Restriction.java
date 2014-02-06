package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.HqlQueryValueBuilder;

/**
 * Represents a restriction. 
 * <p>
 * All restrictions could be expressed using the {@link RestrictionImpl} so 
 * this interface currently only has one implementation.
 */
public interface Restriction extends RestrictionChainable, HqlQueryValueBuilder {
    
    /**
     * Available to check whether or not to add enclosing brackets.
     * A restriction group is wrapped in brackets if it is not
     * the same as the restriction group to which the restriction is added!
     * <p>
     * You probably don't need to care about this though unless
     * you create your own Restriction implementation.
     */
    RestrictionsGroup getRestrictionsGroup();
    
}
