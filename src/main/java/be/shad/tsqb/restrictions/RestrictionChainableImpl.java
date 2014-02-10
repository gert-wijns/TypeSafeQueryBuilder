package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionImpl.EXISTS;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.TypeSafeValue;

public abstract class RestrictionChainableImpl implements RestrictionChainable, RestrictionProvider {

    private RestrictionChainable exists(RestrictionImpl restriction, TypeSafeSubQuery<?> subquery) {
        restriction.setRight(subquery);
        restriction.setLeft(null);
        restriction.setOperator(EXISTS);
        return restriction;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable andExists(TypeSafeSubQuery<?> subquery) {
        return exists(and(), subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable orExists(TypeSafeSubQuery<?> subquery) {
        return exists(or(), subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> and(E value) {
        return new OnGoingEnumRestriction<E>(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> andEnum(TypeSafeValue<E> value) {
        return new OnGoingEnumRestriction<E>(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction and(String value) {
        return new OnGoingTextRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction andString(TypeSafeValue<String> value) {
        return new OnGoingTextRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction and(Boolean value) {
        return new OnGoingBooleanRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction andBoolean(TypeSafeValue<Boolean> value) {
        return new OnGoingBooleanRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction and(Number value) {
        return new OnGoingNumberRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction andNumber(TypeSafeValue<Number> value) {
        return new OnGoingNumberRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction and(Date value) {
        return new OnGoingDateRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction andDate(TypeSafeValue<Date> value) {
        return new OnGoingDateRestriction(and(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction or(String value) {
        return new OnGoingTextRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingTextRestriction orString(TypeSafeValue<String> value) {
        return new OnGoingTextRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction or(Number value) {
        return new OnGoingNumberRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingNumberRestriction orNumber(TypeSafeValue<Number> value) {
        return new OnGoingNumberRestriction(or(), value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction or(Boolean value) {
        return new OnGoingBooleanRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction or(Date value) {
        return new OnGoingDateRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingBooleanRestriction orBoolean(TypeSafeValue<Boolean> value) {
        return new OnGoingBooleanRestriction(or(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingDateRestriction orDate(TypeSafeValue<Date> value) {
        return new OnGoingDateRestriction(or(), value);
    }
    
}
