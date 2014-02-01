package be.shad.tsqb.query;

import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeSubQueryImpl<T extends Object> extends AbstractTypeSafeQuery implements TypeSafeSubQuery<T> {
	private TypeSafeQueryInternal parentQuery;
	private final Class<T> valueClass;

	public TypeSafeSubQueryImpl(Class<T> valueClass, 
			TypeSafeQueryHelper helper,
			TypeSafeQueryInternal parentQuery) {
		super(helper);
		this.valueClass = valueClass;
		this.parentQuery = parentQuery;
		setRootQuery(parentQuery.getRootQuery());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void select(T value) {
		getProjections().project(value, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void select(TypeSafeValue<T> value) {
		getProjections().project(value, null);
	}
	
	/**
	 * Create an hql query as value for this subquery.
	 */
	@Override
	public HqlQueryValue toHqlQueryValue() {
		HqlQuery query = toHqlQuery();
		return new HqlQueryValueImpl("(" +query.getHql() + ")", query.getParams());
	}

	/**
	 * In scope if it is in this query's scope or in its parents' scope.
	 */
	@Override
	public boolean isInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join) {
		if( super.isInScope(data, join) ) {
			return true;
		}
		return parentQuery.isInScope(data, join);
	}
	
	/**
	 * Delegate to root.
	 */
	@Override
	public List<TypeSafeQueryProxyData> dequeueInvocations() {
		return getRootQuery().dequeueInvocations();
	}

	/**
	 * Delegate to root.
	 */
	@Override
	public TypeSafeQueryProxyData dequeueInvocation() {
		return getRootQuery().dequeueInvocation();
	}

	/**
	 * Delegate to root.
	 */
	@Override
	public void invocationWasMade(TypeSafeQueryProxyData data) {
		getRootQuery().invocationWasMade(data);
	}

	/**
	 * Delegate to root.
	 */
	@Override
	public String createEntityAlias() {
		return getRootQuery().createEntityAlias();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Return null or a default primitive value.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T getValue() {
		// set on rootquery, so it can take this into account
		// when the setter of the selectDto is called.
		getRootQuery().queueSubqueryValueRetrieved(this);
		
		// return a random value, (but take primitives into account to prevent NPEs)
		Class<?> primitiveClass = getPrimitiveClass(this.valueClass);
		if( primitiveClass != null ) {
			return (T) defaultPrimitiveValue(primitiveClass);
		}
		return null;
	}
	
	/**
	 * @return the primitive class if the class could be a primitive.
	 */
	private Class<?> getPrimitiveClass(Class<T> valueClass) {
		if( Boolean.class.equals(valueClass) ) {
			return Boolean.TYPE;
		} else if ( Integer.class.equals(valueClass) ) {
			return Integer.TYPE;
		} else if ( Long.class.equals(valueClass) ) {
			return Long.TYPE;
		} else if ( Double.class.equals(valueClass) ) {
			return Double.TYPE;
		} else if ( Byte.class.equals(valueClass) ) {
			return Byte.TYPE;
		} else if ( Short.class.equals(valueClass) ) {
			return Short.TYPE;
		} else if ( Float.class.equals(valueClass) ) {
			return Float.TYPE;
		} else if ( Character.class.equals(valueClass) ) {
			return Character.TYPE;
		}
		return null;
	}

	/**
	 * @return a default value for each primitive class.
	 */
	private Object defaultPrimitiveValue(Class<?> primitiveClass) {
		if( primitiveClass == Boolean.TYPE ) {
			return Boolean.FALSE;
		} else if( primitiveClass == Integer.TYPE ) {
			return Integer.valueOf(0);
		} else if ( primitiveClass == Long.TYPE ) {
			return Long.valueOf(0);
		} else if ( primitiveClass == Double.TYPE ) {
			return Double.valueOf(0);
		} else if( primitiveClass == Byte.TYPE) {
			return Byte.valueOf((byte) 0);
		} else if( primitiveClass == Short.TYPE ) {
			return Short.valueOf((short) 0);
		} else if ( primitiveClass == Float.TYPE ) {
			return Float.valueOf(0);
		} else if ( primitiveClass == Character.TYPE ) {
			return Character.valueOf('a');
		}
		throw new IllegalArgumentException("Didn't take a primtiive class into account: " + primitiveClass);
	}

}
