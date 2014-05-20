package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Exposes Number related restrictions in addition to the basic restrictions.
 */
public interface OnGoingNumberRestriction extends OnGoingRestriction<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction> {

    /**
     * Generates: left >= numberRepresentative
     */
    ContinuedOnGoingNumberRestriction gte(TypeSafeValue<Number> value);

    /**
     * Generates: left >= (referencedValue or actualValue)
     */
    ContinuedOnGoingNumberRestriction gte(Number value);

    /**
     * Generates: left <= numberRepresentative
     */
    ContinuedOnGoingNumberRestriction lte(TypeSafeValue<Number> value);

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    ContinuedOnGoingNumberRestriction lte(Number value);

    /**
     * Generates: left > numberRepresentative
     */
    ContinuedOnGoingNumberRestriction gt(TypeSafeValue<Number> value);

    /**
     * Generates: left > (referencedValue or actualValue)
     */
    ContinuedOnGoingNumberRestriction gt(Number value);

    /**
     * Generates: left < numberRepresentative
     */
    ContinuedOnGoingNumberRestriction lt(TypeSafeValue<Number> value);

    /**
     * Generates: left < (referencedValue or actualValue)
     */
    ContinuedOnGoingNumberRestriction lt(Number value);

}
