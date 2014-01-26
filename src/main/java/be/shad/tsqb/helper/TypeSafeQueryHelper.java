package be.shad.tsqb.helper;

import be.shad.tsqb.proxy.TypeSafeQueryProxyFactory;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;

public interface TypeSafeQueryHelper {
	
	TypeSafeRootQuery createQuery();
	
	TypeSafeQueryProxyFactory getTypeSafeProxyFactory();
	
	String getEntityName(Class<?> entityClass);
	
	<T> T createTypeSafeFromProxy(TypeSafeQueryInternal query, Class<T> clazz);
	
	<T> T createTypeSafeSelectProxy(TypeSafeRootQueryInternal query, Class<T> clazz);
	
}
