package be.shad.tsqb.restrictions;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.values.HqlQueryValueBuilder;

/**
 * Extend to include extra interfaces
 */
public interface RestrictionsGroupInternal extends RestrictionsGroup, RestrictionProvider, RestrictionChainable, HqlQueryValueBuilder {

    /**
     * Get the join, for scope testing.
     */
    TypeSafeQueryProxyData getJoin();
    
}
