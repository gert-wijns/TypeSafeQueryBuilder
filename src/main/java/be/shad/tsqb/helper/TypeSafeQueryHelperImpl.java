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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Supplier;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.OneToManyPersister;
import org.hibernate.query.Query;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.type.BasicType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.MapType;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.type.Type;

import be.shad.tsqb.NamedParameter;
import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.exceptions.TsqbException;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQueryProxyFactory;
import be.shad.tsqb.proxy.TypeSafeQueryProxyType;
import be.shad.tsqb.proxy.TypeSafeQuerySelectionProxy;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupInternal;
import be.shad.tsqb.values.CollectionTypeSafeValue;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;
import be.shad.tsqb.values.TypeSafeValue;
import javassist.util.proxy.ProxyObject;

public class TypeSafeQueryHelperImpl implements TypeSafeQueryHelper {
    private static final Integer DEFAULT_INTEGER = 84;
    private static final Double DEFAULT_DOUBLE = DEFAULT_INTEGER.doubleValue();
    private static final Long DEFAULT_LONG = DEFAULT_INTEGER.longValue();
    private static final Byte DEFAULT_BYTE = DEFAULT_INTEGER.byteValue();
    private static final Short DEFAULT_SHORT = DEFAULT_INTEGER.shortValue();
    private static final Float DEFAULT_FLOAT = DEFAULT_INTEGER.floatValue();
    private static final Character DEFAULT_CHAR = 'g';

    private final Supplier<Session> sessionSup;
    private final MetamodelImplementor metaModel;
    private final TypeSafeQueryProxyFactory proxyFactory;
    private final ConcreteDtoClassResolver classResolver;

    public TypeSafeQueryHelperImpl(Supplier<Session> sessionSup, MetamodelImplementor metaModel) {
        this(sessionSup, metaModel, new ConcreteDtoClassResolverImpl());
    }

    public TypeSafeQueryHelperImpl(Supplier<Session> sessionSup, MetamodelImplementor metaModel, ConcreteDtoClassResolver classResolver) {
        this.sessionSup = sessionSup;
        this.metaModel = metaModel;
        this.classResolver = classResolver;
        this.proxyFactory = new TypeSafeQueryProxyFactory(classResolver);
    }

    @Override
    public ConcreteDtoClassResolver getConcreteDtoClassResolver() {
        return classResolver;
    }

    private Type getTargetType(TypeSafeQueryProxyData data, String property) {
        if (data.getProxyType().isComposite()) {
            return getMetaDataNonNull(data.getCompositeTypeEntityParent().getPropertyType()).
                    getPropertyType(data.getCompositePropertyPath() + "." + property);
        }
        ClassMetadata metaData = getMetaData(data.getPropertyType());
        if (metaData != null) {
            return metaData.getPropertyType(property);
        }
        org.hibernate.type.CompositeType hibernateType = metaModel
                .embeddable(data.getPropertyType()).getHibernateType();
        return hibernateType.getSubtypes()[hibernateType.getPropertyIndex(property)];
    }

    /**
     * Retrieves the type information from hibernate.
     */
    private Class<?> getTargetEntityClass(Type propertyType) {
        if (CollectionType.class.isAssignableFrom(propertyType.getClass())) {
            CollectionType collectionType = (CollectionType) propertyType;
            Type elementType = metaModel.collectionPersister(collectionType.getRole()).getElementType();
            return elementType.getReturnedClass();
        }
        return propertyType.getReturnedClass();
    }

    @Override
    public String getEntityName(Class<?> entityClass) {
        return getMetaDataNonNull(entityClass).getEntityName();
    }

    @Override
    public boolean isEntity(Class<?> entityClass) {
        return getMetaData(entityClass) != null;
    }

    @Override
    public Class<?> getEntityIdClass(Class<?> entityClass) {
        return getMetaDataNonNull(entityClass).getIdentifierType().getReturnedClass();
    }

