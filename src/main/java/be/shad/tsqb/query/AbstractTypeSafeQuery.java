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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQueryProxyDataTree;
import be.shad.tsqb.exceptions.JoinException;
import be.shad.tsqb.exceptions.ValueNotInScopeException;
import be.shad.tsqb.grouping.TypeSafeQueryGroupBys;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.ordering.TypeSafeQueryOrderBys;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.restrictions.DirectValueProvider;
import be.shad.tsqb.restrictions.OnGoingBooleanRestriction;
import be.shad.tsqb.restrictions.OnGoingDateRestriction;
import be.shad.tsqb.restrictions.OnGoingEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
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
    private final RestrictionsGroupInternal restrictions;
    private final TypeSafeQueryGroupBys groupBys;
    private final TypeSafeQueryOrderBys orderBys;
    
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
        this.restrictions = context.get(original.restrictions);
        this.groupBys = context.get(original.groupBys);
        this.orderBys = context.get(original.orderBys);
    }
    
    /**
     * Provide the opportunity to initialize the empty lists/initial counters,
     * required because super values are initialized before subclass values.
     */
    protected abstract void initializeDefaults();

    public AbstractTypeSafeQuery(TypeSafeQueryHelper helper) {
        initializeDefaults();
        this.helper = helper;
        this.dataTree = new TypeSafeQueryProxyDataTree(helper, this);
        this.groupedRestrictionsBuilder = new RestrictionsGroupFactoryImpl(this);
        this.arithmeticsBuilder = new ArithmeticTypeSafeValueFactoryImpl(this);
        this.projections = new TypeSafeQueryProjections(this);
        this.restrictions = new RestrictionsGroupImpl(
                this, null, RestrictionsGroupBracketsPolicy.Never);
        this.groupBys = new TypeSafeQueryGroupBys();
        this.orderBys = new TypeSafeQueryOrderBys(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQueryHelper getHelper() {
        return helper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQueryProxyDataTree getDataTree() {
        return dataTree;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T from(Class<T> fromClass) {
        return from(fromClass, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T from(Class<T> fromClass, String name) {
        T from = helper.createTypeSafeFromProxy(this, fromClass);
        if (name != null) {
            named().name(from, name);
        }
        return from;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S, T extends S> T getAsSubtype(S proxy, Class<T> subtype) {
        return helper.createTypeSafeSubtypeProxy(this, proxy, subtype);
    }
    
    /**
     * {@inheritDoc}
     */
    public <T> T join(Collection<T> anyCollection) {
        return handleJoin(null, null, null, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T join(Collection<T> anyCollection, String name) {
        return handleJoin(null, null, name, false);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T join(T anyObject) {
        return handleJoin(anyObject, null, null, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T join(T anyObject, String name) {
        return handleJoin(anyObject, null, name, false);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T join(Collection<T> anyCollection, JoinType joinType) {
        return handleJoin((T) null, joinType, null, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T join(Collection<T> anyCollection, JoinType joinType, String name) {
        return handleJoin((T) null, joinType, name, false);
    }
    
    /**
     * {@inheritDoc}
     */
    public <T> T join(T anyObject, JoinType joinType) {
        return handleJoin(anyObject, joinType, null, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T join(T anyObject, JoinType joinType, String name) {
        return handleJoin(anyObject, joinType, name, false);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T join(Collection<T> anyCollection, JoinType joinType, boolean createAdditionalJoin) {
        return handleJoin((T) null, joinType, null, createAdditionalJoin);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T join(Collection<T> anyCollection, JoinType joinType, String name, boolean createAdditionalJoin) {
        return handleJoin((T) null, joinType, name, createAdditionalJoin);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T join(T anyObject, JoinType joinType, boolean createAdditionalJoin) {
        return handleJoin(anyObject, joinType, null, createAdditionalJoin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T join(T anyObject, JoinType joinType, String name, boolean createAdditionalJoin) {
        return handleJoin(anyObject, joinType, name, createAdditionalJoin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> WhereRestrictions joinWith(T obj) {
        if(!(obj instanceof TypeSafeQueryProxy)) {
            throw new IllegalArgumentException("Can only get the join using a TypeSafeQueryProxy instance.");
        }
        return dataTree.getJoinRestrictions(((TypeSafeQueryProxy) obj).getTypeSafeProxyData());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    private <T> T handleJoin(T obj, JoinType joinType, String name, boolean createAdditionalJoin) {
        TypeSafeQueryProxyData data = rootQuery.dequeueInvocation();
        if( obj instanceof TypeSafeQueryProxy ) {
            data = ((TypeSafeQueryProxy) obj).getTypeSafeProxyData();
        }
        if( !data.getProxyType().isEntity() ) {
            throw new JoinException(String.format("Attempting to join an object "
                    + "which does not represent an entity. ", data.getAlias()));
        }
        if( createAdditionalJoin ) {
            data = helper.createTypeSafeJoinProxy(this, data.getParent(), 
                    data.getPropertyPath(), data.getPropertyType());
        }
        data.setJoinType(joinType == null ? JoinType.Default: joinType);
        if (name != null) {
            named().name(data.getProxy(), name);
        }
        return (T) data.getProxy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable where() {
        return restrictions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable and(RestrictionHolder restriction, RestrictionHolder... restrictions) {
        return this.restrictions.and(restriction, restrictions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable or(RestrictionHolder restriction, RestrictionHolder... restrictions) {
        return this.restrictions.or(restriction, restrictions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable where(HqlQueryValue restriction) {
        return restrictions.and(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable where(RestrictionsGroup group) {
        return restrictions.and(group.getRestrictions());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable where(Restriction restriction) {
        return restrictions.and(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArithmeticTypeSafeValueFactory getArithmeticsBuilder() {
        return arithmeticsBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionsGroupFactory getGroupedRestrictionsBuilder() {
        return groupedRestrictionsBuilder;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable whereExists(TypeSafeSubQuery<?> subquery) {
        return restrictions.andExists(subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable whereNotExists(TypeSafeSubQuery<?> subquery) {
        return restrictions.andNotExists(subquery);
    }
    
    /**
     * Delegate to restrictions.
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> where(E value) {
        return restrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> whereEnum(TypeSafeValue<E> value) {
        return restrictions.andEnum(value);
    }
    
    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingBooleanRestriction where(Boolean value) {
        return restrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingBooleanRestriction whereBoolean(TypeSafeValue<Boolean> value) {
        return restrictions.andBoolean(value);
    }
    
    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingNumberRestriction where(Number value) {
        return restrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingTextRestriction where(String value) {
        return restrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public <N extends Number> OnGoingNumberRestriction whereNumber(TypeSafeValue<N> value) {
        return restrictions.andNumber(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingTextRestriction whereString(TypeSafeValue<String> value) {
        return restrictions.andString(value);
    }
    
    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingDateRestriction where(Date value) {
        return restrictions.and(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingDateRestriction whereDate(TypeSafeValue<Date> value) {
        return restrictions.andDate(value);
    }
    
    /**
     * Kicks off order by's. Use desc/asc afterwards to order by something.
     */
    public OnGoingOrderBy orderBy() {
        return orderBys;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeValue<Boolean> groupBy(Boolean val) {
        return groupBys.add(toValue(val));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeValue<Date> groupBy(Date val) {
        return groupBys.add(toValue(val));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E>> TypeSafeValue<E> groupBy(E val) {
        return groupBys.add(toValue(val));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> TypeSafeValue<N> groupBy(N val) {
        return groupBys.add(toValue(val));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeValue<String> groupBy(String val) {
        return groupBys.add(toValue(val));
    }
    
    @Override
    public <T> TypeSafeValue<T> groupBy(TypeSafeValue<T> val) {
        return groupBys.add(val);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <VAL> TypeSafeValue<VAL> toValue(VAL value) {
        return toValue(value, null);
    }

    public <VAL> TypeSafeValue<VAL> toValue(VAL value, DirectValueProvider<VAL> provider) {
        if (value instanceof TypeSafeValue<?>) {
            throw new IllegalArgumentException(String.format("The value [%s] is already a type safe value.", value));
        }
        List<TypeSafeQueryProxyData> invocations = dequeueInvocations();
        if( invocations.isEmpty() ) {
            // direct selection
            if (value == null) {
                if (provider != null) {
                    return provider.createEmptyDirectValue();
                }
                throw new IllegalArgumentException("No invocation was queued and the value and provider is null. "
                        + "When using restrictions, don't use .eq(null), use .isNull() instead.");
            }
            if (value instanceof TypeSafeQueryProxy) {
                // required when selecting full hibernate objects (for example when using select distinct hobj)
                return new ReferenceTypeSafeValue<VAL>(this, ((TypeSafeQueryProxy) value).getTypeSafeProxyData());
            } else if (value instanceof String) {
                @SuppressWarnings("unchecked")
                DirectTypeSafeValue<VAL> directValue = (DirectTypeSafeValue<VAL>) 
                        new DirectTypeSafeStringValue(this, (String) value);
                return directValue;
            } else {
                return new DirectTypeSafeValue<VAL>(this, value);
            }
        } else if( invocations.size() == 1 ) {
            // invoked with proxy
            return new ReferenceTypeSafeValue<VAL>(this, invocations.get(0));
        } else {
            // invalid call, only expected one invocation
            throw new IllegalStateException(String.format("[%d] invocations were "
                    + "made before transforming it to a value.", invocations.size()));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <VAL> CustomTypeSafeValue<VAL> customValue(Class<VAL> valueClass, String hql, Object... params) {
        return new CustomTypeSafeValue<>(this, valueClass, HqlQueryValueImpl.hql(hql, params));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <VAL> CaseTypeSafeValue<VAL> caseWhenValue(Class<VAL> valueClass) {
        return new CaseTypeSafeValue<>(this, valueClass);
    }

    /**
     * {@inheritDoc}
     * 
     * Delegates to the dataTree.
     */
    @Override
    public boolean isInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join) {
        return dataTree.isInScope(data, join);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join) {
        if( !isInScope(data, join) ) {
            throw new ValueNotInScopeException("Attempting to use data which is not in scope. " + data);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionsGroup getRestrictions() {
        return restrictions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQueryGroupBys getGroupBys() {
        return groupBys;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQueryOrderBys getOrderBys() {
        return orderBys;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> TypeSafeSubQuery<T> subquery(Class<T> clazz) {
        return new TypeSafeSubQueryImpl<>(clazz, helper, this);
    }

    /**
     * {@inheritDoc}
     */
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
        HqlQueryValue hqlRestrictions = restrictions.toHqlQueryValue(params);
        query.appendWhere(hqlRestrictions.getHql());
        query.addParams(hqlRestrictions.getParams());
        
        // append group part:
        groupBys.appendTo(query, params);
        
        // append order part:
        orderBys.appendTo(query, params);
        
        return query;
    }
    
}
