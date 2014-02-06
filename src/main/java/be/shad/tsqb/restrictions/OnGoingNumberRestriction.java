package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for numbers. Number specific restrictions are added here.
 * 
 * @see OnGoingRestriction
 */
public class OnGoingNumberRestriction extends OnGoingRestriction<Number> {
    private final static String LESS_THAN_EQUAL = "<=";
    private final static String LESS_THAN = "<";
    private final static String GREATER_THAN = ">";
    private final static String GREATER_THAN_EQUAL = ">=";

    public OnGoingNumberRestriction(RestrictionImpl restriction, Number argument) {
        super(restriction, argument);
    }

    public OnGoingNumberRestriction(RestrictionImpl restriction, TypeSafeValue<Number> argument) {
        super(restriction, argument);
    }

    /**
     * Generates: left < (referencedValue or actualValue)
     */
    public Restriction lt(Number value) {
        return lt(toValue(value));
    }

    /**
     * Generates: left < numberRepresentative
     */
    public Restriction lt(TypeSafeValue<Number> value) {
        restriction.setOperator(LESS_THAN);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left > (referencedValue or actualValue)
     */
    public Restriction gt(Number value) {
        return gt(toValue(value));
    }

    /**
     * Generates: left > numberRepresentative
     */
    public Restriction gt(TypeSafeValue<Number> value) {
        restriction.setOperator(GREATER_THAN);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    public Restriction lte(Number value) {
        return lte(toValue(value));
    }

    /**
     * Generates: left <= numberRepresentative
     */
    public Restriction lte(TypeSafeValue<Number> value) {
        restriction.setOperator(LESS_THAN_EQUAL);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left >= (referencedValue or actualValue)
     */
    public Restriction gte(Number value) {
        return gte(toValue(value));
    }

    /**
     * Generates: left >= numberRepresentative
     */
    public Restriction gte(TypeSafeValue<Number> value) {
        restriction.setOperator(GREATER_THAN_EQUAL);
        restriction.setRight(value);
        return restriction;
    }

}
