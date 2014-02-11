package be.shad.tsqb.data;

import java.util.Collection;
import java.util.LinkedHashMap;

import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.JoinType;

/**
 */
public class TypeSafeQueryProxyData {

    private final LinkedHashMap<String, TypeSafeQueryProxyData> children = new LinkedHashMap<>();
    private final TypeSafeQueryProxyData parent;
    private final TypeSafeQueryProxy proxy;
    private final String propertyPath;
    private final String identifierPath;
    private final Class<?> propertyType;
    private final boolean collectionType;
    private final String alias;
    private JoinType joinType;
    
    /**
     * Package protected so that the data is correctly add to the data tree.
     */
    TypeSafeQueryProxyData(TypeSafeQueryProxyData parent, String propertyPath, Class<?> propertyType) {
        this(parent, propertyPath, propertyType, false, null, null, null);
    }
    
    /**
     * 
     * Package protected so that the data is correctly add to the data tree.
     */
    TypeSafeQueryProxyData(TypeSafeQueryProxyData parent, String propertyPath, 
            Class<?> propertyType, boolean collectionType, TypeSafeQueryProxy proxy, 
            String identifierPath, String alias) {
        this.identifierPath = identifierPath;
        this.collectionType = collectionType;
        this.propertyPath = propertyPath;
        this.propertyType = propertyType;
        this.parent = parent;
        this.proxy = proxy;
        this.alias = alias;
    }
    
    public boolean isCollectionType() {
        return collectionType;
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
        if( parent != null && (joinType == null || getEffectiveJoinType() == JoinType.None) ) {
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

    /**
     * When the join type was not set by the user, the default join type is evaluated to an effective join type.
     * When only the identifier property of a joined entity is used, the effective type is None.
     * Otherwise the default join type is Inner.
     */
    public JoinType getEffectiveJoinType() {
        if( joinType == JoinType.Default ) {
            if( !collectionType && children.size() == 1 ) {
                // might be worth checking if only an identity relation was used:
                TypeSafeQueryProxyData child = children.values().iterator().next();
                if( identifierPath.equals(child.getPropertyPath()) ) {
                    return JoinType.None;
                }
            }
            return JoinType.Inner;
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
