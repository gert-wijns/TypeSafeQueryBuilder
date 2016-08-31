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
package be.shad.tsqb.helper;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import javassist.util.proxy.MethodHandler;
import be.shad.tsqb.data.TypeSafeQuerySelectionProxyData;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;

class SelectionDtoMethodHandler implements MethodHandler {
    private final TypeSafeRootQueryInternal query;
    private final TypeSafeQuerySelectionProxyData data;
    private final TypeSafeQueryHelperImpl helper;

    public SelectionDtoMethodHandler(TypeSafeQueryHelperImpl helper,
            TypeSafeRootQueryInternal query, TypeSafeQuerySelectionProxyData data) {
        this.query = query;
        this.helper = helper;
        this.data = data;
    }

    /**
     *
     */
    public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
        if (m.getReturnType().equals(TypeSafeQuerySelectionProxyData.class)) {
            return data;
        }
        String methodName = m.getName();
        if ("toString".equals(methodName)) {
            return String.format("Selection Proxy of [%s]", data.toString());
        }
        if (self instanceof Map) {
            String mapSelectionKey = (String) args[0];
            if ("get".equals(methodName)) {
                TypeSafeQuerySelectionProxyData childData = data.getChild(mapSelectionKey);
                if (childData == null) {
                    throw new IllegalArgumentException("Attempting to get data from a map proxy "
                            + "which was not first put to the map proxy for key: "
                            + mapSelectionKey);
                }
                query.queueInvokedSelection(childData);
                return null;
            }
            if ("put".equals(methodName)) {
                query.clearInvokedSelection();
                query.getProjections().setMapSelectionKeyForNextProjection(mapSelectionKey);

                TypeSafeQuerySelectionProxyData childData = data.getChild(mapSelectionKey);
                if (childData == null) {
                    childData = helper.createTypeSafeSelectSubProxy(query,
                            data, mapSelectionKey, Object.class, false);
                }
                query.getProjections().project(args[1], childData);
                return null;
            }
        }

        boolean setter = methodName.startsWith("set");
        String propertyName = helper.method2PropertyName(m);
        TypeSafeQuerySelectionProxyData childData = data.getChild(propertyName);
        if (childData == null) {
            Class<?> propertyType = m.getReturnType();
            if (setter) {
                propertyType = m.getParameterTypes()[0];
            }
            childData = helper.createTypeSafeSelectSubProxy(query,
                    data, propertyName, propertyType,
                    setter);
        }

        Object childDto = null;
        if (setter) {
            query.clearInvokedSelection();
            query.getProjections().project(args[0], childData);
        } else if (helper.isBasicType(m.getReturnType())) {
            query.queueInvokedSelection(childData);
            return helper.getDummyValue(m.getReturnType());
        } else if (Collection.class.isAssignableFrom(m.getReturnType())) {
            query.queueInvokedSelection(childData);
            return null;
        } else {
            helper.setSelectionDtoMethodHandler(query, childData);
            childDto = childData.getProxy();
        }

        return childDto;
    }
}
