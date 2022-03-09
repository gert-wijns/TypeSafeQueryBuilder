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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQuerySelectionProxyPropertyData;
import be.shad.tsqb.helper.SelectionBuilderSpec;
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
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupImpl;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupInternal;
import be.shad.tsqb.selection.parallel.MapSelectionMerger;
import be.shad.tsqb.selection.parallel.SelectPair;
import be.shad.tsqb.selection.parallel.SelectTriplet;
import be.shad.tsqb.selection.parallel.SelectValue;
import be.shad.tsqb.selection.parallel.SelectionMerger;
import be.shad.tsqb.selection.parallel.SelectionMerger1;
import be.shad.tsqb.selection.parallel.SelectionMerger2;
import be.shad.tsqb.selection.parallel.SelectionMerger3;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.RestrictionTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;
import be.shad.tsqb.values.WrappedTypeSafeValue;

/**
 * Maintains the invocationQueue, provides the entity aliases and buffers the last selected value.
 */
public class TypeSafeRootQueryImpl extends AbstractTypeSafeQuery implements TypeSafeRootQuery, TypeSafeRootQueryInternal {

    private static final String SELECT_RESULT_GROUP = "g0";
    private List<TypeSafeQueryProxyData> invocationQueue;
    private Consumer<TypeSafeQuerySelectionProxyPropertyData<?>> setSelectionValueInterceptor;
    private IdentityHashMap<Object, TypeSafeQuerySelectionGroupInternal<?, ?>> selectionProxyData;
    private Map<String, TypeSafeQueryProxy> customAliasedProxies;
    private final TypeSafeNameds namedObjects;
    private TypeSafeValue<?> lastSelectedValue;
    private TypeSafeQuerySelectionProxyPropertyData<?> lastInvokedSelectionData;
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
        selectionProxyData = new IdentityHashMap<>();
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

    @Override
    public int getFirstResult() {
        return firstResult;
    }

    @Override
    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    @Override
    public int getMaxResults() {
        return maxResults;
    }

