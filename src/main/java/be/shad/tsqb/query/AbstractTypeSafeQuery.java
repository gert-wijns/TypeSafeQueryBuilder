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
package be.shad.tsqb.query;

import static be.shad.tsqb.query.JoinType.Default;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.proxy.HibernateProxy;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQueryProxyDataTree;
import be.shad.tsqb.exceptions.JoinException;
import be.shad.tsqb.exceptions.ValueNotInScopeException;
import be.shad.tsqb.grouping.TypeSafeQueryGroupBys;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.joins.JoinParams;
import be.shad.tsqb.joins.MapJoin;
import be.shad.tsqb.joins.MapJoinImpl;
import be.shad.tsqb.joins.MapJoinInternal;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.ordering.TypeSafeQueryOrderBys;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.restrictions.DirectValueProvider;
import be.shad.tsqb.restrictions.OnGoingBooleanRestriction;
import be.shad.tsqb.restrictions.OnGoingDateRestriction;
import be.shad.tsqb.restrictions.OnGoingEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingLocalDateRestriction;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingObjectRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.Restriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.restrictions.RestrictionHolder;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.restrictions.RestrictionsGroup.RestrictionsGroupBracketsPolicy;
import be.shad.tsqb.restrictions.RestrictionsGroupFactory;
import be.shad.tsqb.restrictions.RestrictionsGroupFactoryImpl;
import be.shad.tsqb.restrictions.RestrictionsGroupImpl;
import be.shad.tsqb.restrictions.RestrictionsGroupInternal;
import be.shad.tsqb.restrictions.WhereRestrictions;
import be.shad.tsqb.selection.TypeSafeQueryProjections;
import be.shad.tsqb.values.CaseTypeSafeValue;
import be.shad.tsqb.values.CustomTypeSafeValue;
import be.shad.tsqb.values.DirectTypeSafeStringValue;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;
import be.shad.tsqb.values.TypeSafeValueFunctions;
import be.shad.tsqb.values.arithmetic.ArithmeticTypeSafeValueFactory;
import be.shad.tsqb.values.arithmetic.ArithmeticTypeSafeValueFactoryImpl;

/**
 * Collects the data and creates the hqlQuery based on this data.
 */
public abstract class AbstractTypeSafeQuery implements TypeSafeQuery, TypeSafeQueryInternal {
    protected final TypeSafeQueryHelper helper;
    private TypeSafeRootQueryInternal rootQuery;

    private final RestrictionsGroupFactory groupedRestrictionsBuilder;
    private final ArithmeticTypeSafeValueFactory arithmeticsBuilder;
    private final TypeSafeQueryProxyDataTree dataTree;
    private final TypeSafeQueryProjections projections;
    private final RestrictionsGroupInternal whereRestrictions;
    private final RestrictionsGroupInternal havingRestrictions;
    private final TypeSafeQueryGroupBys groupBys;
    private final TypeSafeQueryOrderBys orderBys;
    private JoinType activeMultiJoinType;

    /**
     * Copy constructor
     */
    protected AbstractTypeSafeQuery(CopyContext context, AbstractTypeSafeQuery original) {
        initializeDefaults();
        context.put(original, this);
        this.helper = original.helper;
        this.groupedRestrictionsBuilder = new RestrictionsGroupFactoryImpl(this);
        this.arithmeticsBuilder = new ArithmeticTypeSafeValueFactoryImpl(this);
        this.rootQuery = context.get(original.rootQuery);
        this.dataTree = new TypeSafeQueryProxyDataTree(helper, this);
        this.projections = new TypeSafeQueryProjections(this);
        this.dataTree.replay(context, original.dataTree);
        this.projections.replay(context, original.projections);
        this.whereRestrictions = context.get(original.whereRestrictions);
        this.havingRestrictions = context.get(original.havingRestrictions);
        this.groupBys = context.get(original.groupBys);
        this.orderBys = context.get(original.orderBys);
        this.activeMultiJoinType = original.activeMultiJoinType;
    }

    /**
     * Provide the opportunity to initialize the empty lists/initial counters,
     * required because super values are initialized before subclass values.
     */
    protected abstract void initializeDefaults();

    protected AbstractTypeSafeQuery(TypeSafeQueryHelper helper) {
        initializeDefaults();
        this.helper = helper;
        this.dataTree = new TypeSafeQueryProxyDataTree(helper, this);
        this.groupedRestrictionsBuilder = new RestrictionsGroupFactoryImpl(this);
        this.arithmeticsBuilder = new ArithmeticTypeSafeValueFactoryImpl(this);
        this.projections = new TypeSafeQueryProjections(this);
        this.whereRestrictions = new RestrictionsGroupImpl(
                this, null, RestrictionsGroupBracketsPolicy.Never);
        this.havingRestrictions = new RestrictionsGroupImpl(
                this, null, RestrictionsGroupBracketsPolicy.Never);
        this.groupBys = new TypeSafeQueryGroupBys();
        this.orderBys = new TypeSafeQueryOrderBys(this);
    }

