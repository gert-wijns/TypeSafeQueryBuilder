package be.shad.tsqb.restrictions;

/**
 * 
 */
public interface RestrictionProvider {

    /**
     * Creates a new restriction and adds it as 'and' to the existing chain.
     */
    RestrictionImpl and();

    /**
     * Creates a new restriction and adds it as 'or' to the existing chain.
     */
    RestrictionImpl or();
    
}
