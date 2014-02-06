package be.shad.tsqb.values;

import java.util.List;

import be.shad.tsqb.query.TypeSafeQueryInternal;

public class CustomTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private String hql;
    private List<Object> params;

    protected CustomTypeSafeValue(
            TypeSafeQueryInternal query, Class<T> valueType, 
            String hql, List<Object> params) {
        super(query, valueType);
        this.hql = hql;
        this.params = params;
    }

    @Override
    public HqlQueryValue toHqlQueryValue() {
        return new HqlQueryValueImpl(hql, params);
    }

}
