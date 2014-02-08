package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryScopeValidator;

/**
 * Wraps a value in a function. 
 * Examples uses are {@link TypeSafeValueFunctions#sum(Number) sum(...)},
 * {@link TypeSafeValueFunctions#max(Number) max(...)} etc.
 */
public class WrappedTypeSafeValue<T> extends TypeSafeValueImpl<T> implements TypeSafeValueContainer {
    private String function; // sum/max/min/trim/count/...
    private TypeSafeValue<T> value;
    
    public WrappedTypeSafeValue(TypeSafeQuery query, String function, TypeSafeValue<T> value) {
        super(query, value.getValueClass());
        this.function = function;
        this.value = value;
    }
    
    @Override
    public HqlQueryValue toHqlQueryValue() {
        HqlQueryValue value = this.value.toHqlQueryValue();
        return new HqlQueryValueImpl(function + "("+value.getHql()+")", value.getParams());
    }
    
    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        validator.validateInScope(value);
    }
}
