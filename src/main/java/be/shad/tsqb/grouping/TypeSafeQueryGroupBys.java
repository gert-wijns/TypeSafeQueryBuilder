package be.shad.tsqb.grouping;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeQueryGroupBys implements HqlQueryBuilder, OnGoingGroupBy {
    private final List<TypeSafeValue<?>> values = new LinkedList<>();
    private final TypeSafeQueryInternal query;
    
    public TypeSafeQueryGroupBys(TypeSafeQueryInternal query) {
        this.query = query;
    }

    @Override
    public void appendTo(HqlQuery query) {
        for(TypeSafeValue<?> value: values) {
            HqlQueryValue hqlQueryValue = value.toHqlQueryValue();
            query.appendGroupBy(hqlQueryValue.getHql());
            query.addParams(hqlQueryValue.getParams());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(Number val) {
        return add(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(String val) {
        return add(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(Enum<?> val) {
        return add(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(Boolean val) {
        return add(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(Date val) {
        return add(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingGroupBy and(TypeSafeValue<?> val) {
        return add(val);
    }
    
    /**
     * Adds to the list and returns this to support method chaining.
     */
    private OnGoingGroupBy add(Object val) {
        values.add(query.toValue(val));
        return this;
    }

}
