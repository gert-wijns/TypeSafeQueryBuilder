package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionImpl.EQUAL;
import static be.shad.tsqb.restrictions.RestrictionImpl.IN;
import static be.shad.tsqb.restrictions.RestrictionImpl.NOT_EQUAL;
import static be.shad.tsqb.restrictions.RestrictionImpl.NOT_IN;

import java.util.Collection;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.values.CollectionTypeSafeValue;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

public class OnGoingTextRestriction {
	private final RestrictionImpl restriction;
	private final static String WILDCARD = "%";
	private final static String EMPTY = "";
	private final static String LIKE = "like";

	public OnGoingTextRestriction(RestrictionImpl restriction, String argument) {
		this.restriction = restriction;
		restriction.setLeft(toValue(EMPTY, argument, EMPTY));
	}
	
	public OnGoingTextRestriction(RestrictionImpl restriction, TypeSafeValue<String> argument) {
		this.restriction = restriction;
		restriction.setLeft(argument);
	}

	public Restriction in(TypeSafeValue<String> value) {
		restriction.setOperator(IN);
		restriction.setRight(value);
		return restriction;
	}
	
	public Restriction in(Collection<String> values) {
		return in(new CollectionTypeSafeValue<>(values));
	}

	public Restriction notIn(TypeSafeValue<String> value) {
		restriction.setOperator(NOT_IN);
		restriction.setRight(value);
		return restriction;
	}
	
	public Restriction notIn(Collection<String> values) {
		return notIn(new CollectionTypeSafeValue<>(values));
	}

	public Restriction eq(TypeSafeValue<String> value) {
		restriction.setOperator(EQUAL);
		restriction.setRight(value);
		return restriction;
	}
	
	public Restriction eq(String value) {
		return eq(toValue(EMPTY, value, EMPTY));
	}

	public Restriction not(TypeSafeValue<String> value) {
		restriction.setOperator(NOT_EQUAL);
		restriction.setRight(value);
		return restriction;
	}
	
	public Restriction not(String value) {
		return not(toValue(EMPTY, value, EMPTY));
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
