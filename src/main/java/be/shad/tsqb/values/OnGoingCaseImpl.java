package be.shad.tsqb.values;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.restrictions.OnGoingBooleanRestriction;
import be.shad.tsqb.restrictions.OnGoingDateRestriction;
import be.shad.tsqb.restrictions.OnGoingEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.Restriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.restrictions.RestrictionChainableDelegatingImpl;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.restrictions.RestrictionsGroupInternal;

public class OnGoingCaseImpl<T> extends RestrictionChainableDelegatingImpl implements OnGoingCase<T> {
    private final TypeSafeValue<T> value;

    public OnGoingCaseImpl(RestrictionsGroupInternal group, TypeSafeValue<T> value) {
        super(group);
        this.value = value;
    }

    public TypeSafeValue<T> getValue() {
        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable when() {
        return getRestrictionsGroup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable when(HqlQueryValue restriction) {
        return and(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable when(RestrictionsGroup group) {
        return and(group.getRestrictions());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Restriction when(Restriction restriction) {
        return and(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable whenExists(TypeSafeSubQuery<?> subquery) {
        return andExists(subquery);
    }
    
    /**
     * Delegate to 
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> when(E value) {
        return and(value);
    }

    /**
     * Delegate to 
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> whenEnum(TypeSafeValue<E> value) {
        return andEnum(value);
    }
    
    /**
     * Delegate to 
     */
    @Override
    public OnGoingBooleanRestriction when(Boolean value) {
        return and(value);
    }

    /**
     * Delegate to 
     */
    @Override
    public OnGoingBooleanRestriction whenBoolean(TypeSafeValue<Boolean> value) {
        return andBoolean(value);
    }
    
    /**
     * Delegate to 
     */
    @Override
    public OnGoingNumberRestriction when(Number value) {
        return and(value);
    }

    /**
     * Delegate to 
     */
    @Override
    public OnGoingTextRestriction when(String value) {
        return and(value);
    }

    /**
     * Delegate to 
     */
    @Override
    public <N extends Number> OnGoingNumberRestriction whenNumber(TypeSafeValue<N> value) {
        return andNumber(value);
    }

    /**
     * Delegate to 
     */
    @Override
    public OnGoingTextRestriction whenString(TypeSafeValue<String> value) {
        return andString(value);
    }
    
    /**
     * Delegate to 
     */
    @Override
    public OnGoingDateRestriction when(Date value) {
        return and(value);
    }

    /**
     * Delegate to 
     */
    @Override
    public OnGoingDateRestriction whenDate(TypeSafeValue<Date> value) {
        return andDate(value);
    }

    @Override
    public TypeSafeValue<T> otherwise() {
        // TODO Auto-generated method stub
        return null;
    }
}
