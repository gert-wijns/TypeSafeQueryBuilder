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
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.values.CaseTypeSafeValue;
import be.shad.tsqb.values.HqlQueryBuilderParams;
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

    @Override
    protected void initializeDefaults() {
        // no defaults in subquery.
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeSubQueryImpl<T>(context, this);
    }

    /**
     * Copy constructor
     */
    protected TypeSafeSubQueryImpl(CopyContext context, TypeSafeSubQueryImpl<T> original) {
        super(context, original);
        this.valueClass = original.valueClass;
        this.parentQuery = context.get(parentQuery);
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
    public T select(T value) {
        getProjections().project(value, null);
        return helper.getDummyValue(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T select(TypeSafeValue<T> value) {
        getProjections().project(value, null).getValueClass();
        return helper.getDummyValue(valueClass);
    }

    /**
     * Create an hql query as value for this subquery.
     */
    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        if (getProjections().getProjections().isEmpty()) {
            throw new IllegalStateException("Attempting to use a subquery without projections. This is most likely a mistake. "
                    + "If you are using exists/not exists, then use it by calling the selectExists or selectNotExists on "
                    + "this subquery instead of another custom way, or select a value.");
        }
        if (params.isCreatingOrderingBy()) {
            throw new IllegalStateException("Attempting to use a subquery in an order by within a function, "
                    + "hibernate does not support this. An alternative can be to apply the function on the "
                    + "subquery select value and use the subquery as a whole without the function to order.");
        }
        HqlQuery query = toHqlQuery(params);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionPredicate getDefaultRestrictionPredicate() {
        return getRootQuery().getDefaultRestrictionPredicate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultRestrictionPredicate(RestrictionPredicate restrictionValuePredicate) {
        getRootQuery().setDefaultRestrictionPredicate(restrictionValuePredicate);
    }

    @Override
    public T select() {
        return getRootQuery().queueValueSelected(this);
    }

    /**
     * Delegate to root.
     */
    @Override
    public void setHqlAlias(Object value, String alias) {
        getRootQuery().setHqlAlias(value, alias);
    }

    /**
     * Delegate to root.
     */
    @Override
    public <V> V getByHqlAlias(String alias) {
        return getRootQuery().getByHqlAlias(alias);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean selectExists() {
        CaseTypeSafeValue<Boolean> caseValue = new CaseTypeSafeValue<Boolean>(getParentQuery(), Boolean.class);
        caseValue.is(Boolean.TRUE).whenExists(this);
        caseValue.is(Boolean.FALSE).otherwise();
        return caseValue.select();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean selectNotExists() {
        CaseTypeSafeValue<Boolean> caseValue = new CaseTypeSafeValue<Boolean>(getParentQuery(), Boolean.class);
        caseValue.is(Boolean.FALSE).whenExists(this);
        caseValue.is(Boolean.TRUE).otherwise();
        return caseValue.select();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long selectCount() {
        getProjections().project(hqlFunction().count(), null);
        select();
        return helper.getDummyValue(Long.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long selectCountDistinct(T val) {
        getProjections().project(hqlFunction().countDistinct(val).select(), null);
        select();
        return helper.getDummyValue(Long.class);
    }

    @Override
    public TypeSafeNameds named() {
        return getRootQuery().named();
    }

}
