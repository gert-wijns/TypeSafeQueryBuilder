package be.shad.tsqb.restrictions;


/**
 * Groups the Restriction and WhereRestrictions to be able to add a group
 * as a nested restriction group and to provide the where() methods to start
 * chaining.
 */
public interface RestrictionsGroup extends WhereRestrictions {

    /**
     * The RestrictionsGroup doesn't implement the Restriction itself
     * because this would add the RestrictionChainable methods to this interface.
     * <p>
     * To be able to separately build a group, and then add it to the
     * query, use this method to get this group as a Restriction.
     * <p>
     * Usually, the restriction group is built as a chainable and the
     * final return value will already be a restriction.
     */
    Restriction getRestrictions();

}
