package be.shad.tsqb.restrictions;

import java.util.Date;

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for dates. Date specific restrictions are added here.
 * 
 * @see OnGoingRestriction
 */
public class OnGoingDateRestriction extends OnGoingRestriction<Date> {
    private final static String LESS_THAN_EQUAL = "<=";
    private final static String LESS_THAN = "<";
    private final static String GREATER_THAN = ">";
    private final static String GREATER_THAN_EQUAL = ">=";

    public OnGoingDateRestriction(RestrictionImpl restriction, Date argument) {
        super(restriction, argument);
    }

    public OnGoingDateRestriction(RestrictionImpl restriction, TypeSafeValue<Date> argument) {
        super(restriction, argument);
    }

    /**
     * Generates: left < (referencedValue or actualValue)
     */
    public Restriction before(Date value) {
        return before(toValue(value));
    }

    /**
     * Generates: left < dateRepresentative
     */
    public Restriction before(TypeSafeValue<Date> value) {
        restriction.setOperator(LESS_THAN);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left > (referencedValue or actualValue)
     */
    public Restriction after(Date value) {
        return after(toValue(value));
    }

    /**
     * Generates: left > dateRepresentative
     */
    public Restriction after(TypeSafeValue<Date> value) {
        restriction.setOperator(GREATER_THAN);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    public Restriction notAfter(Date value) {
        return notAfter(toValue(value));
    }

    /**
     * Generates: left <= dateRepresentative
     */
    public Restriction notAfter(TypeSafeValue<Date> value) {
        restriction.setOperator(LESS_THAN_EQUAL);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left >= (referencedValue or actualValue)
     */
    public Restriction notBefore(Date value) {
        return notBefore(toValue(value));
    }

    /**
     * Generates: left >= dateRepresentative
     */
    public Restriction notBefore(TypeSafeValue<Date> value) {
        restriction.setOperator(GREATER_THAN_EQUAL);
        restriction.setRight(value);
        return restriction;
    }

}
