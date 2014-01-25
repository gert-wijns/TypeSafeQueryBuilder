package be.shad.tsqb;

import be.shad.tsqb.proxy.TypeSafeQueryProxyFactory;

public interface TypeSafeQueryHelper {
	
	TypeSafeQuery createQuery();
	
	boolean isEntity(Class<?> clazz);

	Class<?> getTargetEntityClass(Class<?> fromClass, String property);

	TypeSafeQueryProxyFactory getTypeSafeProxyFactory();
	
	String getEntityName(Class<?> entityClass);
	
}
