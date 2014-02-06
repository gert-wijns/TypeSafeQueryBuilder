package be.shad.tsqb.selection;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeValueProjection implements TypeSafeProjection {
    private final TypeSafeValue<?> value;
    private final String propertyName;

    public TypeSafeValueProjection(TypeSafeValue<?> value, String propertyName) {
        this.value = value;
        this.propertyName = propertyName;
    }

    @Override
    public void appendTo(HqlQuery query) {
        HqlQueryValue val = value.toHqlQueryValue();
        query.appendSelect(val.getHql() + (propertyName == null ? "": " as " + propertyName));
        query.addParams(val.getParams());
    }

}
