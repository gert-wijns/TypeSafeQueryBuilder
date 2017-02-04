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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQuerySelectionProxyData;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQuerySelectionProxy;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.restrictions.Restriction;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.selection.SelectionValueTransformer;
import be.shad.tsqb.selection.collection.ResultIdentifierBinder;
import be.shad.tsqb.selection.collection.ResultIdentifierBinding;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroup;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupImpl;
import be.shad.tsqb.selection.parallel.MapSelectionMerger;
import be.shad.tsqb.selection.parallel.SelectPair;
import be.shad.tsqb.selection.parallel.SelectTriplet;
import be.shad.tsqb.selection.parallel.SelectValue;
import be.shad.tsqb.selection.parallel.SelectionMerger;
import be.shad.tsqb.selection.parallel.SelectionMerger1;
import be.shad.tsqb.selection.parallel.SelectionMerger2;
import be.shad.tsqb.selection.parallel.SelectionMerger3;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;
import be.shad.tsqb.values.RestrictionTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Maintains the invocationQueue, provides the entity aliases and buffers the last selected value.
 */
public class TypeSafeRootQueryImpl extends AbstractTypeSafeQuery implements TypeSafeRootQuery, TypeSafeRootQueryInternal {

    private static final String SELECT_RESULT_GROUP = "g0";
    private List<TypeSafeQueryProxyData> invocationQueue;
    private Map<String, TypeSafeQueryProxy> customAliasedProxies;
    private TypeSafeNameds namedObjects;
    private TypeSafeValue<?> lastSelectedValue;
    private TypeSafeQuerySelectionProxyData lastInvokedSelectionData;
    private RestrictionPredicate restrictionPredicate;
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
        restrictionPredicate = context.get(original.restrictionPredicate);
        lastSelectedValue = context.get(original.lastSelectedValue);
        lastInvokedSelectionData = null;
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
    public void queueInvokedSelection(TypeSafeQuerySelectionProxyData lastInvokedSelection) {
        this.lastInvokedSelectionData = lastInvokedSelection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearInvokedSelection() {
        this.lastInvokedSelectionData = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String dequeueInvokedProjectionPath() {
        String lastInvokedProjectionPath = lastInvokedSelectionData != null ?
                lastInvokedSelectionData.getEffectivePropertyPath(): null;
        this.lastInvokedSelectionData = null;
        return lastInvokedProjectionPath;
    }

    /**
     * {@inheritDoc}
     */
    public void invocationWasMade(TypeSafeQueryProxyData data) {
        invocationQueue.add(data);
        // reset the invoked projection path, the getter was called without reason?
        lastInvokedSelectionData = null;
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
        if (invocations.isEmpty()) {
            return null;
        }
        if (invocations.size() > 1) {
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
    public RestrictionPredicate getDefaultRestrictionPredicate() {
        return restrictionPredicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultRestrictionPredicate(RestrictionPredicate restrictionPredicate) {
        this.restrictionPredicate = restrictionPredicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(Object value) {
    	selectValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectValue(Object value) {
        getProjections().project(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T select(Class<T> resultClass) {
        return select(resultClass, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean selectBoolean(Restriction restriction) {
        return new RestrictionTypeSafeValue(this, restriction).select();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <ID, T extends ID> T select(Class<T> resultClass, ResultIdentifierBinder<ID> resultIdentifierBinder) {
        TypeSafeQuerySelectionGroup resultGroup = new TypeSafeQuerySelectionGroupImpl(
                SELECT_RESULT_GROUP, resultClass, true, null, null);
        T proxy = helper.createTypeSafeSelectProxy(this, resultClass, resultGroup);
        return doBind(proxy, resultGroup, resultIdentifierBinder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <ID, T extends ID> T select(Collection<T> collection, Class<T> subselectClass, ResultIdentifierBinder<ID> resultIdentifierBinder) {
        if (lastInvokedSelectionData == null) {
            throw new IllegalStateException("The collection was not provided by calling a getSomeCollection method on a selection proxy.");
        }
        TypeSafeQuerySelectionGroup resultGroup = new TypeSafeQuerySelectionGroupImpl(
            createSelectGroupAlias(), subselectClass, false, null, lastInvokedSelectionData);
        T proxy = getHelper().createTypeSafeSelectProxy(this, subselectClass, resultGroup);
        lastInvokedSelectionData = null;
        return doBind(proxy, resultGroup, resultIdentifierBinder);
    }

    /**
     * If the binder is not null, then call it and capture all of the 'bind' calls
     * to use as identity paths.
     */
    private <ID, T extends ID> T doBind(T proxy, final TypeSafeQuerySelectionGroup resultGroup,
            ResultIdentifierBinder<ID> resultIdentifierBinder) {
        if (resultIdentifierBinder != null) {
            resultIdentifierBinder.bind(new ResultIdentifierBinding() {
                @Override
                public void bind(Object value) {
                    if (lastInvokedSelectionData == null) {
                        throw new IllegalStateException("Bind was called without calling a getter on the result proxy.");
                    }
                    resultGroup.addResultIdentifierPropertyPath(lastInvokedSelectionData.getEffectivePropertyPath());
                    lastInvokedSelectionData = null;
                }
            }, proxy);
        }
        return proxy;
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
        TypeSafeQuerySelectionProxyData parent = ((TypeSafeQuerySelectionProxy) resultDto).getTypeSafeQuerySelectionProxyData();
        return getHelper().createTypeSafeSelectProxy(this, subselectClass, new TypeSafeQuerySelectionGroupImpl(
                createSelectGroupAlias(), subselectClass, false, merger, parent));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, K, V> Map<K, V> selectMergeValues(T resultDto, MapSelectionMerger<T, K, V> merger) {
        return selectMergeValues(resultDto, Map.class, (SelectionMerger) merger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, A> SelectValue<A> selectMergeValues(T resultDto, SelectionMerger1<T, A> merger) {
        return (SelectValue) selectMergeValues(resultDto, SelectValue.class, (SelectionMerger) merger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, A, B> SelectPair<A, B> selectMergeValues(T resultDto, SelectionMerger2<T, A, B> merger) {
        return (SelectPair) selectMergeValues(resultDto, SelectPair.class, (SelectionMerger) merger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, A, B, C> SelectTriplet<A, B, C> selectMergeValues(T resultDto, SelectionMerger3<T, A, B, C> merger) {
        return (SelectTriplet) selectMergeValues(resultDto, SelectTriplet.class, (SelectionMerger) merger);
    }

    /**
     * {@inheritDoc}
     */
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
