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
package be.shad.tsqb.selection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQuerySelectionProxyData;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.IsMaybeDistinct;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Container for all projections of a query.
 * Projections can be added using the {@link #project(Object, String)}.
 * This method should not be called from outside the query builder,
 * but it would be allowed if needed.
 */
public class TypeSafeQueryProjections implements HqlQueryBuilder {
    private final TypeSafeQueryInternal query;
    private final LinkedList<TypeSafeValueProjection> projections = new LinkedList<>();
    private SelectionValueTransformer<?, ?> transformerForNextProjection;
    private Class<?> resultClass;

    public TypeSafeQueryProjections(TypeSafeQueryInternal query) {
        this.query = query;
    }

    public void setResultClass(Class<?> resultClass) {
        this.resultClass = resultClass;
    }
    
    public Class<?> getResultClass() {
        return resultClass;
    }
    
    public LinkedList<TypeSafeValueProjection> getProjections() {
        return projections;
    }

    /**
     * Adds the projection to the projections list.
     * If it is a distinct value, then it is added at the front
     * 
     * @throws IllegalArgumentException when a distinct value was already added.
     */
    public void addProjection(TypeSafeValueProjection projection) {
        if(isDistinct(projection)) {
            if (!projections.isEmpty() && isDistinct(projections.getFirst())) {
                throw new IllegalArgumentException(String.format("Attempting to add a second distinct projection. "
                        + "Existing: %s, New: %s", projections.getFirst().getValue(), projection.getValue()));
            }
            projections.addFirst(projection);
        } else {
            projections.add(projection);
        }
    }
    
    private boolean isDistinct(TypeSafeValueProjection projection) {
        return projection.getValue() instanceof IsMaybeDistinct && ((IsMaybeDistinct)projection.getValue()).isDistinct();
    }
    
    /**
     * Sets the given transformer to be put on the next created projection.
     * After it is set on a projection, the transformerForNextProjection is reset.
     */
    public <T, V> void setTransformerForNextProjection(SelectionValueTransformer<T, V> transformerForNextProjection) {
        this.transformerForNextProjection = transformerForNextProjection;
    }
    
    /**
     * First checks if a TypeSafeQueryValue.select() was called. 
     * This will take precendence over everything else.
     * <p>
     * Converts the input value to a type safe value if it isn't one yet when no invocations were made.
     * Covnerts the invocation data to a type safe value otherwise.
     */
    public void project(Object select, TypeSafeQuerySelectionProxyData property) {
        TypeSafeValue<?> value = query.getRootQuery().dequeueSelectedValue();
        if( value != null ) {
            projectBySelectedValue(value, property);
            return;
        }

        // No subquery was selected, check the queue or direct selections:
        projectInvocationQueueValue(select, property);
    }

    /**
     * 
     */
    private void projectInvocationQueueValue(Object select, TypeSafeQuerySelectionProxyData property) {
        TypeSafeValue<?> value = null;
        
        List<TypeSafeQueryProxyData> invocations = query.dequeueInvocations();
        if( invocations.isEmpty() ) {
            if( select instanceof TypeSafeValue<?> ) {
                // any value selection (check if referenced)
                value = (TypeSafeValue<?>) select;
            } else if( select instanceof TypeSafeQueryProxy ) {
                // entity selection
                value = new ReferenceTypeSafeValue<>(query, ((TypeSafeQueryProxy) select).getTypeSafeProxyData());
            } else {
                // direct value selection
                value = new DirectTypeSafeValue<>(query, select);
            }
        } else {
            // value selection by proxy getter:
            value = new ReferenceTypeSafeValue<>(query, invocations.get(0));
        }
        
        query.validateInScope(value, null);
        addProjection(new TypeSafeValueProjection(value, property, transformerForNextProjection));
        transformerForNextProjection = null;
    }

    /**
     * 
     */
    private void projectBySelectedValue(TypeSafeValue<?> value, 
            TypeSafeQuerySelectionProxyData property) {
        query.validateInScope(value, null);
        TypeSafeValueProjection projection = new TypeSafeValueProjection(
                value, property, transformerForNextProjection);
        transformerForNextProjection = null;
        addProjection(projection);
    }

    @Override
    public void appendTo(HqlQuery query) {
        List<TypeSafeQuerySelectionProxyData> selectionDatas = new ArrayList<>(projections.size());
        List<SelectionValueTransformer<?, ?>> transformers = new ArrayList<>(projections.size());
        boolean hasTransformer = false;
        for(TypeSafeValueProjection projection: projections) {
            HqlQueryValue val = projection.getValue().toHqlQueryValue();
            if( projection.getValue() instanceof DirectTypeSafeValue<?> ) {
                val = this.query.getHelper().replaceParamsWithLiterals(val);
            }
            String alias = "";
            TypeSafeQuerySelectionProxyData selectionData = projection.getSelectionData();
            if( selectionData != null ) {
                selectionDatas.add(selectionData);
                alias = " as " + selectionData.getAlias();
            }
            transformers.add(projection.getTransformer());
            hasTransformer = hasTransformer || projection.getTransformer() != null;
            query.appendSelect(val.getHql() + alias);
            query.addParams(val.getParams());
        }
        if( !selectionDatas.isEmpty() ) {
            query.setResultTransformer(new TypeSafeQueryResultTransformer(selectionDatas, transformers));
        } else if( hasTransformer ) {
            query.setResultTransformer(new WithoutAliasesQueryResultTransformer(transformers));
        }
    }

}
