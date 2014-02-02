package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionImpl.EQUAL;
import static be.shad.tsqb.restrictions.RestrictionImpl.IN;
import static be.shad.tsqb.restrictions.RestrictionImpl.IS_NOT_NULL;
import static be.shad.tsqb.restrictions.RestrictionImpl.IS_NULL;
import static be.shad.tsqb.restrictions.RestrictionImpl.NOT_EQUAL;
import static be.shad.tsqb.restrictions.RestrictionImpl.NOT_IN;

import java.util.Collection;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.values.CollectionTypeSafeValue;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Takes a partially built restriction and provides methods complete it.
 * Type specific methods are added in subclasses of this class.
 * 
 * @see RestrictionImpl
 */
public class OnGoingRestriction<VAL> {

	protected final RestrictionImpl restriction;

	public OnGoingRestriction(RestrictionImpl restriction, VAL argument) {
		this.restriction = restriction;
		restriction.setLeft(toValue(argument));
	}

	public OnGoingRestriction(RestrictionImpl restriction, TypeSafeValue<VAL> argument) {
		this.restriction = restriction;
		restriction.setLeft(argument);
	}

	/**
	 * Generates: left is null
	 */
	public Restriction isNull() {
		restriction.setOperator(IS_NULL);
		return restriction;
	}

	/**
	 * Generates: left is not null
	 */
	public Restriction isNotNull() {
		restriction.setOperator(IS_NOT_NULL);
		return restriction;
	}
	
	/**
	 * Generates: left in ( valuesRepresentative )
	 * <p>
	 * Can be used with a TypeSafeSubQuery to check if
	 * the left part is in the subquery results.
	 */
	public Restriction in(TypeSafeValue<VAL> value) {
		restriction.setOperator(IN);
		restriction.setRight(value);
		return restriction;
	}

	/**
	 * Generates: left not in ( actualValues )
	 */
	public Restriction in(Collection<VAL> values) {
		return in(new CollectionTypeSafeValue<>(values));
	}

	/**
	 * Generates: left not in ( valuesRepresentative )
	 * <p>
	 * Can be used with a TypeSafeSubQuery to check if
	 * the left part is not in the subquery results.
	 */
	public Restriction notIn(TypeSafeValue<VAL> value) {
		restriction.setOperator(NOT_IN);
		restriction.setRight(value);
		return restriction;
	}

	/**
	 * Generates: left not in ( actualValues )
	 */
	public Restriction notIn(Collection<VAL> values) {
		return notIn(new CollectionTypeSafeValue<>(values));
	}

	/**
	 * Generates: left = valueRepresentative
	 */
	public Restriction eq(TypeSafeValue<VAL> value) {
		restriction.setOperator(EQUAL);
		restriction.setRight(value);
		return restriction;
	}

	/**
	 * Generates: left = (referencedValue or actualValue)
	 */
	public Restriction eq(VAL value) {
		return eq(toValue(value));
	}

	/**
	 * Generates: left <> valueRepresentative
	 */
	public Restriction not(TypeSafeValue<VAL> value) {
		restriction.setOperator(NOT_EQUAL);
		restriction.setRight(value);
		return restriction;
	}

	/**
	 * Generates: left <> (referencedValue or actualValue)
	 */
	public Restriction not(VAL value) {
		return not(toValue(value));
	}

	/**
	 * Gets the queued invocations. 
	 * If there was exactly one, then this invocations data is used as referenced value.
	 * If there was no invocation, the value is used as a direct value.
	 */
	protected TypeSafeValue<VAL> toValue(VAL value) {
		List<TypeSafeQueryProxyData> invocations = restriction.getQuery().dequeueInvocations();
		if( invocations.isEmpty() ) {
			// direct selection
			return new DirectTypeSafeValue<VAL>(value);
		} else if( invocations.size() == 1 ) {
			// invoked with proxy
			return new ReferenceTypeSafeValue<VAL>(invocations.get(0));
		} else {
			// invalid call, only expected one invocation
			throw new IllegalStateException();
		}
	}
}
