package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQuery;

/**
 * Represents a case when() then ... else ... end.
 */
public class CaseTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    // TODO add implementation (currently javadoc-ing)

    protected CaseTypeSafeValue(TypeSafeQuery query, Class<T> valueType) {
        super(query, valueType);
    }

    @Override
    public HqlQueryValue toHqlQueryValue() {
        return null;
    }

}
