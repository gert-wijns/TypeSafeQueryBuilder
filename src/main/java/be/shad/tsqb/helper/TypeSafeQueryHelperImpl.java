package be.shad.tsqb.helper;

import java.lang.reflect.Method;
import java.util.Collection;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;

import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQueryProxyFactory;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeRootQueryImpl;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;

public class TypeSafeQueryHelperImpl implements TypeSafeQueryHelper {
	private final SessionFactory sessionFactory;
	private final TypeSafeQueryProxyFactory proxyFactory;

	public TypeSafeQueryHelperImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.proxyFactory = new TypeSafeQueryProxyFactory();
	}
	
	private boolean isEntity(Class<?> clazz) {
		ClassMetadata meta = sessionFactory.getClassMetadata(clazz);
		return meta != null;
	}

	private Class<?> getTargetEntityClass(Class<?> fromClass, String property) {
		ClassMetadata meta = sessionFactory.getClassMetadata(fromClass);
		Type propertyType = meta.getPropertyType(property);
		if( CollectionType.class.isAssignableFrom(propertyType.getClass()) ) {
			CollectionType collectionType = (CollectionType) propertyType;
			Type elementType = collectionType.getElementType(
					((SessionFactoryImplementor) sessionFactory));
			return elementType.getReturnedClass();
		}
		return propertyType.getReturnedClass();
	}
	
	@Override
	public TypeSafeQueryProxyFactory getTypeSafeProxyFactory() {
		return proxyFactory;
	}

	@Override
	public String getEntityName(Class<?> entityClass) {
		return sessionFactory.getClassMetadata(entityClass).getEntityName();
	}

	@Override
	public TypeSafeRootQuery createQuery() {
		return new TypeSafeRootQueryImpl(this);
	}

	@Override
	public <T> T createTypeSafeSelectProxy(final TypeSafeRootQueryInternal query, Class<T> clazz) {
		T proxy = proxyFactory.getProxyInstance(clazz);
		((ProxyObject) proxy).setHandler(new MethodHandler() {
			public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
				if( m.getName().startsWith("set") ) {
					String propertyName = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
					query.getProjections().project(args[0], propertyName);
				}
				return null;
			}
		});
		query.getProjections().setResultClass(clazz);
		return proxy;
	}
	
	@Override
	public <T> T createTypeSafeFromProxy(TypeSafeQueryInternal query, Class<T> clazz) {
		T proxy = proxyFactory.getProxyInstance(clazz);
		TypeSafeQueryProxyData data = query.getDataTree().createData(
				null, null, clazz, (TypeSafeQueryProxy) proxy);
		setMethodListener(query, data);
		return proxy;
	}

	/**
	 * Sets the method handler on the proxy to create new proxies when 
	 * hibernate entities are traversed via the getter/setters.
	 */
	private void setMethodListener(final TypeSafeQueryInternal query, final TypeSafeQueryProxyData data) {
		((ProxyObject) data.getProxy()).setHandler(new MethodHandler() {
			public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
				if( m.getReturnType().equals(TypeSafeQueryProxyData.class) ) {
					return data;
				}
				
				String method2Name = method2PropertyName(m);
				TypeSafeQueryProxyData child = data.getChild(method2Name);
				if( child == null ) {
					Class<?> targetClass = getTargetEntityClass(data.getPropertyType(), method2Name);
					if( isEntity(targetClass) ) {
						TypeSafeQueryProxy proxy = (TypeSafeQueryProxy) proxyFactory.getProxyInstance(targetClass);
						child = query.getDataTree().createData(data, method2Name, targetClass, proxy);
						setMethodListener(query, child);
					} else {
						child = query.getDataTree().createData(data, method2Name, targetClass); 
					}
				}
				// remember the method invocation, to be used later...
				query.invocationWasMade(child);
				if( child.getProxy() != null && !Collection.class.isAssignableFrom(m.getReturnType())) {
					// return null to make sure no method chaining occurs. 
					// joining must be done explicitly by query.join, this will return a proxy)
					return proxyFactory.getMethodChainingExceptionProxy(child.getPropertyType()); 
				}
				return proceed.invoke(self, args);
			}
		});
	}

	/**
	 * Simple conversion to the property path to be used in the query building phase.
	 */
	private String method2PropertyName(Method m) {
		String name = m.getName();
		int start;
		if (name.startsWith("get") ) {
			start = 3;
		} else if ( name.startsWith("is") ) {
			start = 2;
		} else {
			return name;
		}
		String ret = name.substring(start, ++start).toLowerCase();
		if (name.length() > start)
			ret += name.substring(start);
		return ret;
	}
	
}
