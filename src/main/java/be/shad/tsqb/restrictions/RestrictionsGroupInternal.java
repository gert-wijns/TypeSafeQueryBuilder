package be.shad.tsqb.restrictions;

import be.shad.tsqb.data.TypeSafeQueryProxyData;

/**
 * Extend to include extra interfaces
 */
public interface RestrictionsGroupInternal extends RestrictionsGroup, RestrictionProvider, Restriction {

    /**
     * Get the join, for scope testing.
     */
    TypeSafeQueryProxyData getJoin();
    
}
