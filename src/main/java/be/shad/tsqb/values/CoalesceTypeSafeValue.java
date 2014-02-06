package be.shad.tsqb.values;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.query.TypeSafeQueryInternal;

public class CoalesceTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private List<TypeSafeValue<T>> values = new LinkedList<>();
    
    public CoalesceTypeSafeValue(TypeSafeQueryInternal query, Class<T> valueType) {
        super(query, valueType);
    }

    public CoalesceTypeSafeValue<T> or(T value) {
        return or(query.toValue(value));
    }
    
    public CoalesceTypeSafeValue<T> or(TypeSafeValue<T> value) {
        this.values.add(value);
        return this;
    }

    @Override
    public HqlQueryValue toHqlQueryValue() {
        StringBuilder coalesce = new StringBuilder();
        List<Object> params = new LinkedList<>();
        for(TypeSafeValue<T> value: values) {
            if( coalesce.length() > 0 ) {
                coalesce.append(",");
            } else {
                coalesce.append("coalesce (");
            }
            HqlQueryValue valueHql = value.toHqlQueryValue();
            coalesce.append(valueHql.getHql());
            for(Object param: valueHql.getParams()) {
                params.add(param);
            }
        }
        String hql = "";
        if( coalesce.length() > 0 ) {
            hql = coalesce.append(")").toString();
        }
        return new HqlQueryValueImpl(hql, params);
    }

}
