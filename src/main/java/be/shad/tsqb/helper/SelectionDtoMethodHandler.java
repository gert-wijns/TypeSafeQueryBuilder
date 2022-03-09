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

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyPropertyData;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupImpl;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupInternal;
import javassist.util.proxy.MethodHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SelectionDtoMethodHandler<T> implements MethodHandler {
    private final TypeSafeQueryHelperImpl helper;
    private final TypeSafeRootQueryInternal query;
    private final TypeSafeQuerySelectionGroupInternal<T, ?> data;

    /**
     *
     */
    public Object invoke(Object self, Method m, Method proceed, Object[] args) {
        if (m.getReturnType().equals(TypeSafeQuerySelectionGroupInternal.class)) {
            return data;
        }
        String methodName = m.getName();
        if ("toString".equals(methodName)) {
            return String.format("Selection Proxy of [%s]", data.toString());
        }
        if (handleMapInvocation(self, methodName, args)) {
            return null;
        }
        if (args.length == 0 && data.getSelectionBuilderSpec().isBuilder()) {
            // builder method, return type may be final -> let the builder create a specific instance and
            // keep a mapping so we can find the builder based on the identity of the object
            @SuppressWarnings({"unchecked"})
            T identity = (T) data.getSelectionBuilderSpec().createNewResult();
            query.putSelectionProxyData(identity, data);
            return identity;
        }

        boolean setter = methodName.startsWith("set");
        String propertyName = helper.method2PropertyName(m);

        boolean returnSelf = m.getReturnType().isAssignableFrom(data.getProxy().getClass());
        if (returnSelf && m.getParameterTypes().length == 1) {
            setter = true;
            propertyName = m.getName();
        }

        TypeSafeQuerySelectionProxyPropertyData<Object> childData = getOrCreateChildData(propertyName, m, setter);
        if (setter) {
            query.handleSetSelectionValue(childData, args[0]);
            return returnSelf ? self: null;
        } else if (helper.isBasicType(m.getReturnType())) {
            query.queueInvokedSelection(childData);
            return helper.getDummyValue(m.getReturnType());
        } else if (Collection.class.isAssignableFrom(m.getReturnType())) {
            query.queueInvokedSelection(childData);
            return null;
        } else {
            return childData.getSubGroup().getProxy();
        }
    }

    @SuppressWarnings({"unchecked"})
    private <T> TypeSafeQuerySelectionProxyPropertyData<T> getOrCreateChildData(String propertyName, Method m, boolean setter) {
        TypeSafeQuerySelectionProxyPropertyData<T> childData = data.getChild(propertyName);
        if (childData != null) {
            return childData;
        }
        Class<T> propertyType = (Class<T>) (setter ? m.getParameterTypes()[0]: m.getReturnType());
        TypeSafeQuerySelectionProxyPropertyData<T> newChildData = query.getDataTree()
                .createSelectionData(propertyName, propertyType, data);
        if (!setter && newChildData.getSubGroup() == null
                && !java.util.Collection.class.isAssignableFrom(propertyType)
                && !helper.isBasicType(propertyType)) {
            Class<T> concreteClass = helper.getConcreteDtoClassResolver().getConcreteClass(propertyType);
            TypeSafeQuerySelectionGroupInternal<T, T> subGroup = new TypeSafeQuerySelectionGroupImpl<>(
                    query.createSelectGroupAlias(), helper.createSelectionBuilderSpec(concreteClass));
            helper.createTypeSafeSelectProxy(query, propertyType, subGroup);
            newChildData.setSubGroup(subGroup);
        }
        return newChildData;
    }

    private boolean handleMapInvocation(Object self, String methodName, Object[] args) {
        if (!(self instanceof Map)) {
            return false;
        }
        String mapSelectionKey = (String) args[0];
        if ("get".equals(methodName)) {
            TypeSafeQuerySelectionProxyPropertyData<?> childData = data.getChild(mapSelectionKey);
            if (childData == null) {
                throw new IllegalArgumentException("Attempting to get data from a map proxy "
                        + "which was not first put to the map proxy for key: "
                        + mapSelectionKey);
            }
            query.queueInvokedSelection(childData);
            return true;
        }
        if ("put".equals(methodName)) {
            query.clearInvokedSelection();
            query.getProjections().setMapSelectionKeyForNextProjection(mapSelectionKey);

            TypeSafeQuerySelectionProxyPropertyData<?> childData = data.getChild(mapSelectionKey);
            if (childData == null) {
                childData = query.getDataTree().createSelectionData(mapSelectionKey, Object.class, data);
            }
            query.getProjections().project(args[1], childData);
            return true;
        }
        return false;
    }
}
