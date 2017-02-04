/*
 * Copyright Gert Wijns gert.wijns@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.shad.tsqb.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import be.shad.tsqb.helper.ConcreteDtoClassResolver;

/**
 * Provides the proxies using javaassist.
 * <p>
 * the proxied classes are cached for faster proxy creation and
 * to prevent extra class creations everytime a proxy is requested.
 */
public final class TypeSafeQueryProxyFactory {

    private static final MethodFilter METHOD_FILTER = new MethodFilter() {
        public boolean isHandled(Method m) {
            switch (m.getName()) {
                case "finalize":
                case "hashCode":
                case "equals":
                    return false;
                default:
                    return true;
            }
        }
    };

    private final Map<Class<?>, Class<?>>[] proxyClasses;
    private final ConcreteDtoClassResolver classResolver;

    @SuppressWarnings("unchecked")
    public TypeSafeQueryProxyFactory(ConcreteDtoClassResolver classResolver) {
        this.classResolver = classResolver;
        proxyClasses = new HashMap[TypeSafeQueryProxyType.values().length];
        for (int i = 0, n = TypeSafeQueryProxyType.values().length; i < n; i++) {
            proxyClasses[i] = new HashMap<>();
        }
    }

    public <T> T getProxy(Class<T> fromClass, TypeSafeQueryProxyType type) {
        try {
            return (T) getProxyClass(fromClass, type).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> getProxyClass(Class<T> requestedClass, TypeSafeQueryProxyType type) {
        synchronized ( proxyClasses) {
            Class<?> proxyClass = proxyClasses[type.ordinal()].get(requestedClass);
            if (proxyClass == null) {
                ProxyFactory f = new ProxyFactory();
                Class<?> concreteClass = classResolver.getConcreteClass(requestedClass);
                f.setSuperclass(concreteClass); // what if the super class is final?? guess it will give an exception..
                if (type.isEntity() || type.isComposite()) {
                    f.setInterfaces(new Class[] { TypeSafeQueryProxy.class });
                } else {
                    f.setInterfaces(new Class[] { TypeSafeQuerySelectionProxy.class });
                }
                f.setFilter(METHOD_FILTER);
                proxyClass = f.createClass();
                proxyClasses[type.ordinal()].put(requestedClass, proxyClass);
                if (requestedClass != concreteClass) {
                    proxyClasses[type.ordinal()].put(concreteClass, proxyClass);
                }
            }
            return (Class<T>) proxyClass;
        }
    }

}
