package be.shad.tsqb.ordering;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.values.TypeSafeValue;

public class OrderByBase implements OrderBy {
    private TypeSafeValue<?> value;
    private boolean descending;
    
    public OrderByBase(TypeSafeValue<?> value, boolean descending) {
        this.value = value;
        this.descending = descending;
    }

    @Override
    public void appendTo(HqlQuery query) {
        //ascending is the default
        String order = descending ? " desc": "";
        query.appendOrderBy(value.toHqlQueryValue().getHql() + order);
    }
    
}
