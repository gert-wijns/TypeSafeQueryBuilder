package be.shad.tsqb.restrictions;

import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.TypeSafeValue;

public interface RestrictionChainable {
	
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
	 * The general restrict by number method. Anything which represents a number
	 * can be used with this method.
	 */
	OnGoingNumberRestriction andn(TypeSafeValue<Number> value);

	/**
	 * Restrict starting with a subquery, more specific than {@link #restrictn(TypeSafeValue)},
	 * it has additional restrictions only available when subquerying.
	 */
	OnGoingSubQueryNumberRestriction andn(TypeSafeSubQuery<Number> value);

	/**
	 * Restrict a number value. This can be a direct value (an actual string),
	 * or a value of a TypeSafeQueryProxy getter. 
	 */
	OnGoingNumberRestriction and(Number value);

	/**
	 * The general restrict by number method. Anything which represents a number
	 * can be used with this method.
	 */
	OnGoingTextRestriction andt(TypeSafeValue<String> value);

	/**
	 * Restrict starting with a subquery, more specific than {@link #restrictt(TypeSafeValue)},
	 * it has additional restrictions only available when subquerying.
	 */
	OnGoingSubQueryTextRestriction andt(TypeSafeSubQuery<String> value);

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
	 * Restrict starting with a subquery, more specific than {@link #restrictn(TypeSafeValue)},
	 * it has additional restrictions only available when subquerying.
	 */
	OnGoingSubQueryNumberRestriction orn(TypeSafeSubQuery<Number> value);

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
	 * Restrict starting with a subquery, more specific than {@link #restrictt(TypeSafeValue)},
	 * it has additional restrictions only available when subquerying.
	 */
	OnGoingSubQueryTextRestriction ort(TypeSafeSubQuery<String> value);

	/**
	 * Restrict a string value. This can be a direct value (an actual string),
	 * or a value of a TypeSafeQueryProxy getter. 
	 */
	OnGoingTextRestriction or(String value);
	
}
