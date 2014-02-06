package be.shad.tsqb.grouping;

import java.util.Date;

import be.shad.tsqb.values.TypeSafeValue;

public interface OnGoingGroupBy {

    /**
     * Converts to a TypeSafeValue and delegates to {@link #and(TypeSafeValue)}.
     */
    OnGoingGroupBy and(Number val);

    /**
     * Converts to a TypeSafeValue and delegates to {@link #and(TypeSafeValue)}.
     */
    OnGoingGroupBy and(String val);

    /**
     * Converts to a TypeSafeValue and delegates to {@link #and(TypeSafeValue)}.
     */
    OnGoingGroupBy and(Enum<?> val);

    /**
     * Converts to a TypeSafeValue and delegates to {@link #and(TypeSafeValue)}.
     */
    OnGoingGroupBy and(Boolean val);

    /**
     * Converts to a TypeSafeValue and delegates to {@link #and(TypeSafeValue)}.
     */
    OnGoingGroupBy and(Date val);

    /**
     * Adds the value to the list of values to group by.
     */
    OnGoingGroupBy and(TypeSafeValue<?> val);
    
}
