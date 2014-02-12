package be.shad.tsqb.helper;

import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.ComponentType;
import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.CompositeType;
import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.EntityCollectionType;
import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.EntityType;
import static be.shad.tsqb.proxy.TypeSafeQueryProxyType.SelectionDtoType;

import java.lang.reflect.Method;
import java.util.Collection;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;

import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.BasicType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.type.Type;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQueryProxyFactory;
import be.shad.tsqb.proxy.TypeSafeQueryProxyType;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeRootQueryImpl;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;

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
                    ((SessionFactoryImplementor) sessionFactory));
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
    public <T> T createTypeSafeSelectProxy(final TypeSafeRootQueryInternal query, Class<T> clazz) {
        T proxy = proxyFactory.getProxy(clazz, SelectionDtoType);
        ((ProxyObject) proxy).setHandler(new MethodHandler() {
            public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
                if( m.getName().startsWith("set") ) {
                    String propertyName = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
                    query.getProjections().project(args[0], propertyName);
                }
                return null;
            }
        });
        query.getProjections().setResultClass(clazz);
        return proxy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T createTypeSafeFromProxy(TypeSafeQueryInternal query, Class<T> clazz) {
        T proxy = proxyFactory.getProxy(clazz, EntityType);
        TypeSafeQueryProxyData data = query.getDataTree().createData(null, 
                null, clazz, EntityType, null, (TypeSafeQueryProxy) proxy);
        setEntityProxyMethodListener(query, data);
        return proxy;
    }

    /**
     * Sets the method handler on the proxy to create new proxies when 
     * hibernate entities are traversed via the getter/setters.
     */
    private void setEntityProxyMethodListener(final TypeSafeQueryInternal query, final TypeSafeQueryProxyData data) {
        ((ProxyObject) data.getProxy()).setHandler(new MethodHandler() {
            public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
                if( m.getReturnType().equals(TypeSafeQueryProxyData.class) ) {
                    return data;
                }
                
                String method2Name = method2PropertyName(m);
                TypeSafeQueryProxyData child = data.getChild(method2Name);
                if( child == null ) {
                    child = createChildData(query, data, method2Name);
                }
                if ( !Collection.class.isAssignableFrom(m.getReturnType()) && child.getProxy() != null ) {
                    // return the proxy without adding to the invocation queue to allow method chaining.
                    return child.getProxy();
                }
                // remember the method invocation, to be used later...
                query.invocationWasMade(child);
                return proceed.invoke(self, args);
            }


        });
    }

    private TypeSafeQueryProxyData createChildData(TypeSafeQueryInternal query, TypeSafeQueryProxyData parent, String property) {
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
        setEntityProxyMethodListener(query, data);
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
    private String method2PropertyName(Method m) {
        String name = m.getName();
        int start;
        if (name.startsWith("get") ) {
            start = 3;
        } else if ( name.startsWith("is") ) {
            start = 2;
        } else {
            return name;
        }
        String ret = name.substring(start, ++start).toLowerCase();
        if (name.length() > start)
            ret += name.substring(start);
        return ret;
    }

    /**
     * Convert a value to a string. This is only used when hibernate would fail if params are used.
     * <p>
     * Uses the hibernate Type object to convert to a literal.
     */
    @SuppressWarnings("unchecked")
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
            return literal = "'" + literal + "'";
        } else {
            throw new IllegalArgumentException("Failed to convert: " + value);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public HqlQueryValue replaceParamsWithLiterals(HqlQueryValue value) {
        if( value.getParams().length > 0 ) {
            String hql = value.getHql();
            for(Object param: value.getParams()) {
                hql = hql.replaceFirst("\\?", toLiteral(param));
            }
            value = new HqlQueryValueImpl(hql);
        }
        return value;
    }
    
}
