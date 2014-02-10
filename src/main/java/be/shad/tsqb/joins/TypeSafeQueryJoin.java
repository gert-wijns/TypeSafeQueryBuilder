package be.shad.tsqb.joins;

import java.util.Date;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.restrictions.OnGoingBooleanRestriction;
import be.shad.tsqb.restrictions.OnGoingDateRestriction;
import be.shad.tsqb.restrictions.OnGoingEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.Restriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.restrictions.RestrictionsGroupImpl;
import be.shad.tsqb.restrictions.RestrictionsGroupInternal;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Links a proxy data with its restrictions.
 */
public class TypeSafeQueryJoin<T> {
    private final TypeSafeQueryProxyData data;
    private final RestrictionsGroupInternal restrictions;

    public TypeSafeQueryJoin(TypeSafeQueryInternal query, TypeSafeQueryProxyData data) {
        this.restrictions = new RestrictionsGroupImpl(query, data);
        this.data = data;
    }
    
    @SuppressWarnings("unchecked")
    public T getProxy() {
        return (T) data.getProxy();
    }
    
    public TypeSafeQueryProxyData getData() {
        return data;
    }
    
    public Restriction getRestrictions() {
        return restrictions;
    }
    
    /**
     * Kickoff for the restriction chainable.
     * <p>
     * It is probably preferrable to use one of the other with(...)
     * methods, but the result would be the same as calling with().and(...).
     */
    public RestrictionChainable with() {
        return restrictions.getRestrictions();
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingNumberRestriction with(Number value) {
        return with().and(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingTextRestriction with(String value) {
        return with().and(value);
    }
    
    /**
     * Delegate to restrictions.
     */
    public OnGoingBooleanRestriction with(Boolean value) {
        return with().and(value);
    }
    
    /**
     * Delegate to restrictions.
     */
    public OnGoingDateRestriction with(Date value) {
        return with().and(value);
    }
    
    /**
     * Delegate to restrictions.
     */
    public <E extends Enum<E>> OnGoingEnumRestriction<E> with(E value) {
        return with().and(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingNumberRestriction withNumber(TypeSafeValue<Number> value) {
        return with().andNumber(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingTextRestriction withString(TypeSafeValue<String> value) {
        return with().andString(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingDateRestriction withDate(TypeSafeValue<Date> value) {
        return with().andDate(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingBooleanRestriction withBoolean(TypeSafeValue<Boolean> value) {
        return with().andBoolean(value);
    }

    /**
     * Delegate to restrictions.
     */
    public <E extends Enum<E>> OnGoingEnumRestriction<E> withEnum(TypeSafeValue<E> value) {
        return with().andEnum(value);
    }
    
}
