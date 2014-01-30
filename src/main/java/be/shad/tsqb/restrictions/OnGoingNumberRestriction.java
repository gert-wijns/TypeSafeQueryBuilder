package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionImpl.EQUAL;

import java.util.Collection;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.values.CollectionTypeSafeValue;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

public class OnGoingNumberRestriction {
	private final RestrictionImpl restriction;
	private final static String LESS_THAN_EQUAL = "<=";
	private final static String LESS_THAN = "<";
	private final static String GREATER_THAN = ">";
	private final static String GREATER_THAN_EQUAL = ">=";

	public OnGoingNumberRestriction(RestrictionImpl restriction, Number argument) {
		this.restriction = restriction;
		restriction.setLeft(toValue(argument));
	}

	public OnGoingNumberRestriction(RestrictionImpl restriction, TypeSafeValue<Number> argument) {
		this.restriction = restriction;
		restriction.setLeft(argument);
	}
	
	public Restriction in(Collection<Number> values) {
		return in(new CollectionTypeSafeValue<>(values));
	}

	public Restriction eq(TypeSafeValue<Number> value) {
		restriction.setOperator(EQUAL);
		restriction.setRight(value);
		return restriction;
	}
	
	public Restriction eq(Number value) {
		return eq(toValue(value));
	}

	public Restriction lt(Number value) {
		return lt(toValue(value));
	}
	
	public Restriction lt(TypeSafeValue<Number> value) {
		restriction.setOperator(LESS_THAN);
		restriction.setRight(value);
		return restriction;
	}

	public Restriction gt(Number value) {
		return gt(toValue(value));
	}
	
	public Restriction gt(TypeSafeValue<Number> value) {
		restriction.setOperator(GREATER_THAN);
		restriction.setRight(value);
		return restriction;
	}
	
	public Restriction lte(Number value) {
		return lte(toValue(value));
	}
	
	public Restriction lte(TypeSafeValue<Number> value) {
		restriction.setOperator(LESS_THAN_EQUAL);
		restriction.setRight(value);
		return restriction;
	}

	public Restriction gte(Number value) {
		return gte(toValue(value));
	}
	
	public Restriction gte(TypeSafeValue<Number> value) {
		restriction.setOperator(GREATER_THAN_EQUAL);
		restriction.setRight(value);
		return restriction;
	}

	public Restriction not(Number value) {
		return not(toValue(value));
	}
	
	public Restriction not(TypeSafeValue<Number> value) {
		restriction.setOperator(RestrictionImpl.NOT_EQUAL);
		restriction.setRight(value);
		return restriction;
	}

	public Restriction in(Number value) {
		return in(toValue(value));
	}
	
	public Restriction in(TypeSafeValue<Number> value) {
		restriction.setOperator(RestrictionImpl.IN);
		restriction.setRight(value);
		return restriction;
	}

	public Restriction notIn(Number value) {
		return notIn(toValue(value));
	}
	
	public Restriction notIn(TypeSafeValue<Number> value) {
		restriction.setOperator(RestrictionImpl.NOT_IN);
		restriction.setRight(value);
		return restriction;
	}
	
	private TypeSafeValue<Number> toValue(Number value) {
		List<TypeSafeQueryProxyData> invocations = restriction.getQuery().dequeueInvocations();
		if( invocations.isEmpty() ) {
			// direct selection
			return new DirectTypeSafeValue<>(value);
		} else if( invocations.size() == 1 ) {
			// invoked with proxy
			return new ReferenceTypeSafeValue<Number>(invocations.get(0));
		} else {
			// invalid call, only expected one invocation
			throw new IllegalStateException();
		}
	}

}
