package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQueryInternal;

/**
 * Base implementation of the TypeSafeValue.
 * 
 * It was created to reduce the amount of code duplication.
 */
public abstract class TypeSafeValueImpl<T> implements TypeSafeValue<T> {
    protected final TypeSafeQueryInternal query;
    private final Class<T> valueType;
    
    protected TypeSafeValueImpl(TypeSafeQueryInternal query, Class<T> valueType) {
        this.query = query;
        this.valueType = valueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getValueClass() {
        return valueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T select() {
        return query.getRootQuery().queueValueSelected(this);
    }

}