    @Override
    public TypeSafeQueryHelper getHelper() {
        return helper;
    }

    @Override
    public TypeSafeQueryProxyDataTree getDataTree() {
        return dataTree;
    }

    @Override
    public TypeSafeRootQueryInternal getRootQuery() {
        return rootQuery;
    }

    /**
     * Allow subclasses to set the rootQuery.
     */
    protected void setRootQuery(TypeSafeRootQueryInternal rootQuery) {
        this.rootQuery = rootQuery;
    }

    @Override
    public <T> T from(Class<T> fromClass) {
        return from(fromClass, null);
    }

    @Override
    public <T> T from(Class<T> fromClass, String name) {
        T from = helper.createTypeSafeFromProxy(this, fromClass);
        if (name != null) {
            named().name(from, name);
        }
        return from;
    }

    @Override
    public <S, T extends S> T getAsSubtype(S proxy, Class<T> subtype) {
        return helper.createTypeSafeSubtypeProxy(this, proxy, subtype);
    }

    @Override
    public <T> T join(Object parent, Class<T> entityClazz) {
        return join(parent, entityClazz, ClassJoinType.Inner, null);
    }

    @Override
    public <T> T join(Object parent, Class<T> entityClazz, ClassJoinType classJoinType) {
        return join(parent, entityClazz, classJoinType, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T join(Object parent, Class<T> entityClazz, ClassJoinType classJoinType, String name) {
        if (!(parent instanceof TypeSafeQueryProxy)) {
            throw new JoinException(String.format("Attempting to use a parent "
                    + "which does not represent an entity. Parent is [%s].", parent));
        }
        TypeSafeQueryProxyData parentData = ((TypeSafeQueryProxy) parent).getTypeSafeProxyData();
        if (!parentData.getProxyType().isEntity()) {
            throw new JoinException(String.format("Attempting to use a parent "
                    + "which does not represent an entity. Parent alias is [%s].", parentData.getAlias()));
        }
        TypeSafeQueryProxyData data = helper.createTypeSafeJoinProxy(this, parentData, null, entityClazz);
        // use joinType behind the scenes (also used for automatic left join resolution later)
        JoinType joinType = null;
        switch (classJoinType) {
            case Inner: joinType = JoinType.Inner; break;
            case Left:  joinType = JoinType.Left; break;
            case Right: joinType = JoinType.Right; break;
        }
        data.setJoinType(joinType);
        if (name != null) {
            named().name(data.getProxy(), name);
        }
        return (T) data.getProxy();
    }

    @Override
    public TypeSafeQueryJoin join(JoinType joinType) {
        if (activeMultiJoinType != null) {
            throw new IllegalStateException("query.join(JoinType) was used previous but it seems "
                    + "to not have been followed up with one of the join methods, "
                    + "causing the activeMultiJoinType to have remained active.");
        }
        activeMultiJoinType = joinType;
        return new TypeSafeQueryMultiJoin(this, joinType);
    }

    @Override
    public <T> T join(Collection<T> anyCollection) {
        return join(anyCollection, Default);
    }

    @Override
    public <T> T join(Collection<T> anyCollection, JoinParams params) {
        return handleJoin(null, params).getProxyAs();
    }

    @Override
    public <T> T join(T anyObject) {
        return join(anyObject, Default);
    }

    @Override
    public <T> T join(T anyObject, JoinParams params) {
        return handleJoin(anyObject, params).getProxyAs();
    }

    @Override
    public <K, V> MapJoin<K, V> join(Map<K, V> anyObject) {
        return join(anyObject, Default);
    }
    
    @Override
    public <K, V> MapJoin<K, V> join(Map<K, V> anyObject, JoinParams params) {
        return new MapJoinImpl<>(this, handleJoin(null, params));
    }

    @Override
    public <T> WhereRestrictions joinWith(T obj) {
        TypeSafeQueryProxyData data;
        if (obj instanceof MapJoinInternal) {
            data = ((MapJoinInternal<?, ?>) obj).getTypeSafeQueryProxyData();
        } else if (obj instanceof TypeSafeQueryProxy) {
            data = ((TypeSafeQueryProxy) obj).getTypeSafeProxyData();
        } else {
            throw new IllegalArgumentException("Can only get the join using a TypeSafeQueryProxy instance.");
        }
        if (data.getParent() == null) {
            throw new IllegalArgumentException("Attempting to get join restrictions for a 'from' proxy [" + obj + "].");
        }
        if (data.getJoinType() == null) {
            throw new IllegalArgumentException("Attempting to get join restrictions for data which isn't a join [" + obj + "].");
        }
        return dataTree.getJoinRestrictions(data);
    }

    private <T> TypeSafeQueryProxyData handleJoin(T obj, JoinParams params) {
        if (activeMultiJoinType != null) {
            throw new IllegalStateException("query.join(JoinType) was used but it seems "
                    + "to not have been followed up with one of the join methods, "
                    + "causing the activeMultiJoinType to have remained active.");
        }

        TypeSafeQueryProxyData data = rootQuery.dequeueInvocation();
        if (obj instanceof TypeSafeQueryProxy) {
            data = ((TypeSafeQueryProxy) obj).getTypeSafeProxyData();
        }
        if (!data.getProxyType().isEntity()) {
            throw new JoinException(String.format("Attempting to join an object [%s]"
                    + "which does not represent an entity. ", data.getAlias()));
        }
        if (params.isCreateAdditionalJoin()) {
            data = helper.createTypeSafeJoinProxy(this, data.getParent(),
                    data.getPropertyPath(), data.getPropertyType());
        }
        data.setJoinType(params.getJoinType());
        if (params.getName() != null) {
            named().name(data.getProxy(), params.getName());
        }
        return data;
    }

    @Override
    public JoinType getActiveMultiJoinType() {
        return activeMultiJoinType;
    }

    @Override
    public void resetActiveMultiJoinType() {
        this.activeMultiJoinType = null;
    }

    @Override
    public RestrictionChainable where() {
        return whereRestrictions;
    }

    @Override
    public RestrictionChainable and(RestrictionHolder restriction, RestrictionHolder... restrictions) {
        return this.whereRestrictions.and(restriction, restrictions);
    }

    @Override
    public RestrictionChainable or(RestrictionHolder restriction, RestrictionHolder... restrictions) {
        return this.whereRestrictions.or(restriction, restrictions);
    }

    @Override
    public RestrictionChainable where(HqlQueryValue restriction) {
        return whereRestrictions.and(restriction);
    }

    @Override
    public RestrictionChainable where(RestrictionsGroup group) {
        return whereRestrictions.and(group.getRestrictions());
    }

    @Override
    public RestrictionChainable where(Restriction restriction) {
        return whereRestrictions.and(restriction);
    }

    @Override
    public ArithmeticTypeSafeValueFactory getArithmeticsBuilder() {
        return arithmeticsBuilder;
    }

    @Override
    public RestrictionsGroupFactory getGroupedRestrictionsBuilder() {
        return groupedRestrictionsBuilder;
    }

    @Override
    public RestrictionChainable whereExists(TypeSafeSubQuery<?> subquery) {
        return whereRestrictions.andExists(subquery);
    }

    @Override
    public RestrictionChainable whereNotExists(TypeSafeSubQuery<?> subquery) {
        return whereRestrictions.andNotExists(subquery);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> where(E value) {
        return whereRestrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public <VAL> OnGoingObjectRestriction<VAL> where(TypeSafeValue<VAL> value) {
        return whereRestrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> whereEnum(TypeSafeValue<E> value) {
        return whereRestrictions.andEnum(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingBooleanRestriction where(Boolean value) {
        return whereRestrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingBooleanRestriction whereBoolean(TypeSafeValue<Boolean> value) {
        return whereRestrictions.andBoolean(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingNumberRestriction where(Number value) {
        return whereRestrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingTextRestriction where(String value) {
        return whereRestrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public <N extends Number> OnGoingNumberRestriction whereNumber(TypeSafeValue<N> value) {
        return whereRestrictions.andNumber(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingTextRestriction whereString(TypeSafeValue<String> value) {
        return whereRestrictions.andString(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingDateRestriction where(Date value) {
        return whereRestrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingDateRestriction whereDate(TypeSafeValue<Date> value) {
        return whereRestrictions.andDate(value);
    }

    @Override
    public OnGoingLocalDateRestriction where(LocalDate value) {
        return whereRestrictions.and(value);
    }

    @Override
    public OnGoingLocalDateRestriction whereLocalDate(TypeSafeValue<LocalDate> value) {
        return whereRestrictions.andLocalDate(value);
    }

    @Override
    public RestrictionChainable having() {
        return havingRestrictions;
    }

    @Override
    public RestrictionChainable having(HqlQueryValue restriction) {
        return havingRestrictions.and(restriction);
    }

    @Override
    public RestrictionChainable having(RestrictionsGroup group) {
        return havingRestrictions.and(group);
    }

    @Override
    public RestrictionChainable having(Restriction restriction) {
        return havingRestrictions.and(restriction);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> havingEnum(TypeSafeValue<E> value) {
        return havingRestrictions.andEnum(value);
    }

    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> having(E value) {
        return havingRestrictions.and(value);
    }

    @Override
    public OnGoingBooleanRestriction havingBoolean(TypeSafeValue<Boolean> value) {
        return havingRestrictions.andBoolean(value);
    }

    @Override
    public OnGoingBooleanRestriction having(Boolean value) {
        return havingRestrictions.and(value);
    }

    @Override
    public <N extends Number> OnGoingNumberRestriction havingNumber(TypeSafeValue<N> value) {
        return havingRestrictions.andNumber(value);
    }

    @Override
    public OnGoingNumberRestriction having(Number value) {
        return havingRestrictions.and(value);
    }

    @Override
    public OnGoingDateRestriction havingDate(TypeSafeValue<Date> value) {
        return havingRestrictions.andDate(value);
    }

    @Override
    public OnGoingDateRestriction having(Date value) {
        return havingRestrictions.and(value);
    }

    @Override
    public OnGoingTextRestriction havingString(TypeSafeValue<String> value) {
        return havingRestrictions.andString(value);
    }

    @Override
    public OnGoingTextRestriction having(String value) {
        return havingRestrictions.and(value);
    }

    @Override
    public RestrictionChainable havingExists(TypeSafeSubQuery<?> subquery) {
        return havingRestrictions.andExists(subquery);
    }

    @Override
    public RestrictionChainable havingNotExists(TypeSafeSubQuery<?> subquery) {
        return havingRestrictions.andNotExists(subquery);
    }

    /**
     * Kicks off order by's. Use desc/asc afterwards to order by something.
     */
    public OnGoingOrderBy orderBy() {
        return orderBys;
    }

    @Override
    public TypeSafeValue<Boolean> groupBy(Boolean val) {
        return groupBys.add(toValue(val));
    }

    @Override
    public TypeSafeValue<Date> groupBy(Date val) {
        return groupBys.add(toValue(val));
    }

    @Override
    public <E extends Enum<E>> TypeSafeValue<E> groupBy(E val) {
        return groupBys.add(toValue(val));
    }

    @Override
    public <N extends Number> TypeSafeValue<N> groupBy(N val) {
        return groupBys.add(toValue(val));
    }

    @Override
    public TypeSafeValue<String> groupBy(String val) {
        return groupBys.add(toValue(val));
    }

    @Override
    public <T> TypeSafeValue<T> groupBy(TypeSafeValue<T> val) {
        return groupBys.add(val);
    }

    @Override
    public <VAL> TypeSafeValue<VAL> toValue(VAL value) {
        return toValue(value, null);
    }

    @SuppressWarnings("unchecked")
    public <VAL> TypeSafeValue<VAL> toValue(VAL value, DirectValueProvider<VAL> provider) {
        if (value instanceof TypeSafeValue<?>) {
            throw new IllegalArgumentException(String.format("The value [%s] is already a type safe value.", value));
        }
        @SuppressWarnings("unchecked")
        TypeSafeValue<VAL> selectedValue = (TypeSafeValue<VAL>) getRootQuery().dequeueSelectedValue();
        if (selectedValue != null) {
            return selectedValue;
        }
        List<TypeSafeQueryProxyData> invocations = dequeueInvocations();
        if (invocations.isEmpty()) {
            return toValueWithoutInvocation(value, provider);
        } else if (invocations.size() == 1) {
            // invoked with proxy
            return toValueWithInvocation(value, invocations.get(0));
        } else {
            // invalid call, only expected one invocation
            throw new IllegalStateException(String.format("[%d] invocations were made "
                    + "before transforming it to a value. The invocations were: %s",
                    invocations.size(), invocations.toString()));
        }
    }

    private <VAL> TypeSafeValue<VAL> toValueWithInvocation(VAL value, TypeSafeQueryProxyData data) {
        if (value != null) {
            // validate value is the same as the dummy value if not null
            Object dummyValue = helper.getDummyValue(data.getPropertyType());
            if (!value.equals(dummyValue)) {
                throw new IllegalArgumentException(String.format(
                        "Expected usage of property [%s]. But got value [%s]. "
                                + "Don't calculate values using proxied getters.",
                        data, value));
            }
        }
        return new ReferenceTypeSafeValue<>(this, data);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <VAL> TypeSafeValue<VAL> toValueWithoutInvocation(VAL value, DirectValueProvider<VAL> provider) {
        // direct selection
        if (value == null) {
            if (provider != null) {
                return provider.createEmptyDirectValue(this);
            }
            throw new IllegalArgumentException("No invocation was queued and the value and provider is null. "
                    + "When using restrictions, don't use .eq(null), use .isNull() instead.");
        }
        if (value instanceof TypeSafeQueryProxy) {
            // required when selecting full hibernate objects (for example when using select distinct hobj)
            return new ReferenceTypeSafeValue<>(this, ((TypeSafeQueryProxy) value).getTypeSafeProxyData());
        } else if (value instanceof String) {
            return (DirectTypeSafeValue<VAL>) new DirectTypeSafeStringValue(this, (String) value);
        } else if (value instanceof HibernateProxy) {
            return new DirectTypeSafeValue(this, ((HibernateProxy) value)
                    .getHibernateLazyInitializer().getIdentifier());
        } else if (helper.isEntity(value.getClass())) {
            return new DirectTypeSafeValue(this, helper.getIdentifier(value));
        } else {
            return new DirectTypeSafeValue<>(this, value);
        }
    }

    @Override
    public String toFormattedString() {
        HqlQueryBuilderParamsImpl params = new HqlQueryBuilderParamsImpl();
        params.setBuildingForDisplay(true);
        return toHqlQuery(params).toFormattedString();
    }

    @Override
    public <VAL> CustomTypeSafeValue<VAL> customValue(Class<VAL> valueClass, String hql, Object... params) {
        return new CustomTypeSafeValue<>(this, valueClass, HqlQueryValueImpl.hql(hql, params));
    }

    @Override
    public <VAL> CaseTypeSafeValue<VAL> caseWhenValue(Class<VAL> valueClass) {
        return new CaseTypeSafeValue<>(this, valueClass);
    }

    /**
     * {@inheritDoc}
     * Delegates to the dataTree.
     */
    @Override
    public boolean isInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join) {
        return dataTree.isInScope(data, join);
    }

    @Override
    public void validateInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join) {
        if (!isInScope(data, join)) {
            throw new ValueNotInScopeException("Attempting to use data which is not in scope. " + data);
        }
    }

    @Override
    public void validateInScope(TypeSafeValue<?> value, TypeSafeQueryProxyData join) {
        new TypeSafeQueryScopeValidatorImpl(this, join).validateInScope(value);
    }

    /**
     * The projections, can be called to add extra projections.
     */
    public TypeSafeQueryProjections getProjections() {
        return projections;
    }

    @Override
    public RestrictionsGroup getRestrictions() {
        return whereRestrictions;
    }

    @Override
    public boolean containsRestriction(Restriction restriction) {
        return whereRestrictions.contains(restriction);
    }

    @Override
    public TypeSafeQueryGroupBys getGroupBys() {
        return groupBys;
    }

    @Override
    public TypeSafeQueryOrderBys getOrderBys() {
        return orderBys;
    }

    @Override
    public <T> TypeSafeSubQuery<T> subquery(Class<T> clazz) {
        return new TypeSafeSubQueryImpl<>(clazz, helper, this);
    }

    @Override
    public TypeSafeValueFunctions hqlFunction() {
        return new TypeSafeValueFunctions(this);
    }

    /**
     * Compose a query object with the selections, from, wheres, group bys and order bys.
     */
    protected HqlQuery toHqlQuery(HqlQueryBuilderParams params) {
        HqlQuery query = new HqlQuery();

        // append select part:
        projections.appendTo(query, params);

        // append from part + their joins:
        dataTree.appendTo(query, params);

        // append where part:
        HqlQueryValue hqlWhereRestrictions = whereRestrictions.toHqlQueryValue(params);
        query.appendWhere(hqlWhereRestrictions.getHql());
        query.addParams(hqlWhereRestrictions.getParams());

        // append group part:
        groupBys.appendTo(query, params);

        if (!havingRestrictions.isEmpty() && params.isSelectingCount()) {
            throw new IllegalArgumentException("Cannot create a selecting count query when having is used.");
        }
        HqlQueryValue hqlHavingRestrictions = havingRestrictions.toHqlQueryValue(params);
        query.appendHaving(hqlHavingRestrictions.getHql());
        query.addParams(hqlHavingRestrictions.getParams());

        // append order part:
        orderBys.appendTo(query, params);

        return query;
    }

}
