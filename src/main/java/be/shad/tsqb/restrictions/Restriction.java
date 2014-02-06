package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.HqlQueryValueBuilder;

public interface Restriction extends RestrictionChainable, HqlQueryValueBuilder {
    
    RestrictionsGroup getRestrictionsGroup();
    
}
