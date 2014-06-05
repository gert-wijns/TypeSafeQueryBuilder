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

import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.EntityPropertyType;
import static be.shad.tsqb.query.JoinType.Default;
import static be.shad.tsqb.query.JoinType.Inner;
import static be.shad.tsqb.query.JoinType.None;

import java.util.Collection;
import java.util.LinkedHashMap;

import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQueryProxyType;
import be.shad.tsqb.query.JoinType;

/**
 */
public class TypeSafeQueryProxyData {

    private final LinkedHashMap<String, TypeSafeQueryProxyData> children = new LinkedHashMap<>();
    private final TypeSafeQueryProxyType proxyType;
    private final TypeSafeQueryProxyData parent;
    private final TypeSafeQueryProxy proxy;
    private final TypeSafeQueryProxyData compositeTypeEntityParent;
    private final String compositeTypePropertyPath;
    private final String propertyPath;
    private final String identifierPath;
    private final Class<?> propertyType;
    private final String alias;
    private String customAlias;
    private JoinType joinType;
    
    /**
     * Package protected so that the data is correctly add to the data tree.
     */
    TypeSafeQueryProxyData(TypeSafeQueryProxyData parent, String propertyPath, Class<?> propertyType) {
        this(parent, propertyPath, propertyType, EntityPropertyType, null, null, null);
    }
    
    /**
     * 
     * Package protected so that the data is correctly add to the data tree.
     */
    TypeSafeQueryProxyData(TypeSafeQueryProxyData parent, String propertyPath, 
            Class<?> propertyType, TypeSafeQueryProxyType proxyType, TypeSafeQueryProxy proxy, 
            String identifierPath, String alias) {
        this.identifierPath = identifierPath;
        this.propertyPath = propertyPath;
        this.propertyType = propertyType;
        this.proxyType = proxyType;
        this.parent = parent;
        this.proxy = proxy;
        this.alias = alias;

        // set composite related data:
        if( proxyType.isComposite() ) { 
            if (parent.getProxyType().isComposite() ) {
                compositeTypeEntityParent = parent.compositeTypeEntityParent;
                compositeTypePropertyPath = parent.compositeTypePropertyPath + "." + propertyPath;
            } else {
                compositeTypeEntityParent = parent;
                compositeTypePropertyPath = propertyPath;
            }
        } else {
            compositeTypeEntityParent = null;
            compositeTypePropertyPath = null;
        }
    }
    
    public TypeSafeQueryProxyType getProxyType() {
        return proxyType;
    }
    
    public String getIdentifierPath() {
        return identifierPath;
    }
    
    public TypeSafeQueryProxyData getParent() {
        return parent;
    }
    
    public Collection<TypeSafeQueryProxyData> getChildren() {
        return children.values();
    }
    
    public TypeSafeQueryProxyData getChild(String name) {
        return children.get(name);
    }
    
    public void putChild(TypeSafeQueryProxyData child) {
        children.put(child.propertyPath, child);
    }
    
    public String getAlias() {
        if( parent != null && (joinType == null || getEffectiveJoinType() == None) ) {
            return parent.getAlias() + "." + propertyPath;
        }
        return customAlias == null ? alias: customAlias;
    }
    
    public void setCustomAlias(String customAlias) {
        if (this.customAlias != null && !this.customAlias.equals(customAlias)) {
            throw new IllegalArgumentException(String.format("A custom alias was already set. [%s, %s]", 
                    this.customAlias, customAlias));
        }
        this.customAlias = customAlias;
    }
    
    public TypeSafeQueryProxy getProxy() {
        return proxy;
    }
    
    public TypeSafeQueryProxyData getCompositeTypeEntityParent() {
        return compositeTypeEntityParent;
    }
    
    public String getCompositePropertyPath() {
        return compositeTypePropertyPath;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    /**
     * When the join type was not set by the user, the default join type is evaluated to an effective join type.
     * When only the identifier property of a joined entity is used, the effective type is None.
     * Otherwise the default join type is Inner.
     */
    public JoinType getEffectiveJoinType() {
        if( proxyType.isComposite() ) {
            return JoinType.None;
        }
        if (joinType == Default) {
            if (getParent() != null && getParent().getParent() != null) {
                // check the parent join type if the parent is not the root of the query (a FROM proxy)
                JoinType parentJoinType = getParent().getEffectiveJoinType();
                switch (parentJoinType) {
                    case LeftFetch: 
                    case Left: return JoinType.Left;
                    default:
                }
            }
            if (customAlias == null && !proxyType.isCollection() && children.size() == 1) {
                // might be worth checking if only an identity relation was used:
                TypeSafeQueryProxyData child = children.values().iterator().next();
                if (identifierPath.equals(child.getPropertyPath())) {
                    return None;
                }
            }
            return Inner;
        }
        return joinType;
    }
    
    public void setJoinType(JoinType joinType) {
        if( proxy == null && joinType != null ) {
            throw new IllegalStateException("Trying to join on a field "
                    + "value instead of an entity. " + toString());
        }
        this.joinType = joinType;
    }
    
    public Class<?> getPropertyType() {
        return propertyType;
    }

    @Override
    public String toString() {
        String s;
        if( parent != null ) {
            s = parent.toString() + "." + propertyPath;
        } else {
            s = propertyType.getSimpleName();
        }
        if( proxy == null ) {
            s += ":"+propertyType.getSimpleName();
        }
        return s;
    }
}
