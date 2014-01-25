package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionBase.EQUAL;

import java.util.Collection;
import java.util.List;

import be.shad.tsqb.proxy.TypeSafeQueryProxyData;
import be.shad.tsqb.values.CollectionTypeSafeValue;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

public class OnGoingNumberRestriction {
	private final RestrictionBase restriction;
	private final static String LESS_THAN_EQUAL = "<=";
	private final static String LESS_THAN = "<";
	private final static String GREATER_THAN = ">";
	private final static String GREATER_THAN_EQUAL = ">=";

	public OnGoingNumberRestriction(RestrictionBase restriction, Number argument) {
		this.restriction = restriction;
		restriction.setLeft(toValue(argument));
	}

	public OnGoingNumberRestriction(RestrictionBase restriction, TypeSafeValue<Number> argument) {
		this.restriction = restriction;
		restriction.setLeft(argument);
	}
	
	public RestrictionChainable in(Collection<Number> values) {
		return in(new CollectionTypeSafeValue<>(values));
	}

	public RestrictionChainable eq(TypeSafeValue<Number> value) {
		restriction.setOperator(EQUAL);
		restriction.setRight(value);
		return restriction;
	}
	
	public RestrictionChainable eq(Number value) {
		return eq(toValue(value));
	}

	public RestrictionChainable lt(Number value) {
		return lt(toValue(value));
	}
	
	public RestrictionChainable lt(TypeSafeValue<Number> value) {
		restriction.setOperator(LESS_THAN);
		restriction.setRight(value);
		return restriction;
	}

	public RestrictionChainable gt(Number value) {
		return gt(toValue(value));
	}
	
	public RestrictionChainable gt(TypeSafeValue<Number> value) {
		restriction.setOperator(GREATER_THAN);
		restriction.setRight(value);
		return restriction;
	}
	
	public RestrictionChainable lte(Number value) {
		return lte(toValue(value));
	}
	
	public RestrictionChainable lte(TypeSafeValue<Number> value) {
		restriction.setOperator(LESS_THAN_EQUAL);
		restriction.setRight(value);
		return restriction;
	}

	public RestrictionChainable gte(Number value) {
		return gte(toValue(value));
	}
	
	public RestrictionChainable gte(TypeSafeValue<Number> value) {
		restriction.setOperator(GREATER_THAN_EQUAL);
		restriction.setRight(value);
		return restriction;
	}

	public RestrictionChainable not(Number value) {
		return not(toValue(value));
	}
	
	public RestrictionChainable not(TypeSafeValue<Number> value) {
		restriction.setOperator(RestrictionBase.NOT_EQUAL);
		restriction.setRight(value);
		return restriction;
	}

	public RestrictionChainable in(Number value) {
		return in(toValue(value));
	}
	
	public RestrictionChainable in(TypeSafeValue<Number> value) {
		restriction.setOperator(RestrictionBase.IN);
		restriction.setRight(value);
		return restriction;
	}

	public RestrictionChainable notIn(Number value) {
		return notIn(toValue(value));
	}
	
	public RestrictionChainable notIn(TypeSafeValue<Number> value) {
		restriction.setOperator(RestrictionBase.NOT_IN);
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
