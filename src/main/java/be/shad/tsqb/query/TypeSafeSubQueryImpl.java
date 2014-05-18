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

    /**
     * Delegate to root.
     */
    @Override
    public void registerCustomAliasForProxy(Object value, String alias) {
        getRootQuery().registerCustomAliasForProxy(value, alias);
    }

    /**
     * Delegate to root.
     */
    @Override
    public <V> V getProxyByCustomEntityAlias(String alias) {
        return getRootQuery().getProxyByCustomEntityAlias(alias);
    }
    
}
