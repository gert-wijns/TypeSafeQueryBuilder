package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionImpl.EQUAL;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for booleans, boolean specific restrictions are added here.
 * 
 * @see OnGoingRestriction
 */
public class OnGoingBooleanRestriction extends OnGoingRestriction<Boolean> implements RestrictionChainable {

    public OnGoingBooleanRestriction(RestrictionImpl restriction, Boolean argument) {
        super(restriction, argument);
    }

    public OnGoingBooleanRestriction(RestrictionImpl restriction, TypeSafeValue<Boolean> argument) {
        super(restriction, argument);
    }

    /**
     * Generates: left = false
     */
    public Restriction isFalse() {
        restriction.setOperator(EQUAL);
        restriction.setRight(new DirectTypeSafeValue<>(restriction.getQuery(), Boolean.FALSE));
        return restriction;
    }

    /**
     * Generates: left = true
     */
    public Restriction isTrue() {
        restriction.setOperator(EQUAL);
        restriction.setRight(new DirectTypeSafeValue<>(restriction.getQuery(), Boolean.TRUE));
        return restriction;
    }

    @Override
    public RestrictionChainable andExists(TypeSafeSubQuery<?> subquery) {
        return isTrue().andExists(subquery);
    }

    @Override
    public RestrictionChainable orExists(TypeSafeSubQuery<?> subquery) {
        return isTrue().orExists(subquery);
    }

    @Override
    public Restriction and(Restriction restriction) {
        return isTrue().and(restriction);
    }

    @Override
    public Restriction or(Restriction restriction) {
        return isTrue().or(restriction);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> andEnum(TypeSafeValue<E> value) {
        return isTrue().andEnum(value);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> and(E value) {
        return isTrue().and(value);
    }

    @Override
    public OnGoingBooleanRestriction andBoolean(TypeSafeValue<Boolean> value) {
        return isTrue().andBoolean(value);
    }

    @Override
    public OnGoingBooleanRestriction and(Boolean value) {
        return isTrue().and(value);
    }

    @Override
    public OnGoingNumberRestriction andNumber(TypeSafeValue<Number> value) {
        return isTrue().andNumber(value);
    }

    @Override
    public OnGoingNumberRestriction and(Number value) {
        return isTrue().and(value);
    }

    @Override
    public OnGoingDateRestriction andDate(TypeSafeValue<Date> value) {
        return isTrue().andDate(value);
    }

    @Override
    public OnGoingDateRestriction and(Date value) {
        return isTrue().and(value);
    }

    @Override
    public OnGoingTextRestriction andString(TypeSafeValue<String> value) {
        return isTrue().andString(value);
    }

    @Override
    public OnGoingTextRestriction and(String value) {
        return isTrue().and(value);
    }

    @Override
    public OnGoingDateRestriction or(Date value) {
        return isTrue().or(value);
    }

    @Override
    public OnGoingDateRestriction orDate(TypeSafeValue<Date> value) {
        return isTrue().orDate(value);
    }

    @Override
    public OnGoingBooleanRestriction or(Boolean value) {
        return isTrue().or(value);
    }

    @Override
    public OnGoingBooleanRestriction orBoolean(TypeSafeValue<Boolean> value) {
        return isTrue().orBoolean(value);
    }

    @Override
    public OnGoingNumberRestriction orNumber(TypeSafeValue<Number> value) {
        return isTrue().orNumber(value);
    }

    @Override
    public OnGoingNumberRestriction or(Number value) {
        return isTrue().or(value);
    }

    @Override
    public OnGoingTextRestriction orString(TypeSafeValue<String> value) {
        return isTrue().orString(value);
    }

    @Override
    public OnGoingTextRestriction or(String value) {
        return isTrue().or(value);
    }

    @Override
    public RestrictionChainable and(RestrictionsGroup group) {
        return isTrue().and(group);
    }

    @Override
    public RestrictionChainable or(RestrictionsGroup group) {
        return isTrue().or(group);
    }
    
}
