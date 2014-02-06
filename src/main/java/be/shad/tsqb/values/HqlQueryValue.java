package be.shad.tsqb.values;


public interface HqlQueryValue {

    /**
     * The hql to append to an hqlQuery.
     */
    String getHql();
    
    /**
     * The params to append to an hqlQuery.
     */
    Object[] getParams();
    
}
