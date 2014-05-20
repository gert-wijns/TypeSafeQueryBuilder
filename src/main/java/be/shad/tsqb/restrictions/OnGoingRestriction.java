package be.shad.tsqb.restrictions;

import java.util.Collection;

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Exposes the basic restrictions available for all value types.
 */
public interface OnGoingRestriction<VAL, CONTINUED extends ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL>, 
        ORIGINAL extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>> {

    /**
     * Generates: left <> (referencedValue or actualValue)
     */
    CONTINUED not(VAL value);

    /**
     * Generates: left <> valueRepresentative
     */
    CONTINUED not(TypeSafeValue<VAL> value);

    /**
     * Generates: left = (referencedValue or actualValue)
     */
    CONTINUED eq(VAL value);

    /**
     * Generates: left = valueRepresentative
     */
    CONTINUED eq(TypeSafeValue<VAL> value);

    /**
     * Generates: left not in ( actualValues )
     */
    <T extends VAL> CONTINUED notIn(Collection<T> values);

    /**
     * Generates: left not in ( valuesRepresentative )
     * <p>
     * Can be used with a TypeSafeSubQuery to check if
     * the left part is not in the subquery results.
     */
    <T extends VAL> CONTINUED notIn(TypeSafeValue<T> value);

    /**
     * Generates: left not in ( actualValues )
     */
    <T extends VAL> CONTINUED in(Collection<T> values);

    /**
     * Generates: left in ( valuesRepresentative )
     * <p>
     * Can be used with a TypeSafeSubQuery to check if
     * the left part is in the subquery results.
     */
    <T extends VAL> CONTINUED in(TypeSafeValue<T> value);

    /**
     * Generates: left is not null
     */
    CONTINUED isNotNull();

    /**
     * Generates: left is null
     */
    CONTINUED isNull();

}
