package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQueryInternal;

/**
 * The value is an actual value, not a proxy or property path.
 * This value is added as param to the query.
 */
public class DirectTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private T value;
    
    @SuppressWarnings("unchecked")
    public DirectTypeSafeValue(TypeSafeQueryInternal query, T value) {
        super(query, (Class<T>) value.getClass());
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public HqlQueryValueImpl toHqlQueryValue() {
        return new HqlQueryValueImpl("?", value);
    }

}
