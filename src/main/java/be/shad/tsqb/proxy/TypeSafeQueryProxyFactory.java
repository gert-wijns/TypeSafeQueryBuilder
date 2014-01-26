package be.shad.tsqb.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
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
	public <T> T getMethodChainingExceptionProxy(Class<T> fromClass) {
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
	
}
