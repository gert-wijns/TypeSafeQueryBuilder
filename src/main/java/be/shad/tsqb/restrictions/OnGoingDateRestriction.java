package be.shad.tsqb.restrictions;

import java.util.Date;

import be.shad.tsqb.values.TypeSafeValue;

public interface OnGoingDateRestriction extends OnGoingRestriction<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> {

    /**
     * Generates: left >= dateRepresentative
     */
    ContinuedOnGoingDateRestriction notBefore(TypeSafeValue<Date> value);

    /**
     * Generates: left >= (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction notBefore(Date value);

    /**
     * Generates: left <= dateRepresentative
     */
    ContinuedOnGoingDateRestriction notAfter(TypeSafeValue<Date> value);

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction notAfter(Date value);

    /**
     * Generates: left > dateRepresentative
     */
    ContinuedOnGoingDateRestriction after(TypeSafeValue<Date> value);

    /**
     * Generates: left > (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction after(Date value);

    /**
     * Generates: left < dateRepresentative
     */
    ContinuedOnGoingDateRestriction before(TypeSafeValue<Date> value);

    /**
     * Generates: left < (referencedValue or actualValue)
     */
    ContinuedOnGoingDateRestriction before(Date value);

}
