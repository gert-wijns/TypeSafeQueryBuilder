package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryInternal;

/**
 * Base implementation of the TypeSafeValue.
 * 
 * It was created to reduce the amount of code duplication.
 */
public abstract class TypeSafeValueImpl<T> implements TypeSafeValue<T> {
    protected final TypeSafeQueryInternal query;
    private final Class<T> valueType;
    
    protected TypeSafeValueImpl(TypeSafeQuery query, Class<T> valueType) {
        // all queries are internal queries - the internal query just hides some methods from the API 
        this.query = (TypeSafeQueryInternal) query; 
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
