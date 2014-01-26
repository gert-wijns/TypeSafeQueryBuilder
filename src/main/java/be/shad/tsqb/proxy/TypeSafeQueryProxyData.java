package be.shad.tsqb.proxy;

import be.shad.tsqb.query.JoinType;

public class TypeSafeQueryProxyData {

	private final TypeSafeQueryProxyData parent;
	private final TypeSafeQueryProxy proxy;
	private final String propertyPath;
	private final Class<?> propertyType;
	private final String alias;
	private JoinType joinType;
	
	public TypeSafeQueryProxyData(TypeSafeQueryProxyData parent, String propertyPath, Class<?> propertyType) {
		this(parent, propertyPath, propertyType, null, null);
	}
	
	public TypeSafeQueryProxyData(TypeSafeQueryProxyData parent, String propertyPath, 
			Class<?> propertyType, TypeSafeQueryProxy proxy, String alias) {
		this.propertyPath = propertyPath;
		this.propertyType = propertyType;
		this.parent = parent;
		this.proxy = proxy;
		this.alias = alias;
	}
	
	public String getAlias() {
		if( parent != null && (joinType == null || joinType == JoinType.None) ) {
			return parent.getAlias() + "." + propertyPath;
		}
		return alias;
	}
	
	public TypeSafeQueryProxy getProxy() {
		return proxy;
	}

	public String getPropertyPath() {
		return propertyPath;
	}

	public JoinType getJoinType() {
		return joinType;
	}
	
	public void setJoinType(JoinType joinType) {
		if( proxy == null && joinType != null ) {
			throw new IllegalStateException("Trying to join on a field "
					+ "value instead of an entity. " + toString());
		}
		this.joinType = joinType;
	}
	
	public Class<?> getPropertyType() {
		return propertyType;
	}

	@Override
	public String toString() {
		String s;
		if( parent != null ) {
			s = parent.toString() + "." + propertyPath;
		} else {
			s = propertyType.getSimpleName();
		}
		if( proxy == null ) {
			s += ":"+propertyType.getSimpleName();
		}
		return s;
	}
}
