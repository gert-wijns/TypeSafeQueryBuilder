package be.shad.tsqb.query;

import be.shad.tsqb.values.CaseTypeSafeValue;
import be.shad.tsqb.values.CustomTypeSafeValue;

public interface TypeSafeQueryValues {

    /**
     * Creates a caseWhen with the given valueClass. Shorthand for <br>
     * new CaseTypeSafeValue<>(query, valueClass)<br>
     * query.values().caseWhen(valueClass);
     */
    <T> CaseTypeSafeValue<T> caseWhen(Class<T> valueType);
    
    /**
     * Creates a custom value with the given hql and params.
     */
    <T> CustomTypeSafeValue<T> custom(Class<T> valueType, String hql, Object... params);
    
}
