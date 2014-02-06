package be.shad.tsqb.restrictions;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.TypeSafeValue;

public interface RestrictionChainable {

    /**
     * Adds the 'and exists(subquery)' to the chain.
     */
    RestrictionChainable andExists(TypeSafeSubQuery<?> subquery);

    /**
     * Adds the 'or exists(subquery)' to the chain.
     */
    RestrictionChainable orExists(TypeSafeSubQuery<?> subquery);
    
    /**
     * Add a restriction, the restriction is returned to continue chaining.
     * Use this to add groups of restrictions (useful when using ´or´s in a query).
     */
    Restriction and(Restriction restriction);
    
    /**
     * Add a restriction, the restriction is returned to continue chaining.
     * Use this to add groups of restrictions (useful when using ´or´s in a query).
     */
    Restriction or(Restriction restriction);

    /**
     * The general restrict by enum method. Anything which represents a number
     * can be used with this method.
     */
    <E extends Enum<E>> OnGoingEnumRestriction<E> ande(TypeSafeValue<E> value);

    /**
     * Restrict an enum value. This can be a direct value (an actual enum value),
     * or a value of a TypeSafeQueryProxy getter.
     */
    <E extends Enum<E>> OnGoingEnumRestriction<E> and(E value);
    
    /**
     * The general restrict by number method. Anything which represents a number
     * can be used with this method.
     */
    OnGoingBooleanRestriction andb(TypeSafeValue<Boolean> value);

    /**
     * Restrict a number value. This can be a direct value (an actual string),
     * or a value of a TypeSafeQueryProxy getter. 
     */
    OnGoingBooleanRestriction and(Boolean value);

    /**
     * The general restrict by number method. Anything which represents a number
     * can be used with this method.
     */
    OnGoingNumberRestriction andn(TypeSafeValue<Number> value);

    /**
     * Restrict a number value. This can be a direct value (an actual string),
     * or a value of a TypeSafeQueryProxy getter. 
     */
    OnGoingNumberRestriction and(Number value);

    /**
     * The general restrict by date method. Anything which represents a number
     * can be used with this method.
     */
    OnGoingDateRestriction andd(TypeSafeValue<Date> value);

    /**
     * Restrict a number value. This can be a direct value (an actual string),
     * or a value of a TypeSafeQueryProxy getter. 
     */
    OnGoingDateRestriction and(Date value);
    
    /**
     * The general restrict by number method. Anything which represents a number
     * can be used with this method.
     */
    OnGoingTextRestriction andt(TypeSafeValue<String> value);

    /**
     * Restrict a string value. This can be a direct value (an actual string),
     * or a value of a TypeSafeQueryProxy getter. 
     */
    OnGoingTextRestriction and(String value);

    /**
     * The general restrict by number method. Anything which represents a number
     * can be used with this method.
     */
    OnGoingNumberRestriction orn(TypeSafeValue<Number> value);

    /**
     * Restrict a number value. This can be a direct value (an actual string),
     * or a value of a TypeSafeQueryProxy getter. 
     */
    OnGoingNumberRestriction or(Number value);

    /**
     * The general restrict by number method. Anything which represents a number
     * can be used with this method.
     */
    OnGoingTextRestriction ort(TypeSafeValue<String> value);

    /**
     * Restrict a string value. This can be a direct value (an actual string),
     * or a value of a TypeSafeQueryProxy getter. 
     */
    OnGoingTextRestriction or(String value);
    
}
