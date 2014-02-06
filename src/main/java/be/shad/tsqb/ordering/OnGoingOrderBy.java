package be.shad.tsqb.ordering;

import java.util.Date;

import be.shad.tsqb.values.TypeSafeValue;

public interface OnGoingOrderBy {

    /**
     * Converts to a TypesafeValue and delegates to {@link #desc(TypeSafeValue)}
     */
    OnGoingOrderBy desc(Number val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #desc(TypeSafeValue)}
     */
    OnGoingOrderBy desc(String val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #desc(TypeSafeValue)}
     */
    OnGoingOrderBy desc(Enum<?> val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #desc(TypeSafeValue)}
     */
    OnGoingOrderBy desc(Boolean val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #desc(TypeSafeValue)}
     */
    OnGoingOrderBy desc(Date val);

    /**
     * Adds the value to the list of order bys with the notion that it is ordered descending.
     */
    OnGoingOrderBy desc(TypeSafeValue<?> val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #asc(TypeSafeValue)}
     */
    OnGoingOrderBy asc(Number val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #asc(TypeSafeValue)}
     */
    OnGoingOrderBy asc(String val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #asc(TypeSafeValue)}
     */
    OnGoingOrderBy asc(Enum<?> val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #asc(TypeSafeValue)}
     */
    OnGoingOrderBy asc(Boolean val);

    /**
     * Converts to a TypesafeValue and delegates to {@link #asc(TypeSafeValue)}
     */
    OnGoingOrderBy asc(Date val);

    /**
     * Adds the value to the list of order bys with the notion that it is ordered ascending.
     */
    OnGoingOrderBy asc(TypeSafeValue<?> val);
    
}
