package be.shad.tsqb.selection;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Couples a value to a selection alias.
 * <p>
 * The value can be anything, created by a function, a custom value, a subquery, a referenced value and so on.
 * <p>
 * The propertyName represents the alias of this value in the select clause.
 */
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
