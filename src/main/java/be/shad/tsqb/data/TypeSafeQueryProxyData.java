package be.shad.tsqb.data;

import java.util.Collection;
import java.util.LinkedHashMap;

import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.JoinType;

public class TypeSafeQueryProxyData {

    private final LinkedHashMap<String, TypeSafeQueryProxyData> children = new LinkedHashMap<>();
    private final TypeSafeQueryProxyData parent;
    private final TypeSafeQueryProxy proxy;
    private final String propertyPath;
    private final Class<?> propertyType;
    private final String alias;
    private JoinType joinType;
    
    /**
     * Package protected so that the data is correctly add to the data tree.
     */
    TypeSafeQueryProxyData(TypeSafeQueryProxyData parent, String propertyPath, Class<?> propertyType) {
        this(parent, propertyPath, propertyType, null, null);
    }
    
    /**
     * 
     * Package protected so that the data is correctly add to the data tree.
     */
    TypeSafeQueryProxyData(TypeSafeQueryProxyData parent, String propertyPath, 
            Class<?> propertyType, TypeSafeQueryProxy proxy, String alias) {
        this.propertyPath = propertyPath;
        this.propertyType = propertyType;
        this.parent = parent;
        this.proxy = proxy;
        this.alias = alias;
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
        if( parent != null && (joinType == null || joinType == JoinType.None) ) {
            return parent.getAlias() + "." + propertyPath;
        }
        return alias;
    }
    
    public TypeSafeQueryProxy getProxy() {
        return proxy;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public JoinType getJoinType() {
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
