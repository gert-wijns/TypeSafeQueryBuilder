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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.restrictions.predicate.RestrictionValuePredicate;
import be.shad.tsqb.selection.SelectionValueTransformer;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroup;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupImpl;
import be.shad.tsqb.selection.parallel.SelectPair;
import be.shad.tsqb.selection.parallel.SelectTriplet;
import be.shad.tsqb.selection.parallel.SelectValue;
import be.shad.tsqb.selection.parallel.SelectionMerger;
import be.shad.tsqb.selection.parallel.SelectionMerger1;
import be.shad.tsqb.selection.parallel.SelectionMerger2;
import be.shad.tsqb.selection.parallel.SelectionMerger3;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Maintains the invocationQueue, provides the entity aliases and buffers the last selected value.
 */
public class TypeSafeRootQueryImpl extends AbstractTypeSafeQuery implements TypeSafeRootQuery, TypeSafeRootQueryInternal {
    
    private List<TypeSafeQueryProxyData> invocationQueue;
    private Map<String, TypeSafeQueryProxy> customAliasedProxies;
    private TypeSafeNameds namedObjects;
    private TypeSafeValue<?> lastSelectedValue;
    private String lastInvokedProjectionPath;
    private RestrictionValuePredicate restrictionValuePredicate;
    private int entityAliasCount;
    private int selectionGroupAliasCount;
    private int firstResult;
    private int maxResults;
    
