package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQueryScopeValidator;

public interface TypeSafeValueContainer {

    /**
     * Let the validator validate the scope of all nested values.
     */
    void validateContainedInScope(TypeSafeQueryScopeValidator validator);
    
}
