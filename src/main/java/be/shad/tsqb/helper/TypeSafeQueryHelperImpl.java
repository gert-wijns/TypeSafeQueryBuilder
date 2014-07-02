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

import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.ComponentType;
import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.CompositeType;
import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.EntityCollectionType;
import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.EntityType;
import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.SelectionDtoType;

import java.lang.reflect.Method;

import javassist.util.proxy.ProxyObject;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.BasicType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.type.Type;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQuerySelectionProxyData;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQueryProxyFactory;
import be.shad.tsqb.proxy.TypeSafeQueryProxyType;
import be.shad.tsqb.proxy.TypeSafeQuerySelectionProxy;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeRootQueryImpl;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroup;

public class TypeSafeQueryHelperImpl implements TypeSafeQueryHelper {
    private final SessionFactory sessionFactory;
    private final TypeSafeQueryProxyFactory proxyFactory;

    public TypeSafeQueryHelperImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.proxyFactory = new TypeSafeQueryProxyFactory();
    }
    
    private Type getTargetType(TypeSafeQueryProxyData data, String property) {
        if ( data.getProxyType().isComposite() ) {
            return sessionFactory.getClassMetadata(data.getCompositeTypeEntityParent().getPropertyType()).
                    getPropertyType(data.getCompositePropertyPath() + "." + property);
        }
        return sessionFactory.getClassMetadata(data.getPropertyType()).getPropertyType(property);
    }

    /**
     * Retrieves the type information from hibernate.
     */
    private Class<?> getTargetEntityClass(Type propertyType) {
        if( CollectionType.class.isAssignableFrom(propertyType.getClass()) ) {
            CollectionType collectionType = (CollectionType) propertyType;
            Type elementType = collectionType.getElementType(
                    (SessionFactoryImplementor) sessionFactory);
            return elementType.getReturnedClass();
        }
        return propertyType.getReturnedClass();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityName(Class<?> entityClass) {
        return sessionFactory.getClassMetadata(entityClass).getEntityName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeRootQuery createQuery() {
        return new TypeSafeRootQueryImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T createTypeSafeSelectProxy(final TypeSafeRootQueryInternal query, Class<T> clazz, TypeSafeQuerySelectionGroup group) {
        final T proxy = proxyFactory.getProxy(clazz, SelectionDtoType);
        TypeSafeQuerySelectionProxyData data = query.getDataTree().createSelectionData(
                null, null, clazz, group, (TypeSafeQuerySelectionProxy) proxy);
        setSelectionDtoMethodHandler(query, data);
        query.getProjections().setResultClass(clazz);
        return proxy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQuerySelectionProxyData createTypeSafeSelectSubProxy(TypeSafeRootQueryInternal query, 
            TypeSafeQuerySelectionProxyData parent, String propertyName, 
            Class<?> targetClass, boolean setter) {
        TypeSafeQuerySelectionProxyData childData = query.getDataTree().createSelectionData(
                parent, propertyName, targetClass, parent.getGroup(), null);
        if (!setter) {
            // only need method handler when the data is being retrieved,
            // on set we will just return the proxy data.
            setSelectionDtoMethodHandler(query, childData);
        }
        return childData;
    }
    
    /**
     * Build nested property path when values are retrieved, link to projections when values are set.
     */
    void setSelectionDtoMethodHandler(final TypeSafeRootQueryInternal query, 
            final TypeSafeQuerySelectionProxyData data) {
        if (data.getProxy() == null) {
            TypeSafeQuerySelectionProxy childProxy = null;
            if (!isBasicType(data.getPropertyType())) {
                childProxy = (TypeSafeQuerySelectionProxy) proxyFactory.getProxy(
                        data.getPropertyType(), SelectionDtoType);
                data.setProxy(childProxy);
            } else {
                // No proxy is to be set if it is a basic type, these types
                // won't need to get extra method handling.
                return;
            }
        }
        ((ProxyObject) data.getProxy()).setHandler(new SelectionDtoMethodHandler(this, query, data));
    }

    boolean isBasicType(Class<?> returnType) {
        return sessionFactory.getTypeHelper().basic(returnType) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S, T extends S> T createTypeSafeSubtypeProxy(TypeSafeQueryInternal query, S proxy, Class<T> subtype) {
        if (!(proxy instanceof TypeSafeQueryProxy)) {
            throw new IllegalArgumentException(String.format("The provided proxy [%s] is not a TypeSafeQueryProxy.", proxy));
        }
        ClassMetadata subtypeMeta = sessionFactory.getClassMetadata(subtype);
        if (subtypeMeta == null) {
            throw new IllegalArgumentException(String.format("The subtype [%s] is not "
                    + "known in hibernate. Maybe you forgot to map it?.", subtype));
        }
        // we now know the subtype is a hibernate type and it should be a subclass of the proxy,
        // bind the same data object to the subtype:
        T subtypeProxy = proxyFactory.getProxy(subtype, EntityType);
        TypeSafeQueryProxyData data = ((TypeSafeQueryProxy) proxy).getTypeSafeProxyData();
        setEntityProxyMethodListener(query, (TypeSafeQueryProxy) subtypeProxy, data);
        return subtypeProxy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T createTypeSafeFromProxy(TypeSafeQueryInternal query, Class<T> clazz) {
        T proxy = proxyFactory.getProxy(clazz, EntityType);
        TypeSafeQueryProxyData data = query.getDataTree().createData(null, 
                null, clazz, EntityType, null, (TypeSafeQueryProxy) proxy);
        setEntityProxyMethodListener(query, (TypeSafeQueryProxy) proxy, data);
        return proxy;
    }

    /**
     * Sets the method handler on the proxy to create new proxies when 
     * hibernate entities are traversed via the getter/setters.
     */
    private void setEntityProxyMethodListener(final TypeSafeQueryInternal query, 
            final TypeSafeQueryProxy proxy, final TypeSafeQueryProxyData data) {
        ((ProxyObject) proxy).setHandler(new EntityProxyMethodHandler(this, query, data));
    }

    /**
     * Creates data based on the hibernate metadata for the given <code>property</code>.
     */
    TypeSafeQueryProxyData createChildData(TypeSafeQueryInternal query, TypeSafeQueryProxyData parent, String property) {
        Type propertyType = getTargetType(parent, property);
        Class<?> targetClass = getTargetEntityClass(propertyType);
        ClassMetadata metadata = sessionFactory.getClassMetadata(targetClass);
        if( metadata == null && !propertyType.isComponentType() ) {
            return query.getDataTree().createData(parent, property, targetClass); 
        }
        TypeSafeQueryProxyType proxyType = null;
        if( metadata != null ) {
            proxyType = propertyType.isCollectionType() ? EntityCollectionType: EntityType;
        } else {
            proxyType = propertyType instanceof ComponentType ? ComponentType: CompositeType;
        }
        TypeSafeQueryProxy proxy = (TypeSafeQueryProxy) proxyFactory.getProxy(targetClass, proxyType);
        TypeSafeQueryProxyData data = query.getDataTree().createData(parent, property, targetClass, 
                proxyType, metadata == null ? null: metadata.getIdentifierPropertyName(), proxy);
        setEntityProxyMethodListener(query, proxy, data);
        return data;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQueryProxyData createTypeSafeJoinProxy(TypeSafeQueryInternal query, 
            TypeSafeQueryProxyData parent, String propertyName, Class<?> targetClass) {
        return createChildData(query, parent, propertyName);
    }

    /**
     * Simple conversion to the property path to be used in the query building phase.
     */
    String method2PropertyName(Method m) {
        String name = m.getName();
        int start;
        if (name.startsWith("get") ) {
            start = 3;
        } else if ( name.startsWith("is") ) {
            start = 2;
        } else if( name.startsWith("set") ) {
            start = 3;
        } else {
            return name;
        }
        String ret = name.substring(start, ++start).toLowerCase();
        if (name.length() > start) {
            ret += name.substring(start);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public String toLiteral(Object value) {
        if( value == null ) {
            return "null";
        }
        BasicType basic = sessionFactory.getTypeHelper().basic(value.getClass());
        if( basic instanceof StringRepresentableType<?> ) {
            String literal = ((StringRepresentableType<Object>) basic).toString(value);
            if( value instanceof Number || value instanceof Boolean ) {
                return literal;
            }
            return "'" + literal + "'";
        } else {
            throw new IllegalArgumentException("Failed to convert: " + value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResolvedTypeName(Class<?> javaType) {
        return sessionFactory.getTypeHelper().basic(javaType).getName();
    }

    /**
     * return a random value, (but take primitives into account to prevent NPEs)
     */
    @SuppressWarnings("unchecked")
    public <T> T getDummyValue(Class<T> clazz) {
        Class<?> primitiveClass = getPrimitiveClass(clazz);
        if( primitiveClass != null ) {
            return (T) defaultPrimitiveValue(primitiveClass);
        }
        return null;
    }

    /**
     * @return the primitive class if the class could be a primitive.
     */
    private Class<?> getPrimitiveClass(Class<?> valueClass) {
        if (valueClass.isPrimitive()) {
            return valueClass;
        } else if (Boolean.class.equals(valueClass)) {
            return Boolean.TYPE;
        } else if (Integer.class.equals(valueClass)) {
            return Integer.TYPE;
        } else if (Long.class.equals(valueClass)) {
            return Long.TYPE;
        } else if (Double.class.equals(valueClass)) {
            return Double.TYPE;
        } else if (Byte.class.equals(valueClass)) {
            return Byte.TYPE;
        } else if (Short.class.equals(valueClass)) {
            return Short.TYPE;
        } else if (Float.class.equals(valueClass)) {
            return Float.TYPE;
        } else if (Character.class.equals(valueClass)) {
            return Character.TYPE;
        }
        return null;
    }

    /**
     * @return a default value for each primitive class.
     */
    private Object defaultPrimitiveValue(Class<?> primitiveClass) {
        if (primitiveClass == Boolean.TYPE) {
            return Boolean.FALSE;
        } else if (primitiveClass == Integer.TYPE) {
            return Integer.valueOf(0);
        } else if (primitiveClass == Long.TYPE) {
            return Long.valueOf(0);
        } else if (primitiveClass == Double.TYPE) {
            return Double.valueOf(0);
        } else if (primitiveClass == Byte.TYPE) {
            return Byte.valueOf((byte) 0);
        } else if (primitiveClass == Short.TYPE) {
            return Short.valueOf((short) 0);
        } else if (primitiveClass == Float.TYPE) {
            return Float.valueOf(0);
        } else if (primitiveClass == Character.TYPE) {
            return Character.valueOf('a');
        }
        throw new IllegalArgumentException("Didn't take a primtiive class into account: " + primitiveClass);
    }
}
