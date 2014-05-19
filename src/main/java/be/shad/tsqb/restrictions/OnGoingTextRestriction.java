package be.shad.tsqb.restrictions;

import be.shad.tsqb.values.TypeSafeValue;


public interface OnGoingTextRestriction extends OnGoingRestriction<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> {

    /**
     * Generates: left like ? with (? = '%value')
     */
    ContinuedOnGoingTextRestriction endsWith(String value);

    /**
     * Generates: left like ? with (? = 'value%')
     */
    ContinuedOnGoingTextRestriction startsWith(String value);

    /**
     * Generates: left like ? with (? = '%value%')
     */
    ContinuedOnGoingTextRestriction contains(String value);

    /**
     * Generates: left like stringRepresentative
     */
    ContinuedOnGoingTextRestriction like(TypeSafeValue<String> value);

}
