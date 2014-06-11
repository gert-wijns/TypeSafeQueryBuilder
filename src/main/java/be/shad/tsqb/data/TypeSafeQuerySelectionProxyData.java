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
package be.shad.tsqb.data;

import java.util.LinkedHashMap;

import be.shad.tsqb.proxy.TypeSafeQuerySelectionProxy;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroup;

public class TypeSafeQuerySelectionProxyData {

    private final LinkedHashMap<String, TypeSafeQuerySelectionProxyData> children = new LinkedHashMap<>();
    private final TypeSafeQuerySelectionProxyData parent;
    private final Class<?> propertyType;
    private final String propertyPath;
    private final TypeSafeQuerySelectionGroup group;
    private TypeSafeQuerySelectionProxy proxy;
    
    public TypeSafeQuerySelectionProxyData(TypeSafeQuerySelectionProxyData parent,
            String propertyPath, Class<?> propertyType, TypeSafeQuerySelectionGroup group,
            TypeSafeQuerySelectionProxy proxy) {
        this.parent = parent;
        this.propertyPath = propertyPath;
        this.propertyType = propertyType;
        this.group = group;
        this.proxy = proxy;
        if (parent != null) {
            parent.putChild(this);
        }
    }
    
    public TypeSafeQuerySelectionProxyData getChild(String propertyName) {
        return children.get(propertyName);
    }
    
    private void putChild(TypeSafeQuerySelectionProxyData child) {
        children.put(child.propertyPath, child);
    }
    
    public TypeSafeQuerySelectionProxyData getParent() {
        return parent;
    }
    
    public String getPropertyPath() {
        if (parent != null && parent.getParent() != null) {
            return parent.getPropertyPath() + "." + propertyPath;
        }
        return propertyPath;
    }
    
    public Class<?> getPropertyType() {
        return propertyType;
    }
    
    public TypeSafeQuerySelectionProxy getProxy() {
        return proxy;
    }

    public void setProxy(TypeSafeQuerySelectionProxy proxy) {
        this.proxy = proxy;
    }

    public String getAlias() {
        StringBuilder alias = new StringBuilder();
        if (!group.isResultGroup()) {
            alias.append(group.getAliasPrefix()).append("__");
        }
        return alias.append(getPropertyPath().replace(".", "_")).toString();
    }

    public TypeSafeQuerySelectionGroup getGroup() {
        return group;
    }

}