    @Override
    public TypeSafeRootQuery copy() {
        return new CopyContext().get(this);
    }
    
    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeRootQueryImpl(context, this);
    }
    
    /**
     * Initializes the empty lists/initial counters,
     * was put into a separate method because super()
     * is called before all predefined values are initialized.
     */
    @Override
    protected void initializeDefaults() {
        invocationQueue = new LinkedList<>();
        customAliasedProxies = new HashMap<>();
        entityAliasCount = 1;
        selectionGroupAliasCount = 1;
        firstResult = -1;
        maxResults = -1;
    }

    /**
     * Copy constructor
     */
    protected TypeSafeRootQueryImpl(CopyContext context, TypeSafeRootQueryImpl original) {
        super(context, original);
        for(TypeSafeQueryProxyData data: original.invocationQueue) {
            invocationQueue.add(context.get(data));
        }
        namedObjects = context.get(original.namedObjects);
        for(Entry<String, TypeSafeQueryProxy> customAliasedProxy: original.customAliasedProxies.entrySet()) {
            customAliasedProxies.put(customAliasedProxy.getKey(), context.get(customAliasedProxy.getValue()));
        }
        restrictionValuePredicate = context.get(original.restrictionValuePredicate);
        lastSelectedValue = context.get(original.lastSelectedValue);
        lastInvokedProjectionPath = original.lastInvokedProjectionPath;
        entityAliasCount = original.entityAliasCount;
        selectionGroupAliasCount = original.selectionGroupAliasCount;
        firstResult = original.firstResult;
        maxResults = original.maxResults;
    }

    public TypeSafeRootQueryImpl(TypeSafeQueryHelper helper) {
        super(helper);
        setRootQuery(this);
        namedObjects = new TypeSafeNamedsImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFirstResult() {
        return firstResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQueryInternal getParentQuery() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queueInvokedProjectionPath(String lastInvokedProjectionPath) {
        this.lastInvokedProjectionPath = lastInvokedProjectionPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String dequeueInvokedProjectionPath() {
        String lastInvokedProjectionPath = this.lastInvokedProjectionPath;
        this.lastInvokedProjectionPath = null;
        return lastInvokedProjectionPath;
    }
    
    /**
     * {@inheritDoc}
     */
    public void invocationWasMade(TypeSafeQueryProxyData data) {
        invocationQueue.add(data);
        // reset the invoked projection path, the getter was called without reason?
        lastInvokedProjectionPath = null;
    }

    /**
     * {@inheritDoc}
     */
    public List<TypeSafeQueryProxyData> dequeueInvocations() {
        List<TypeSafeQueryProxyData> old = invocationQueue;
        invocationQueue = new LinkedList<>();
        return old;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQueryProxyData dequeueInvocation() {
        List<TypeSafeQueryProxyData> invocations = dequeueInvocations();
        if( invocations.isEmpty() ) {
            return null;
        }
        if( invocations.size() > 1 ) {
            throw new IllegalStateException(String.format("There are %d invocations pending. Only 1 should be pending. "
                    + "The one that was used to call join(value, joinType).", invocations.size()));
        }
        return invocations.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHqlAlias(Object value, String customAlias) {
        TypeSafeQueryProxy current;
        TypeSafeQueryProxyData queuedData = dequeueInvocation();
        if (value == null && queuedData != null) {
            current = queuedData.getProxy();
        } else if (!(value instanceof TypeSafeQueryProxy)) {
            throw new IllegalArgumentException(String.format("Value [%s] is not a TypeSafeQueryProxy", value));
        } else {
            current = (TypeSafeQueryProxy) value;
        }
        TypeSafeQueryProxy previous = customAliasedProxies.put(customAlias, current);
        if (previous != null) {
            String previousAlias = previous.getTypeSafeProxyData().getAlias();
            String currentAlias = current.getTypeSafeProxyData().getAlias();
            if (!previousAlias.equals(currentAlias)) {
                throw new IllegalArgumentException(String.format("A different proxy [%s] was already "
                        + "registered for alias [%s]. Cannot register proxy [%s]", 
                        previousAlias, customAlias, currentAlias));
            }
        }
        current.getTypeSafeProxyData().setCustomAlias(customAlias);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getByHqlAlias(String alias) {
        return (T) customAliasedProxies.get(alias);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createEntityAlias() {
        return "hobj"+ entityAliasCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createSelectGroupAlias() {
        return "g" + selectionGroupAliasCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionValuePredicate getDefaultRestrictionValuePredicate() {
        return restrictionValuePredicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultRestrictionValuePredicate(RestrictionValuePredicate restrictionValuePredicate) {
        this.restrictionValuePredicate = restrictionValuePredicate;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void select(Object value) {
        getProjections().project(value, null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T select(Class<T> resultClass) {
        TypeSafeQuerySelectionGroup resultGroup = new TypeSafeQuerySelectionGroupImpl(
                createSelectGroupAlias(), resultClass, true, null);
        return helper.createTypeSafeSelectProxy(this, resultClass, resultGroup);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T, V> V select(Class<V> transformedClass, T value, SelectionValueTransformer<T, V> transformer) {
        if (value instanceof TypeSafeQueryProxy) {
            // invocation was not added because it is not a leaf (to support method chaining).
            invocationWasMade(((TypeSafeQueryProxy) value).getTypeSafeProxyData());
        }
        getProjections().setTransformerForNextProjection(transformer);
        return helper.getDummyValue(transformedClass);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <VAL> VAL distinct(VAL value) {
        return distinct(toValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <VAL> VAL distinct(TypeSafeValue<VAL> value) {
        return hqlFunction().distinct(value).select();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T queueValueSelected(TypeSafeValue<T> value) {
        lastSelectedValue = value;
        return helper.getDummyValue(value.getValueClass());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeValue<?> dequeueSelectedValue() {
        TypeSafeValue<?> value = lastSelectedValue;
        lastSelectedValue = null;
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, SUB> SUB selectMergeValues(T resultDto, Class<SUB> subselectClass, SelectionMerger<T, SUB> merger) {
        return getHelper().createTypeSafeSelectProxy(this, subselectClass, new TypeSafeQuerySelectionGroupImpl(
                createSelectGroupAlias(), subselectClass, false, merger));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, A> SelectValue<A> selectMergeValues(T resultDto, SelectionMerger1<T, A> merger) {
        return selectMergeValues(resultDto, SelectValue.class, (SelectionMerger) merger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, A, B> SelectPair<A, B> selectMergeValues(T resultDto, SelectionMerger2<T, A, B> merger) {
        return selectMergeValues(resultDto, SelectPair.class, (SelectionMerger) merger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, A, B, C> SelectTriplet<A, B, C> selectMergeValues(T resultDto, SelectionMerger3<T, A, B, C> merger) {
        return selectMergeValues(resultDto, SelectTriplet.class, (SelectionMerger) merger);
    }
    
    @Override
    public HqlQuery toHqlQuery() {
        return super.toHqlQuery(new HqlQueryBuilderParamsImpl());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeNameds named() {
        return namedObjects;
    }

}
