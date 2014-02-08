package be.shad.tsqb.values;

import be.shad.tsqb.restrictions.Restriction;

public interface OnGoingCase<T> {

    /**
     * Provide a when ( some expression evaluates true ) case.
     */
    OnGoingCaseWhen<T> when(Restriction restriction);
    
}