    @Override
    public Object getIdentifier(Object entity) {
        String identifierPropertyName = getMetaDataNonNull(entity.getClass()).getIdentifierPropertyName();
        try {
            return entity.getClass().getMethod("get" +
                                Character.toUpperCase(identifierPropertyName.charAt(0))
                                + identifierPropertyName.substring(1)).invoke(entity);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            throw new TsqbException(e);
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public  <SB, SR> SB createTypeSafeSelectProxy(TypeSafeRootQueryInternal query,
                                                  Class<SB> clazz, TypeSafeQuerySelectionGroupInternal<SB, SR> group) {
        final SB proxy = proxyFactory.getProxy(clazz, SelectionDtoType);
        group.setProxy((TypeSafeQuerySelectionProxy<SB>) proxy);
        query.getRootQuery().putSelectionProxyData(proxy, group);
        setSelectionDtoMethodHandler(query, group);
        query.getProjections().setResultClass(clazz);
        return proxy;
    }

    /**
     * Build nested property path when values are retrieved, link to projections when values are set.
     */
    <T, R> void setSelectionDtoMethodHandler(final TypeSafeRootQueryInternal query,
            final TypeSafeQuerySelectionGroupInternal<T, R> data) {
        ((ProxyObject) data.getProxy()).setHandler(new SelectionDtoMethodHandler<>(this, query, data));
    }

    private BasicType getBasicType(Class<?> type) {
        return metaModel.getTypeConfiguration().getBasicTypeRegistry().getRegisteredType(type.getName());
    }

    boolean isBasicType(Class<?> returnType) {
        return getBasicType(returnType) != null;
    }

    @Override
    public <S, T extends S> T createTypeSafeSubtypeProxy(TypeSafeQueryInternal query, S proxy, Class<T> subtype) {
        if (!(proxy instanceof TypeSafeQueryProxy)) {
            throw new IllegalArgumentException(String.format("The provided proxy [%s] is not a TypeSafeQueryProxy.", proxy));
        }
        ClassMetadata subtypeMeta = getMetaData(subtype);
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
        ClassMetadata metadata = getMetaData(targetClass);
        if (metadata == null && !propertyType.isComponentType() && !(propertyType instanceof MapType)) {
            return query.getDataTree().createData(parent, property, targetClass);
        }
        TypeSafeQueryProxyType proxyType;
        if (metadata != null || propertyType instanceof MapType) {
            proxyType = propertyType.isCollectionType() ? EntityCollectionType: EntityType;
        } else {
            proxyType = propertyType instanceof ComponentType ? ComponentType: CompositeType;
        }
        TypeSafeQueryProxy proxy = isBasicType(targetClass) ? null: (TypeSafeQueryProxy) proxyFactory.getProxy(targetClass, proxyType);
        TypeSafeQueryProxyData data = query.getDataTree().createData(parent, property, targetClass,
                proxyType, metadata == null ? null: metadata.getIdentifierPropertyName(), proxy);
        if (proxy != null) {
            setEntityProxyMethodListener(query, proxy, data);
        }
        return data;
    }

    private ClassMetadata getMetaDataNonNull(Class<?> entityClass) {
        ClassMetadata metaData = getMetaData(entityClass);
        if (metaData == null) {
            throw new IllegalArgumentException(entityClass + " is not an entity!");
        }
        return metaData;
    }

    private ClassMetadata getMetaData(Class<?> targetClass) {
        try {
            return (ClassMetadata) metaModel.entityPersister(targetClass);
        } catch (MappingException ex) {
            return null;
        }
    }

    @Override
    public TypeSafeQueryProxyData createTypeSafeJoinProxy(TypeSafeQueryInternal query,
            TypeSafeQueryProxyData parent, String propertyName, Class<?> targetClass) {
        if (propertyName != null) {
            return createChildData(query, parent, propertyName);
        } else {
            return createClassJoinProxy(query, parent, targetClass);
        }
    }

    private TypeSafeQueryProxyData createClassJoinProxy(TypeSafeQueryInternal query,
            TypeSafeQueryProxyData parent, Class<?> targetClass) {
        ClassMetadata metadata = getMetaDataNonNull(targetClass);
        TypeSafeQueryProxyType proxyType = TypeSafeQueryProxyType.EntityType;
        TypeSafeQueryProxy proxy = (TypeSafeQueryProxy) proxyFactory.getProxy(targetClass, proxyType);
        TypeSafeQueryProxyData data = query.getDataTree().createData(parent, null, targetClass,
                proxyType, metadata.getIdentifierPropertyName(), proxy);
        setEntityProxyMethodListener(query, proxy, data);
        return data;
    }

    /**
     * Simple conversion to the property path to be used in the query building phase.
     */
    String method2PropertyName(Method m) {
        String name = m.getName();
        int start;
        if (name.startsWith("get")) {
            start = 3;
        } else if (name.startsWith("is")) {
            start = 2;
        } else if (name.startsWith("set")) {
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

    @Override
    public String getMappedByProperty(TypeSafeQueryProxyData child) {
        Type propertyType = getTargetType(child.getParent(), child.getPropertyPath());
        if (!propertyType.isCollectionType()) {
            throw new IllegalArgumentException("Method not designed to fetch MappedByProperty "
                    + "for a non-collection type. PropertyType was: " + propertyType);
        }

        CollectionMetadata collectionMetadata = (CollectionMetadata) metaModel.collectionPersister(
                ((CollectionType) propertyType).getRole());
        if (collectionMetadata instanceof OneToManyPersister) {
            return ((OneToManyPersister) collectionMetadata).getMappedByProperty();
        }
        // what about many to many?
        return null;
    }

    @Override
    public <VAL> TypeSafeValue<VAL> createCollectionTypeSafeValue(TypeSafeQueryInternal query,
              Class<VAL> supportedValueClass, Collection<VAL> values, Integer batchSize) {
        return new CollectionTypeSafeValue<>(query, supportedValueClass, values, batchSize);
    }

    @Override
    public void bindNamedParameter(Query<Object[]> query, NamedParameter param) {
        if (param.getValue() instanceof Collection) {
            query.setParameterList(param.getName(), (Collection<?>) param.getValue());
        } else {
            query.setParameter(param.getName(), param.getValue());
        }
    }

    @Override
    public <P, R> SelectionBuilderSpec<P, R> createSelectionBuilderSpec(Class<P> selectionBuilderClass) {
        return getConcreteDtoClassResolver().createBuilderSpec(selectionBuilderClass);
    }

    @Override
    public String toFormattedSqlQuery(TypeSafeRootQuery query) {
        SessionFactoryImplementor factory = metaModel.getSessionFactory();
        HqlQuery hqlQuery = query.toHqlQuery(HqlQueryBuilderParamsImpl.builder()
                .requiresLiterals(true)
                .build());
        SessionImplementor session = (SessionImplementor) sessionSup.get().getSession();
        LoadQueryInfluencers loadQueryInfluencers = session.getLoadQueryInfluencers();
        String sql = new HQLQueryPlan(hqlQuery.getHql(), false,
                loadQueryInfluencers.getEnabledFilters(), factory)
                .getSqlStrings()[0];
        StatementInspector inspector = factory.getSessionFactoryOptions().getStatementInspector();
        return inspector == null ? sql: inspector.inspect(sql);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toLiteral(Object value) {
        if (value == null) {
            return "null";
        }
        BasicType basic = getBasicType(value.getClass());
        if (basic instanceof StringRepresentableType<?>) {
            String literal = ((StringRepresentableType<Object>) basic).toString(value);
            if (value instanceof Number || value instanceof Boolean) {
                return literal;
            }
            return "'" + literal + "'";
        } else {
            throw new IllegalArgumentException("Failed to convert: " + value);
        }
    }

    @Override
    public String getResolvedTypeName(Class<?> javaType) {
        return getBasicType(javaType).getName();
    }

    /**
     * return a random value, (but take primitives into account to prevent NPEs)
     */
    @SuppressWarnings("unchecked")
    public <T> T getDummyValue(Class<T> clazz) {
        Class<?> primitiveClass = getPrimitiveClass(clazz);
        if (primitiveClass != null) {
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
            return DEFAULT_INTEGER;
        } else if (primitiveClass == Long.TYPE) {
            return DEFAULT_LONG;
        } else if (primitiveClass == Double.TYPE) {
            return DEFAULT_DOUBLE;
        } else if (primitiveClass == Byte.TYPE) {
            return DEFAULT_BYTE;
        } else if (primitiveClass == Short.TYPE) {
            return DEFAULT_SHORT;
        } else if (primitiveClass == Float.TYPE) {
            return DEFAULT_FLOAT;
        } else if (primitiveClass == Character.TYPE) {
            return DEFAULT_CHAR;
        }
        throw new IllegalArgumentException("Didn't take a primtiive class into account: " + primitiveClass);
    }
}
