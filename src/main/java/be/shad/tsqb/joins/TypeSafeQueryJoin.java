package be.shad.tsqb.joins;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeQueryJoin<T> {
    private final TypeSafeQueryProxyData data;
    private final RestrictionsGroup restrictions;

    public TypeSafeQueryJoin(TypeSafeQueryInternal query, TypeSafeQueryProxyData data) {
        this.restrictions = new RestrictionsGroup(query, data);
        this.data = data;
    }
    
    @SuppressWarnings("unchecked")
    public T getProxy() {
        return (T) data.getProxy();
    }
    
    public TypeSafeQueryProxyData getData() {
        return data;
    }
    
    public RestrictionsGroup getRestrictions() {
        return restrictions;
    }
    
    /**
     * 
     */
    public RestrictionChainable with() {
        return restrictions;
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingNumberRestriction with(Number value) {
        return restrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingTextRestriction with(String value) {
        return restrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingNumberRestriction withn(TypeSafeValue<Number> value) {
        return restrictions.andn(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingTextRestriction witht(TypeSafeValue<String> value) {
        return restrictions.andt(value);
    }
    
    
    
}
