package be.shad.tsqb.values;

public interface HqlQueryValueBuilder {
    
    /**
     * Convert an object to a HqlQueryValue,
     * so it can be appended to an HqlQuery.
     */
    HqlQueryValue toHqlQueryValue();

}
