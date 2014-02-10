package be.shad.tsqb.restrictions;

/**
 * An implementation where all methods are delegates to a group.
 */
public abstract class RestrictionChainableDelegatingImpl extends RestrictionChainableImpl {

    private final RestrictionsGroupInternal group;
    
    public RestrictionChainableDelegatingImpl(RestrictionsGroupInternal group) {
        this.group = group;
    }

    public RestrictionsGroupInternal getRestrictionsGroup() {
        return group;
    }

    public RestrictionImpl and() {
        return group.and();
    }

    public RestrictionImpl or() {
        return group.or();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Restriction and(Restriction restriction) {
        return group.and(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable and(RestrictionsGroup group) {
        return this.group.and(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Restriction or(Restriction restriction) {
        return group.or(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable or(RestrictionsGroup group) {
        return this.group.or(group);
    }
    
}
