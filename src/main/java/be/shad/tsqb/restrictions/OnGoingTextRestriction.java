package be.shad.tsqb.restrictions;

import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for text. Text specific restrictions are added here.
 * 
 * @see OnGoingRestriction
 */
public class OnGoingTextRestriction extends OnGoingRestriction<String> {
	private final static String WILDCARD = "%";
	private final static String EMPTY = "";
	private final static String LIKE = "like";

	public OnGoingTextRestriction(RestrictionImpl restriction, String argument) {
		super(restriction, argument);
	}
	
	public OnGoingTextRestriction(RestrictionImpl restriction, TypeSafeValue<String> argument) {
		super(restriction, argument);
	}

	/**
	 * Generates: left like stringRepresentative
	 */
	public Restriction like(TypeSafeValue<String> value) {
		restriction.setOperator(LIKE);
		restriction.setRight(value);
		return restriction;
	}
	
	/**
	 * Generates: left like ? with (? = '%value%')
	 */
	public Restriction contains(String value) {
		return like(toValue(WILDCARD, value, WILDCARD));
	}

	/**
	 * Generates: left like ? with (? = 'value%')
	 */
	public Restriction startsWith(String value) {
		return like(toValue(WILDCARD, value, EMPTY));
	}

	/**
	 * Generates: left like ? with (? = '%value')
	 */
	public Restriction endsWith(String value) {
		return like(toValue(EMPTY, value, WILDCARD));
	}
	
	/**
	 * Adds wildcards to the value in case it is a direct value.
	 * Validates no wildcards are used otherwise.
	 */
	private TypeSafeValue<String> toValue(String left, String value, String right) {
		if( value instanceof String ) {
			return toValue(left + value + right);
		}
		if( left.length() != 0 || right.length() != 0 ) {
			List<TypeSafeQueryProxyData> dequeued = restriction.getQuery().dequeueInvocations();
			if( !dequeued.isEmpty() ) {
				throw new UnsupportedOperationException("Like not supported for "
						+ "referenced value [" + dequeued.get(0) + "].");
			}
		}
		TypeSafeValue<String> toValue = toValue(value);
		return toValue;
	}

}
