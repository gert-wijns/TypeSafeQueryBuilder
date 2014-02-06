package be.shad.tsqb.values;


public interface HqlQueryValue {

    String getHql();
    
    Object[] getParams();
    
}
