package be.shad.tsqb.values;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;

/**
 * The proxy data represents a getter on one of the proxies
 * created while building a query.
 * <p>
 * The data can be converted to a property path by calling its getAlias method.
 */
public class ReferenceTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private final TypeSafeQueryProxyData data;
    
    @SuppressWarnings("unchecked")
    public ReferenceTypeSafeValue(TypeSafeQueryInternal query, TypeSafeQueryProxyData data) {
        super(query, (Class<T>) data.getPropertyType());
        this.data = data;
    }
    
    public TypeSafeQueryProxyData getData() {
        return data;
    }
    
    @Override
    public HqlQueryValue toHqlQueryValue() {
        return new HqlQueryValueImpl(data.getAlias());
    }

}
