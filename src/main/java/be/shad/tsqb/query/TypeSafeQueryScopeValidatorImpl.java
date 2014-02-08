package be.shad.tsqb.query;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.exceptions.ValueNotInScopeException;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;
import be.shad.tsqb.values.TypeSafeValueContainer;

class TypeSafeQueryScopeValidatorImpl implements TypeSafeQueryScopeValidator {
    private final TypeSafeQueryInternal query;
    private final TypeSafeQueryProxyData join;
    
    public TypeSafeQueryScopeValidatorImpl(
            TypeSafeQueryInternal query, 
            TypeSafeQueryProxyData join) {
        this.query = query;
        this.join = join;
    }
    
    /**
     * {@inheritDoc}
     */
    public void validateInScope(TypeSafeValue<?> value) {
        if( value instanceof ReferenceTypeSafeValue<?> ) {
            query.validateInScope(((ReferenceTypeSafeValue<?>) value).getData(), join);
        }
        if( value instanceof TypeSafeQueryInternal ) {
            TypeSafeQueryInternal valueQuery = (TypeSafeQueryInternal) value;
            if( valueQuery.getParentQuery() != query) {
                throw new ValueNotInScopeException(
                        "Subqueries may only be used as direct child of their parent."
                        + query.getRootQuery().toString());
            }
        }
        if( value instanceof TypeSafeValueContainer ) {
            ((TypeSafeValueContainer) value).validateContainedInScope(this);
        }
    }
    
}
