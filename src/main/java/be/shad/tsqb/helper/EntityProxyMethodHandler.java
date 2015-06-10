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
import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;

class EntityProxyMethodHandler implements MethodHandler {
    private final TypeSafeQueryInternal query;
    private final TypeSafeQueryHelperImpl helper;
    private final TypeSafeQueryProxyData data;

    public EntityProxyMethodHandler(
            TypeSafeQueryHelperImpl helper,
            TypeSafeQueryInternal query,
            TypeSafeQueryProxyData data) {
        this.query = query;
        this.helper = helper;
        this.data = data;
    }

    public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
        if (m.getReturnType().equals(TypeSafeQueryProxyData.class)) {
            return data;
        }
        if ("toString".equals(m.getName())) {
            return String.format("Proxy of [%s]", data.toString());
        }
        if (m.getName().startsWith("set")) {
            throw new IllegalArgumentException("Calling the setter of an entity proxy has no point. "
                    + "If this object was supposed to be used as selection proxy, "
                    + "then use the select(class) instead and set the values there. "
                    + "If this setter was called to add a restriction, then use the "
                    + "query.where(...) methods instead.");
        }

        String method2Name = helper.method2PropertyName(m);
        TypeSafeQueryProxyData child = data.getChild(method2Name);
        if (child == null) {
            child = helper.createChildData(query, data, method2Name);
        }
        if (query.getActiveMultiJoinType() != null) {
            if (!child.getProxyType().isEntity()) {
                throw new IllegalStateException("query.join(JoinType) was used but it seems "
                        + "to not have been followed up with one of the join methods, "
                        + "causing the activeMultiJoinType to have remained active.");
            }
            // join type override is active, update the child join type:
            child.setJoinType(query.getActiveMultiJoinType());
        }
        if (!Collection.class.isAssignableFrom(m.getReturnType()) && child.getProxy() != null) {
            // return the proxy without adding to the invocation queue to allow method chaining.
            return child.getProxy();
        }
        // remember the method invocation, to be used later...
        query.invocationWasMade(child);
        return helper.getDummyValue(m.getReturnType());
    }

}