    @Override
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    @Override
    public TypeSafeQueryInternal getParentQuery() {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SB, SR> TypeSafeQuerySelectionGroupInternal<SB, SR> getSelectionProxyData(SB select) {
        if (select instanceof TypeSafeQuerySelectionProxy) {
            return (TypeSafeQuerySelectionGroupInternal<SB, SR>) ((TypeSafeQuerySelectionProxy<SB>) select)
                    .getTypeSafeQuerySelectionProxyData();
        }
        return (TypeSafeQuerySelectionGroupInternal<SB, SR>) selectionProxyData.get(select);
    }

    @Override
    public <T> void putSelectionProxyData(T select, TypeSafeQuerySelectionGroupInternal<T, ?> builderData) {
        selectionProxyData.put(select, builderData);
    }

    @Override
    public <T> void queueInvokedSelection(TypeSafeQuerySelectionProxyPropertyData<T> lastInvokedSelection) {
        this.lastInvokedSelectionData = lastInvokedSelection;
    }

    @Override
    public void clearInvokedSelection() {
        this.lastInvokedSelectionData = null;
    }

    @Override
    public String dequeueInvokedProjectionPath() {
        String lastInvokedProjectionPath = lastInvokedSelectionData != null ?
                lastInvokedSelectionData.getPropertyPath(): null;
        this.lastInvokedSelectionData = null;
        return lastInvokedProjectionPath;
    }

    @Override
    public void invocationWasMade(TypeSafeQueryProxyData data) {
        invocationQueue.add(data);
        // reset the invoked projection path, the getter was called without reason?
        lastInvokedSelectionData = null;
    }

    @Override
    public List<TypeSafeQueryProxyData> dequeueInvocations() {
        List<TypeSafeQueryProxyData> old = invocationQueue;
        invocationQueue = new LinkedList<>();
        return old;
    }

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

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getByHqlAlias(String alias) {
        return (T) customAliasedProxies.get(alias);
    }

    @Override
    public String createEntityAlias() {
        return "hobj"+ entityAliasCount++;
    }

    @Override
    public String createSelectGroupAlias() {
        return "g" + selectionGroupAliasCount++;
    }

    @Override
    public RestrictionPredicate getDefaultRestrictionPredicate() {
        return restrictionPredicate;
    }

    @Override
    public void setDefaultRestrictionPredicate(RestrictionPredicate restrictionPredicate) {
        this.restrictionPredicate = restrictionPredicate;
    }

    @Override
    public String toFormattedSqlQuery() {
        return getHelper().toFormattedSqlQuery(this);
    }

    @Override
    public <T> void selectValue(T value) {
        TypeSafeQuerySelectionGroupInternal<T, ?> selectBuilderOrigin = getSelectionProxyData(value);
        if (selectBuilderOrigin != null) {
            selectBuilderOrigin.setResultGroup(true);
        } else {
            getProjections().project(value, null);
        }
    }

    @Override
    public <T> T select(Class<T> resultClass) {
        return select(resultClass, (ResultIdentifierBinder<Object>) null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T select(Supplier<T> selectionBuilderSupplier) {
        return (T) select(selectionBuilderSupplier.get().getClass(), (ResultIdentifierBinder<Object>) null);
    }

    @Override
    public <T> T groupSelectBy(T invocationResult) {
        setSelectionValueInterceptor = property -> {
            property.getGroup().addResultIdentifierPropertyPath(property.getPropertyPath());
        };
        return invocationResult;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T subBuilder(Supplier<T> selectionBuilderSupplier) {
        return subBuilder((Class<T>) selectionBuilderSupplier.get().getClass());
    }

    @Override
    public <T, V> T subSetBuilder(Supplier<T> selectionBuilderSupplier, Consumer<Set<V>> collection) {
        return subCollectionBuilder(selectionBuilderSupplier, collection, HashSet::new);
    }

    @Override
    public <T, V> T subListBuilder(Supplier<T> selectionBuilderSupplier, Consumer<List<V>> collection){
        return subCollectionBuilder(selectionBuilderSupplier, collection, ArrayList::new);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T, V, C extends Collection<V>> T subCollectionBuilder(Supplier<T> selectionBuilderSupplier, Consumer<C> col, Supplier<C> sup) {
        T builder = subBuilder(selectionBuilderSupplier);
        TypeSafeQuerySelectionProxy<T> proxy = (TypeSafeQuerySelectionProxy<T>) builder;
        setSelectionValueInterceptor = property -> {
            property.setCollectionSupplier((Supplier) sup);
            property.setSubGroup((TypeSafeQuerySelectionGroupInternal) proxy.getTypeSafeQuerySelectionProxyData());
            setSelectionValueInterceptor = null;
        };
        col.accept(null);
        return builder;
    }

    @Override
    public <V> void handleSetSelectionValue(TypeSafeQuerySelectionProxyPropertyData<V> property, V setterArg) {
        if (setSelectionValueInterceptor != null) {
            setSelectionValueInterceptor.accept(property);
        }

        TypeSafeQuerySelectionGroupInternal<?, V> setterArgData = getSelectionProxyData(setterArg);
        if (setterArgData != null) {
            property.setSubGroup(setterArgData);
        }
        if (property.getSubGroup() == null) {
            getProjections().project(setterArg, property);
        } else {
            clearInvokedSelection();
        }
    }

    @Override
    public <T> T subBuilder(Class<T> selectionBuilderClass) {
        SelectionBuilderSpec<T, Object> sbs = helper.createSelectionBuilderSpec(selectionBuilderClass);
        return getHelper().createTypeSafeSelectProxy(this, sbs.getSelectionBuilderClass(),
                new TypeSafeQuerySelectionGroupImpl<>(createSelectGroupAlias(), sbs));
    }

    private <P, R> TypeSafeQuerySelectionGroupInternal<P, R> getSelectionGroup(P selectDto) {
        TypeSafeQuerySelectionGroupInternal<P, R> group = getSelectionProxyData(selectDto);
        if (group == null) {
            throw new IllegalArgumentException("SelectDto doesn't have a known matching builder! SelectDto: " + selectDto);
        }
        return group;
    }

    @Override
    public Boolean selectBoolean(Restriction restriction) {
        return new RestrictionTypeSafeValue(this, restriction).select();
    }

    @Override
    public <ID, T extends ID> T select(Class<T> resultClass, ResultIdentifierBinder<ID> resultIdentifierBinder) {
        if (!getProjections().getProjections().isEmpty() && getProjections().getResultClass() == null) {
            throw new IllegalArgumentException("Attempting to select using resultClass but projections have " +
                    "already been made without a result class. The projections: " + getProjections());
        }
        TypeSafeQuerySelectionGroupInternal<T, ?> resultGroup = new TypeSafeQuerySelectionGroupImpl<>(
                SELECT_RESULT_GROUP, helper.createSelectionBuilderSpec(resultClass));
        resultGroup.setResultGroup(true);
        T proxy = helper.createTypeSafeSelectProxy(this, resultClass, resultGroup);
        return doBind(proxy, resultGroup, resultIdentifierBinder);
    }

    @Override
    public <ID, T extends ID> T select(List<T> collection, Class<T> subselectClass, ResultIdentifierBinder<ID> resultIdentifierBinder) {
        return selectCollection(subselectClass, resultIdentifierBinder, ArrayList::new);
    }

    @Override
    public <ID, T extends ID> T select(Set<T> collection, Class<T> subselectClass, ResultIdentifierBinder<ID> resultIdentifierBinder) {
        return selectCollection(subselectClass, resultIdentifierBinder, HashSet::new);
    }

    @Override
    public <ID, T extends ID> T select(Collection<T> collection, Class<T> subselectClass, ResultIdentifierBinder<ID> resultIdentifierBinder) {
        return selectCollection(subselectClass, resultIdentifierBinder, ArrayList::new);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <ID, T extends ID> T selectCollection(Class<T> subselectClass,
                                        ResultIdentifierBinder<ID> resultIdentifierBinder,
                                        Supplier<Collection<T>> collectionSupplier) {
        if (lastInvokedSelectionData == null) {
            throw new IllegalStateException("The collection was not provided by calling a getSomeCollection method on a selection proxy.");
        }
        TypeSafeQuerySelectionGroupInternal<T, ?> resultGroup = new TypeSafeQuerySelectionGroupImpl<>(
                createSelectGroupAlias(), helper.createSelectionBuilderSpec(subselectClass));
        lastInvokedSelectionData.setCollectionSupplier((Supplier) collectionSupplier);
        T proxy = getHelper().createTypeSafeSelectProxy(this, subselectClass, resultGroup);
        lastInvokedSelectionData.setSubGroup((TypeSafeQuerySelectionGroupInternal) resultGroup);
        lastInvokedSelectionData = null;
        return doBind(proxy, resultGroup, resultIdentifierBinder);
    }

    /**
     * If the binder is not null, then call it and capture all of the 'bind' calls
     * to use as identity paths.
     */
    private <ID, T extends ID> T doBind(T proxy, final TypeSafeQuerySelectionGroupInternal<T, ?> resultGroup,
            ResultIdentifierBinder<ID> resultIdentifierBinder) {
        if (resultIdentifierBinder != null) {
            resultIdentifierBinder.bind(value -> {
                if (lastInvokedSelectionData == null) {
                    throw new IllegalStateException("Bind was called without calling a getter on the result proxy.");
                }
                resultGroup.addResultIdentifierPropertyPath(lastInvokedSelectionData.getPropertyPath());
                lastInvokedSelectionData = null;
            }, proxy);
        }
        return proxy;
    }

    @Override
    public <T, V> V select(Class<V> transformedClass, T value, SelectionValueTransformer<T, V> transformer) {
        return selectValue(transformedClass, value, transformer).select();
    }

    @Override
    public <T, V> TypeSafeValue<V> selectValue(Class<V> transformedClass, T value, SelectionValueTransformer<T, V> transformer) {
        getProjections().setTransformerForNextProjection(transformer);
        if (value instanceof TypeSafeQueryProxy) {
            // invocation was not added because it is not a leaf (to support method chaining).
            invocationWasMade(((TypeSafeQueryProxy) value).getTypeSafeProxyData());
            value = null;
        }
        return new WrappedTypeSafeValue<>(this, null, transformedClass, toValue(value));
    }

    @Override
    public <VAL> VAL distinct(VAL value) {
        return distinct(toValue(value));
    }

    @Override
    public <VAL> VAL distinct(TypeSafeValue<VAL> value) {
        return hqlFunction().distinct(value).select();
    }

    @Override
    public <T> T queueValueSelected(TypeSafeValue<T> value) {
        lastSelectedValue = value;
        return helper.getDummyValue(value.getValueClass());
    }

    @Override
    public TypeSafeValue<?> dequeueSelectedValue() {
        TypeSafeValue<?> value = lastSelectedValue;
        lastSelectedValue = null;
        return value;
    }

    @Override
    public <T, SUB> SUB selectMergeValues(T resultDto, Class<SUB> subselectClass, SelectionMerger<T, SUB> merger) {
        TypeSafeQuerySelectionGroupImpl<SUB, SUB> childGroup = new TypeSafeQuerySelectionGroupImpl<>(
                createSelectGroupAlias(), helper.createSelectionBuilderSpec(subselectClass));
        TypeSafeQuerySelectionGroupInternal<?, T> parentGroup = getSelectionGroup(resultDto);
        parentGroup.putMerger(childGroup, merger);
        return getHelper().createTypeSafeSelectProxy(this, subselectClass, childGroup);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, K, V> Map<K, V> selectMergeValues(T resultDto, MapSelectionMerger<T, K, V> merger) {
        return selectMergeValues(resultDto, Map.class, (SelectionMerger) merger);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, A> SelectValue<A> selectMergeValues(T resultDto, SelectionMerger1<T, A> merger) {
        return (SelectValue) selectMergeValues(resultDto, SelectValue.class, (SelectionMerger) merger);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, A, B> SelectPair<A, B> selectMergeValues(T resultDto, SelectionMerger2<T, A, B> merger) {
        return (SelectPair) selectMergeValues(resultDto, SelectPair.class, (SelectionMerger) merger);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, A, B, C> SelectTriplet<A, B, C> selectMergeValues(T resultDto, SelectionMerger3<T, A, B, C> merger) {
        return (SelectTriplet) selectMergeValues(resultDto, SelectTriplet.class, (SelectionMerger) merger);
    }

    @Override
    public HqlQuery toHqlQuery() {
        return toHqlQuery(new HqlQueryBuilderParamsImpl());
    }

    @Override
    public HqlQuery toHqlQuery(HqlQueryBuilderParams params) {
        return super.toHqlQuery(params);
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        return toHqlQuery(params);
    }

    @Override
    public TypeSafeNameds named() {
        return namedObjects;
    }

}
