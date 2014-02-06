package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQueryInternal;

/**
 * Represents a case when() then ... else ... end.
 */
public class CaseTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    // TODO add implementation (currently javadoc-ing)

    protected CaseTypeSafeValue(TypeSafeQueryInternal query, Class<T> valueType) {
        super(query, valueType);
    }

    @Override
    public HqlQueryValue toHqlQueryValue() {
        return null;
    }

}
