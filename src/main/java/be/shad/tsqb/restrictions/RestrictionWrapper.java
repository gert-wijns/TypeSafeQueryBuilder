package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;

/**
 * Wraps a restriction in brackets.
 */
public class RestrictionWrapper extends RestrictionChainableImpl implements Restriction {
    private final RestrictionsGroup group;
    private final Restriction restriction;
    
    public RestrictionWrapper(RestrictionsGroup group,
            Restriction restriction) {
        this.restriction = restriction;
        this.group = group;
    }

    @Override
    public Restriction and(Restriction restriction) {
        return group.and(restriction);
    }

    @Override
    public Restriction or(Restriction restriction) {
        return group.or(restriction);
    }

    @Override
    public RestrictionImpl and() {
        return group.and();
    }

    @Override
    public RestrictionImpl or() {
        return group.or();
    }

    @Override
    public HqlQueryValue toHqlQueryValue() {
        HqlQueryValue value = restriction.toHqlQueryValue();
        return new HqlQueryValueImpl("(" + value.getHql() + ")", value.getParams());
    }

    @Override
    public RestrictionsGroup getRestrictionsGroup() {
        return group;
    }

}
