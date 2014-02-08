package be.shad.tsqb.values;


public interface OnGoingCaseWhen<T> {

    /**
     * Defines the value to return in a certain case.
     * Allows additional when's to be added.
     */
    OnGoingCaseWhen<T> then(TypeSafeValue<T> value);
    
    /**
     * Delegates to {@link #then(TypeSafeValue)} with a converted value;
     */
    OnGoingCaseWhen<T> then(T value);

    /**
     * Defines the value to return in the else part of a (case when ... then ... (else ...) end).
     * The end() is implicitely called.
     */
    TypeSafeValue<T> otherwise(TypeSafeValue<T> value);
    
    /**
     * Delegates to {@link #otherwise(TypeSafeValue)} with a converted value;
     */
    TypeSafeValue<T> otherwise(T value);
    
    /**
     * Stop chaining and return the case when value representative.
     */
    TypeSafeValue<T> end();
    
}
