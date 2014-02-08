package be.shad.tsqb.values;

import java.util.List;

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryInternal;

/**
 * When the TypeSafeQueryBuilder doesn't support a certain hql construction,
 * this custom value may be used to inject hql with params into the query.
 */
public class CustomTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private String hql;
    private List<Object> params;

    public CustomTypeSafeValue(
            TypeSafeQuery query, Class<T> valueType, 
            String hql, List<Object> params) {
        super((TypeSafeQueryInternal) query, valueType);
        this.hql = hql;
        this.params = params;
    }

    @Override
    public HqlQueryValue toHqlQueryValue() {
        return new HqlQueryValueImpl(hql, params);
    }

}
