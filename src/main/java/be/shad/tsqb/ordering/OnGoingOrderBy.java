package be.shad.tsqb.ordering;

import java.util.Date;

import be.shad.tsqb.values.TypeSafeValue;

public interface OnGoingOrderBy {

    OnGoingOrderBy desc(Number val);

    OnGoingOrderBy desc(String val);
    
    OnGoingOrderBy desc(Enum<?> val);

    OnGoingOrderBy desc(Boolean val);

    OnGoingOrderBy desc(Date val);
    
    OnGoingOrderBy desc(TypeSafeValue<?> val);

    OnGoingOrderBy asc(Number val);

    OnGoingOrderBy asc(String val);
    
    OnGoingOrderBy asc(Enum<?> val);

    OnGoingOrderBy asc(Boolean val);

    OnGoingOrderBy asc(Date val);
    
    OnGoingOrderBy asc(TypeSafeValue<?> val);
    
}
