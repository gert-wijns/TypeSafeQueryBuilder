package be.shad.tsqb.query;

import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Mostly delegates methods to the root query.
 * <p>
 * Overrides the isInScope method to also check parent queries.
 * 
 * @see TypeSafeSubQuery
 */
public class TypeSafeSubQueryImpl<T> extends AbstractTypeSafeQuery implements TypeSafeSubQuery<T> {
    private TypeSafeQueryInternal parentQuery;
    private final Class<T> valueClass;

    public TypeSafeSubQueryImpl(Class<T> valueClass, 
            TypeSafeQueryHelper helper,
            TypeSafeQueryInternal parentQuery) {
        super(helper);
        this.valueClass = valueClass;
        this.parentQuery = parentQuery;
        setRootQuery(parentQuery.getRootQuery());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQueryInternal getParentQuery() {
        return parentQuery;
    }
    
    @Override
    public Class<T> getValueClass() {
        return valueClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(T value) {
        getProjections().project(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(TypeSafeValue<T> value) {
        getProjections().project(value, null);
    }
    
    /**
     * Create an hql query as value for this subquery.
     */
    @Override
    public HqlQueryValue toHqlQueryValue() {
        HqlQuery query = toHqlQuery();
        return new HqlQueryValueImpl("(" +query.getHql() + ")", query.getParams());
    }

    /**
     * In scope if it is in this query's scope or in its parents' scope.
     */
    @Override
    public boolean isInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join) {
        if( super.isInScope(data, join) ) {
            return true;
        }
        return parentQuery.isInScope(data, join);
    }
    
    /**
     * Delegate to root.
     */
    @Override
    public List<TypeSafeQueryProxyData> dequeueInvocations() {
        return getRootQuery().dequeueInvocations();
    }

    /**
     * Delegate to root.
     */
    @Override
    public TypeSafeQueryProxyData dequeueInvocation() {
        return getRootQuery().dequeueInvocation();
    }

    /**
     * Delegate to root.
     */
    @Override
    public void invocationWasMade(TypeSafeQueryProxyData data) {
        getRootQuery().invocationWasMade(data);
    }

    /**
     * Delegate to root.
     */
    @Override
    public String createEntityAlias() {
        return getRootQuery().createEntityAlias();
    }

    @Override
    public T select() {
        return getRootQuery().queueValueSelected(this);
    }
    
}
