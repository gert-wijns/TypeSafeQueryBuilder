package be.shad.tsqb.query;

import be.shad.tsqb.values.TypeSafeValue;

public interface TypeSafeQueryScopeValidator {

    /**
     * Validates if the value is available in the scope of a query (+join)
     */
    void validateInScope(TypeSafeValue<?> value);
    
}
