package be.shad.tsqb.proxy;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import be.shad.tsqb.TypeSafeQuery;
import be.shad.tsqb.TypeSafeQueryHelper;

public final class TypeSafeQueryProxyFactory {
	private static final MethodFilter METHOD_FILTER = new MethodFilter() {
		public boolean isHandled(Method m) {
			switch( m.getName() ) {
				case "finalize":
				case "hashCode":
				case "equals":
					return false;
				default: 
					return true;
			}
		}
	};
	private final Map<Class<?>, Class<?>> proxyClasses = new HashMap<>();
	private final Map<Class<?>, ProxyObject> methodChainingExceptionProxies = new HashMap<>();
	private final TypeSafeQueryHelper helper;
	
	public TypeSafeQueryProxyFactory(TypeSafeQueryHelper helper) {
		this.helper = helper;
	}

	public <T> T getProxyInstance(Class<T> fromClass) {
		try {
			return (T) getProxyClass(fromClass).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException();
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> Class<T> getProxyClass(Class<T> fromClass) {
		synchronized ( TypeSafeQueryProxyFactory.class) { 
			Class<?> proxyClass = proxyClasses.get(fromClass);
			if( proxyClass == null ) {
				ProxyFactory f = new ProxyFactory();
				f.setSuperclass(fromClass); // what if the super class is final?? guess it will give an exception..
				f.setInterfaces(new Class[] { TypeSafeQueryProxy.class });
				f.setFilter(METHOD_FILTER);
				proxyClass = f.createClass();
				proxyClasses.put(fromClass, proxyClass);
			}
			return (Class<T>) proxyClass;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getMethodChainingExceptionProxy(Class<T> fromClass) {
		synchronized( methodChainingExceptionProxies ) {
			ProxyObject methodChainingExceptionProxy = methodChainingExceptionProxies.get(fromClass);
			// will not be null often ...
			if( methodChainingExceptionProxy == null ) {
				methodChainingExceptionProxy = (ProxyObject) getProxyInstance(fromClass);
				methodChainingExceptionProxy.setHandler(new MethodHandler() {
					public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
						throw new IllegalStateException("Method chaining is not allowed. Please use the "
								+ "TypeSafeQuery.from and TypeSafeQuery.join methods to get proxy instances "
								+ "which will allow calling methods on them.\n"
								+ "Example:\n"
								+ "    class A with field b of class B;\n"
								+ "    class B with field name of class String;\n"
								+ "To perform a query on A, obtain a query object and call from:\n"
								+ "    A a = query.from(A.class);\n"
								+ "A proxy of B of obtained by calling join on the query object:\n"
								+ "    B b = query.join(a.getB());\n"
								+ "If you tried getting B by calling B b = a.getB(), this object will have been returned instead.\n"
								+ "As soon as b.getName() is called on this object, this exception will be thrown.\n"
								+ "Calling b.getName() on the instance obtained by calling query.join(a.getB()) will work correctly.\n"
								+ "This is done because it is otherwise not possible to validate the correct method calls were made.");
					}
				});
				methodChainingExceptionProxies.put(fromClass, methodChainingExceptionProxy);
			}
			return (T) methodChainingExceptionProxy;
		}
	}
	
	/**
	 * Sets the method handler on the proxy to create new proxies when 
	 * hibernate entities are traversed via the getter/setters.
	 */
	public void setMethodListener(final TypeSafeQueryProxyData data, final TypeSafeQuery query) {
		((ProxyObject) data.getProxy()).setHandler(new MethodHandler() {
			public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
				if( m.getReturnType().equals(TypeSafeQueryProxyData.class) ) {
					return data;
				}
				
				String method2Name = method2PropertyName(m);
				TypeSafeQueryProxyData child = query.getData(data.getProxy(), method2Name);
				if( child == null ) {
					Class<?> targetClass = helper.getTargetEntityClass(data.getPropertyType(), method2Name);
					if( helper.isEntity(targetClass) ) {
						TypeSafeQueryProxy proxy = (TypeSafeQueryProxy) getProxyInstance(targetClass);
						child = new TypeSafeQueryProxyData(data, method2Name, targetClass, 
								proxy, query.createEntityAlias());
						setMethodListener(child, query);
					} else {
						child = new TypeSafeQueryProxyData(data, method2Name, targetClass);
					}
					query.add(data, child);
				}
				// remember the method invocation, to be used later...
				query.invocationWasMade(child);
				if( child.getProxy() != null && !Collection.class.isAssignableFrom(m.getReturnType())) {
					// return null to make sure no method chaining occurs. 
					// joining must be done explicitly by query.join, this will return a proxy)
					return getMethodChainingExceptionProxy(child.getPropertyType()); 
				}
				return proceed.invoke(self, args);
			}
		});
	}
	
	public void setSelectIntoMethodListener(Object proxy, final TypeSafeQuery query) {
		((ProxyObject) proxy).setHandler(new MethodHandler() {
			public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
				if( m.getName().startsWith("set") ) {
					String propertyName = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
					query.getProjections().project(args[0], propertyName);
				}
				return null;
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
