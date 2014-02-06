package be.shad.tsqb.ordering;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeQueryOrderBys implements OnGoingOrderBy, HqlQueryBuilder {
    private final List<OrderBy> orderBys = new LinkedList<>();
    private final TypeSafeQueryInternal query;

    public TypeSafeQueryOrderBys(TypeSafeQueryInternal query) {
        this.query = query;
    }

    private OnGoingOrderBy orderBy(Object val, boolean desc) {
        orderBys.add(new OrderByImpl(query.toValue(val), desc));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(Number val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(String val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(Enum<?> val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(Boolean val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(Date val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy desc(TypeSafeValue<?> val) {
        return orderBy(val, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(Number val) {
        return orderBy(val, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(String val) {
        return orderBy(val, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(Enum<?> val) {
        return orderBy(val, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(Boolean val) {
        return orderBy(val, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(Date val) {
        return orderBy(val, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingOrderBy asc(TypeSafeValue<?> val) {
        return orderBy(val, false);
    }
    
    @Override
    public void appendTo(HqlQuery query) {
        for(OrderBy orderBy: orderBys) {
            orderBy.appendTo(query);
        }
    }

}
