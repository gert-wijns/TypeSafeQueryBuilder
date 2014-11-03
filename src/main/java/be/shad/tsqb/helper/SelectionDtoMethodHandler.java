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
        if ("toString".equals(m.getName())) {
            return String.format("Selection Proxy of [%s]", data.toString());
        }

        boolean setter = m.getName().startsWith("set");
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
        if( setter ) {
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
