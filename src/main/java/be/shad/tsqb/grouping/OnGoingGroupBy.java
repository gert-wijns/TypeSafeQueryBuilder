package be.shad.tsqb.grouping;

import java.util.Date;

import be.shad.tsqb.values.TypeSafeValue;

public interface OnGoingGroupBy {

    OnGoingGroupBy and(Number val);

    OnGoingGroupBy and(String val);
    
    OnGoingGroupBy and(Enum<?> val);

    OnGoingGroupBy and(Boolean val);

    OnGoingGroupBy and(Date val);
    
    OnGoingGroupBy and(TypeSafeValue<?> val);
    
}
