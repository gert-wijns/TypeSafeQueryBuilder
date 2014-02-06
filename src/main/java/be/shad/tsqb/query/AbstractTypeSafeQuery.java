package be.shad.tsqb.query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQueryProxyDataTree;
import be.shad.tsqb.grouping.OnGoingGroupBy;
import be.shad.tsqb.grouping.TypeSafeQueryGroupBys;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.joins.TypeSafeQueryJoin;
import be.shad.tsqb.ordering.OnGoingOrderBy;
import be.shad.tsqb.ordering.TypeSafeQueryOrderBys;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.restrictions.OnGoingBooleanRestriction;
import be.shad.tsqb.restrictions.OnGoingDateRestriction;
import be.shad.tsqb.restrictions.OnGoingEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryDateRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingSubQueryTextRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.selection.TypeSafeQueryProjections;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;
import be.shad.tsqb.values.TypeSafeValueFunctions;

public abstract class AbstractTypeSafeQuery implements TypeSafeQuery, TypeSafeQueryInternal {
    protected final TypeSafeQueryHelper helper;
    private TypeSafeRootQueryInternal rootQuery;

    private final TypeSafeQueryProxyDataTree dataTree;
    private final TypeSafeQueryProjections projections = new TypeSafeQueryProjections(this); 
    private final RestrictionsGroup restrictions = new RestrictionsGroup(this, null); 
    private final TypeSafeQueryGroupBys groupBys = new TypeSafeQueryGroupBys(this);
    private final TypeSafeQueryOrderBys orderBys = new TypeSafeQueryOrderBys(this);
    
    public AbstractTypeSafeQuery(TypeSafeQueryHelper helper) {
        this.helper = helper;
        this.dataTree = new TypeSafeQueryProxyDataTree(helper, this);
    }
    
    @Override
    public TypeSafeQueryProxyDataTree getDataTree() {
        return dataTree;
    }
    
    @Override
    public TypeSafeRootQueryInternal getRootQuery() {
        return rootQuery;
    }
    
    public void setRootQuery(TypeSafeRootQueryInternal rootQuery) {
        this.rootQuery = rootQuery;
    }

    public <T> T from(Class<T> fromClass) {
        return helper.createTypeSafeFromProxy(this, fromClass);
    }
    

    public <T> T join(Collection<T> anyCollection) {
        return join(anyCollection, JoinType.Inner);
    }

    public <T> T join(T anyObject) {
        return join(anyObject, JoinType.Inner);
    }

    @SuppressWarnings("unchecked")
    public <T> T join(Collection<T> anyCollection, JoinType joinType) {
        return (T) join(joinType);
    }

    @SuppressWarnings("unchecked")
    public <T> T join(T anyObject, JoinType joinType) {
        return (T) join(joinType);
    }
    
    @Override
    public <T> TypeSafeQueryJoin<T> getJoin(T obj) {
        if(!(obj instanceof TypeSafeQueryProxy)) {
            throw new IllegalArgumentException("Can only get the join using a TypeSafeQueryProxy instance.");
        }
        return dataTree.getJoin(((TypeSafeQueryProxy) obj).getTypeSafeProxyData());
    }

    private TypeSafeQueryProxy join(JoinType joinType) {
        List<TypeSafeQueryProxyData> invocations = rootQuery.dequeueInvocations();
        if( invocations.size() != 1 ) {
            throw new IllegalStateException(String.format("There are %d invocations pending. Only 1 should be pending. "
                    + "The one that was used to call join(value, joinType).", invocations.size()));
        }
        TypeSafeQueryProxyData data = invocations.get(0);
        data.setJoinType(joinType);
        return data.getProxy();
    }

    @Override
    public RestrictionChainable where() {
        return restrictions;
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
    public <E extends Enum<E>> OnGoingSubQueryEnumRestriction<E> wheree(TypeSafeSubQuery<E> value) {
        return restrictions.ande(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> wheree(TypeSafeValue<E> value) {
        return restrictions.ande(value);
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
    public OnGoingBooleanRestriction whereb(TypeSafeValue<Boolean> value) {
        return restrictions.andb(value);
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
    public OnGoingSubQueryNumberRestriction wheren(
            TypeSafeSubQuery<Number> value) {
        return restrictions.andn(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingNumberRestriction wheren(TypeSafeValue<Number> value) {
        return restrictions.andn(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingSubQueryTextRestriction wheret(TypeSafeSubQuery<String> value) {
        return restrictions.andt(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingTextRestriction wheret(TypeSafeValue<String> value) {
        return restrictions.andt(value);
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
    public OnGoingSubQueryDateRestriction whered(
            TypeSafeSubQuery<Date> value) {
        return restrictions.andd(value);
    }

    /**
     * Delegate to restrictions.
     */
    @Override
    public OnGoingDateRestriction whered(TypeSafeValue<Date> value) {
        return restrictions.andd(value);
    }
    
    /**
     * Kicks off order by's. Use desc/asc afterwards to order by something.
     */
    public OnGoingOrderBy orderBy() {
        return orderBys;
    }

    @Override
    public OnGoingGroupBy groupBy(Number val) {
        return groupBys.and(val);
    }

    @Override
    public OnGoingGroupBy groupBy(String val) {
        return groupBys.and(val);
    }

    @Override
    public OnGoingGroupBy groupBy(Enum<?> val) {
        return groupBys.and(val);
    }

    @Override
    public OnGoingGroupBy groupBy(Boolean val) {
        return groupBys.and(val);
    }
    
    @Override
    public OnGoingGroupBy groupBy(Date val) {
        return groupBys.and(val);
    }

    @Override
    public OnGoingGroupBy groupBy(TypeSafeValue<?> val) {
        return groupBys.and(val);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <VAL> TypeSafeValue<VAL> toValue(VAL value) {
        List<TypeSafeQueryProxyData> invocations = dequeueInvocations();
        if( invocations.isEmpty() ) {
            // direct selection
            return new DirectTypeSafeValue<VAL>(this, value);
        } else if( invocations.size() == 1 ) {
            // invoked with proxy
            return new ReferenceTypeSafeValue<VAL>(this, invocations.get(0));
        } else {
            // invalid call, only expected one invocation
            throw new IllegalStateException();
        }
    }

    public boolean isInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join) {
        return dataTree.isInScope(data, join);
    }
    
    public TypeSafeQueryProjections getProjections() {
        return projections;
    }

    @Override
    public RestrictionsGroup getRestrictions() {
        return restrictions;
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
    public TypeSafeValueFunctions function() {
        return new TypeSafeValueFunctions(this);
    }

    /**
     * Compose a query object with the selections, from, wheres, group bys and order bys.
     */
    public HqlQuery toHqlQuery() {
        HqlQuery query = new HqlQuery();
        
        // append select part:
        projections.appendTo(query);
        
        // append from part + their joins:
        dataTree.appendTo(query);
        
        // append where part:
        HqlQueryValue hqlRestrictions = restrictions.toHqlQueryValue();
        query.appendWhere(hqlRestrictions.getHql());
        query.addParams(hqlRestrictions.getParams());
        
        // append group part:
        groupBys.appendTo(query);
        
        // append order part:
        orderBys.appendTo(query);
        
        return query;
    }
    
    
}
