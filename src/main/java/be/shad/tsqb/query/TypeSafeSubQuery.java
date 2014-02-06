package be.shad.tsqb.query;

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Represents the selection of a value using a subquery. 
 * The valueType is the generic type T.
 * Subqueries may be nested as deep as needed.
 */
public interface TypeSafeSubQuery<T> extends TypeSafeValue<T>, TypeSafeQuery {
    
    /**
     * Set the value to select.
     * <p>
     * Converts the value to a TypeSafeValue and delegates to {@link #select(TypeSafeValue)}.
     */
    void select(T value);
    
    /**
     * Set the value to select.
     * <p>
     * This method should be called before converting to hql, unless using the exists function.
     */
    void select(TypeSafeValue<T> value);
    
}
