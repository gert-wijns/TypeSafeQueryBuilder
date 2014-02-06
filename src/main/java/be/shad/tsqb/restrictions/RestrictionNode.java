package be.shad.tsqb.restrictions;

/**
 * A node in the chain of restrictions.
 * The type can be null in case it is the first restriction in the chain.
 * <p>
 * This class is most or only for internal use and is only used in the RestrictionsGroup.
 */
public class RestrictionNode {
    private Restriction restriction;
    private RestrictionNodeType type;
    
    public RestrictionNode(Restriction restriction, RestrictionNodeType type) {
        this.restriction = restriction;
        this.type = type;
    }

    public Restriction getRestriction() {
        return restriction;
    }
    
    public RestrictionNodeType getType() {
        return type;
    }

}
