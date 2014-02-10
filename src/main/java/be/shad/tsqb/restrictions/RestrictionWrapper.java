package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;

/**
 * Wraps a restriction in brackets.
 */
public class RestrictionWrapper extends RestrictionChainableDelegatingImpl implements Restriction {
    private final Restriction restriction;
    
    public RestrictionWrapper(RestrictionsGroupInternal group,
            Restriction restriction) {
        super(group);
        this.restriction = restriction;
    }

    @Override
    public HqlQueryValue toHqlQueryValue() {
        HqlQueryValue value = restriction.toHqlQueryValue();
        return new HqlQueryValueImpl("(" + value.getHql() + ")", value.getParams());
    }

}
