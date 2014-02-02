package be.shad.tsqb.restrictions;

import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

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

	public Restriction contains(TypeSafeValue<String> value) {
		restriction.setOperator(LIKE);
		restriction.setRight(value);
		return restriction;
	}
	
	public Restriction contains(String value) {
		return contains(toValue(WILDCARD, value, WILDCARD));
	}
	
	public Restriction startsWith(TypeSafeValue<String> value) {
		restriction.setOperator(LIKE);
		restriction.setRight(value);
		return restriction;
	}
	
	public Restriction startsWith(String value) {
		return startsWith(toValue(WILDCARD, value, EMPTY));
	}

	public Restriction endsWith(TypeSafeValue<String> value) {
		restriction.setOperator(LIKE);
		restriction.setRight(value);
		return restriction;
	}
	
	public Restriction endsWith(String value) {
		return endsWith(toValue(EMPTY, value, WILDCARD));
	}
	
	private TypeSafeValue<String> toValue(String left, String value, String right) {
		List<TypeSafeQueryProxyData> invocations = restriction.getQuery().dequeueInvocations();
		if( invocations.isEmpty() ) {
			// direct selection
			return new DirectTypeSafeValue<>(left + value + right);
		} else if( invocations.size() == 1 ) {
			if( left.length() != 0 || right.length() != 0 ) {
				throw new UnsupportedOperationException("Like not supported for "
						+ "referenced value [" + invocations.get(0) + "].");
			}
			// invoked with proxy
			return new ReferenceTypeSafeValue<String>(invocations.get(0));
		} else {
			// invalid call, only expected one invocation
			throw new IllegalStateException();
		}
	}

}
