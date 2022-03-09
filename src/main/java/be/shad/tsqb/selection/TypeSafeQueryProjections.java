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

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.ResultTransformer;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQuerySelectionProxyPropertyData;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.restrictions.Restriction;
import be.shad.tsqb.restrictions.RestrictionHolder;
import be.shad.tsqb.result.ResultGroupProjection;
import be.shad.tsqb.result.ResultMergeProjection;
import be.shad.tsqb.result.ResultProjection;
import be.shad.tsqb.result.ResultSubProjection;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupInternal;
import be.shad.tsqb.values.CustomTypeSafeValue;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.IsMaybeDistinct;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.RestrictionTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Container for all projections of a query.
 * Projections can be added using the {@link #project(Object, TypeSafeQuerySelectionProxyPropertyData)}.
 * This method should not be called from outside the query builder,
 * but it would be allowed if needed.
 */
public class TypeSafeQueryProjections implements HqlQueryBuilder {
    private final TypeSafeQueryInternal query;
    private final Deque<TypeSafeValueProjection> projections = new LinkedList<>();
    private SelectionValueTransformer<?, ?> transformerForNextProjection;
    private String mapSelectionKeyForNextProjection;
    private Class<?> resultClass;
    private boolean selectingIntoDto;
    private boolean includeAliases;
    private boolean hasTransformer;

    public TypeSafeQueryProjections(TypeSafeQueryInternal query) {
        this.query = query;
    }

    public void replay(CopyContext context, TypeSafeQueryProjections original) {
        this.transformerForNextProjection = context.getOrOriginal(original.transformerForNextProjection);
        this.mapSelectionKeyForNextProjection = original.mapSelectionKeyForNextProjection;
        this.resultClass = original.resultClass;
        this.includeAliases = original.includeAliases;
        for(TypeSafeValueProjection projection: original.projections) {
            projections.add(context.get(projection));
        }
    }

    public void setResultClass(Class<?> resultClass) {
        this.resultClass = resultClass;
        this.selectingIntoDto = resultClass != null;
    }

    public Class<?> getResultClass() {
        return resultClass;
    }

    public void setIncludeAliases(boolean includeAliases) {
        this.includeAliases = includeAliases;
    }

    public Deque<TypeSafeValueProjection> getProjections() {
        return projections;
    }

    /**
     * Adds the projection to the projections list.
     * If it is a distinct value, then it is added at the front
     *
     * @throws IllegalArgumentException when a distinct value was already added.
     */
    public void addProjection(TypeSafeValueProjection projection) {
        TypeSafeQuerySelectionProxyPropertyData<?> selectionData = projection.getSelectionData();
        if (projections.isEmpty()) {
            selectingIntoDto = selectionData != null;
        }
        if (selectionData != null && !selectingIntoDto) {
            throw new IllegalArgumentException(String.format("Values have already been selected "
                    + "without a resultClass. Current projections %s, added %s",
                    projections, projection));
        } else if (selectionData == null && selectingIntoDto) {
            throw new IllegalArgumentException(String.format("Values have already been selected "
                    + "into a resultClass. Current projections %s, added %s",
                    projections, projection));
        }
        hasTransformer |= projection.getTransformer() != null;
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
     * Sets the given mapSelectionKey to be put on the next created projection.
     * After it is set on a projection, the mapSelectionKeyForNextProjection is reset.
     */
    public void setMapSelectionKeyForNextProjection(String mapSelectionKeyForNextProjection) {
        this.mapSelectionKeyForNextProjection = mapSelectionKeyForNextProjection;
    }

    /**
     * First checks if a TypeSafeQueryValue.select() was called.
     * This will take precendence over everything else.
     * <p>
     * Converts the input value to a type safe value if it isn't one yet when no invocations were made.
     * Covnerts the invocation data to a type safe value otherwise.
     */
    public TypeSafeValue<?> project(Object select, TypeSafeQuerySelectionProxyPropertyData<?> property) {
        TypeSafeValue<?> value = query.getRootQuery().dequeueSelectedValue();
        if (value != null) {
            projectBySelectedValue(value, property);
            return value;
        }

        // No subquery was selected, check the queue or direct selections:
        return projectInvocationQueueValue(select, property);
    }

    /**
     *
     */
    private TypeSafeValue<?> projectInvocationQueueValue(Object select, TypeSafeQuerySelectionProxyPropertyData<?> property) {
        TypeSafeValue<?> value;

        List<TypeSafeQueryProxyData> invocations = query.dequeueInvocations();
        if (invocations.isEmpty()) {
            if (select instanceof TypeSafeValue<?>) {
                // any value selection (check if referenced)
                value = (TypeSafeValue<?>) select;
            } else if (select instanceof TypeSafeQueryProxy) {
                // entity selection
                value = new ReferenceTypeSafeValue<>(query, ((TypeSafeQueryProxy) select).getTypeSafeProxyData());
            } else if (select instanceof Restriction) {
                // select the value as a case when (restriction is true) ...
                value = new RestrictionTypeSafeValue(query, (Restriction) select);
            } else if (select instanceof RestrictionHolder) {
                // select the value as a case when (restriction is true) ...
                value = new RestrictionTypeSafeValue(query, ((RestrictionHolder) select).getRestriction());
            } else if (select != null) {
                // direct value selection
                value = new DirectTypeSafeValue<>(query, select);
            } else {
                value = null;
            }
        } else {
            // value selection by proxy getter:
            value = new ReferenceTypeSafeValue<>(query, invocations.get(0));
        }

        if (value != null) {
            query.validateInScope(value, null);
            addProjection(new TypeSafeValueProjection(value,
                    property, transformerForNextProjection,
                    mapSelectionKeyForNextProjection));
        }
        transformerForNextProjection = null;
        mapSelectionKeyForNextProjection = null;
        return value;
    }

    /**
     *
     */
    private void projectBySelectedValue(TypeSafeValue<?> value,
            TypeSafeQuerySelectionProxyPropertyData<?> property) {
        query.validateInScope(value, null);
        TypeSafeValueProjection projection = new TypeSafeValueProjection(
                value, property, transformerForNextProjection,
                mapSelectionKeyForNextProjection);
        transformerForNextProjection = null;
        mapSelectionKeyForNextProjection = null;
        addProjection(projection);
    }

    @Override
    public void appendTo(HqlQuery query, HqlQueryBuilderParams params) {
        if (params.isSelectingCount()) {
            query.appendSelect("count(*)");
            return;
        }
        for(TypeSafeValueProjection projection: projections) {
            TypeSafeQuerySelectionProxyPropertyData<?> selectionData = projection.getSelectionData();
            String alias = "";
            if (selectionData != null && (params.isBuildingForDisplay() || includeAliases)) {
                alias = " as " + selectionData.getAlias();
            }
            HqlQueryValue val = getHqlQueryValue(projection, params);
            query.appendSelect(val.getHql() + alias);
            query.addParams(val.getParams());
        }
        if (params.isBuildingForDisplay()) {
            // don't bother setting the result transformer, we're only interested in the hql string and params
            return;
        }
        if (selectingIntoDto) {
            query.setResultTransformer(createSelectingIntoDtoTransformer());
        } else if (hasTransformer) {
            query.setResultTransformer(createSelectingWithoutDtoTransformer());
        }
    }

    private ResultTransformer createSelectingWithoutDtoTransformer() {
        return new WithoutAliasesQueryResultTransformer(projections.stream()
                .map(TypeSafeValueProjection::getTransformer)
                .collect(toList()));
    }

    private ResultTransformer createSelectingIntoDtoTransformer() {
        List<TypeSafeQuerySelectionProxyPropertyData<?>> selectionDatas = new ArrayList<>(query.getDataTree().getSelectionDatas());
        Map<TypeSafeQuerySelectionProxyPropertyData<?>, Integer> depths = new HashMap<>();
        selectionDatas.forEach(d -> fillDepthMap(d.getGroup(), 0, depths));

        int[] groupIndex = {0};
        List<ResultGroupProjection> groups = selectionDatas.stream()
                .filter(s -> s.getPropertyPath() != null)
                .sorted(comparingInt(depths::get).reversed())
                .map(TypeSafeQuerySelectionProxyPropertyData::getGroup)
                .distinct()
                .map(group -> ResultGroupProjection.builder()
                        .mergeProjections(new ArrayList<>())
                        .subProjections(new ArrayList<>())
                        .projections(new ArrayList<>())
                        .groupIndex(groupIndex[0]++)
                        .group(group)
                        .build())
                .collect(toList());

        Map<String, ResultGroupProjection> projectionGroups = groups.stream()
                .collect(toMap(g -> g.getGroup().getId(), g -> g));

        int projectionIndex = 0;
        for (TypeSafeValueProjection p: projections) {
            String groupKey = p.getSelectionData().getGroup().getId();
            ResultProjection resultProjection = ResultProjection.builder()
                    .transformer(p.getTransformer())
                    .data(p.getSelectionData())
                    .projectionIndex(projectionIndex++)
                    .build();
            projectionGroups.get(groupKey).getProjections().add(resultProjection);
        }

        for (ResultGroupProjection pg: projectionGroups.values()) {
            for (TypeSafeQuerySelectionProxyPropertyData<?> child: pg.getGroup().getChildren()) {
                if (child.getSubGroup() != null) {
                    int childGroupIndex = projectionGroups.get(child.getSubGroup().getId()).getGroupIndex();
                    @SuppressWarnings("unchecked")
                    ResultSubProjection<?> subProjection = ResultSubProjection.builder()
                            .subGroupIndex(childGroupIndex)
                            .data((TypeSafeQuerySelectionProxyPropertyData<Object>) child)
                            .build();
                    pg.getSubProjections().add(subProjection);
                }
            }
            pg.getGroup().getMergers().forEach((sub, merger) ->
                pg.getMergeProjections().add(ResultMergeProjection.builder()
                        .subGroupIndex(projectionGroups.get(sub.getId()).getGroupIndex())
                        .merger(merger)
                        .build())
            );
        }

        return new TypeSafeQueryResultTransformer(query.getHelper().getConcreteDtoClassResolver(), groups);
    }

    private void fillDepthMap(TypeSafeQuerySelectionGroupInternal<?, ?> group, int depth,
                              Map<TypeSafeQuerySelectionProxyPropertyData<?>, Integer> depths) {
        for (TypeSafeQuerySelectionProxyPropertyData<?> child: group.getChildren()) {
            Integer prevDepth = depths.getOrDefault(child, -1);
            if (prevDepth < depth) {
                depths.put(child, depth);
                if (child.getSubGroup() != null) {
                    fillDepthMap(child.getSubGroup(), depth + 1, depths);
                }
            }
        }
        group.getMergers().keySet().forEach(sub -> fillDepthMap(sub, depth + 1, depths));
    }

    private HqlQueryValue getHqlQueryValue(TypeSafeValueProjection projection, HqlQueryBuilderParams params) {
        if (projection.getValue() instanceof DirectTypeSafeValue<?>) {
            boolean previous = params.setRequiresLiterals(true);
            HqlQueryValue val = projection.getValue().toHqlQueryValue(params);
            params.setRequiresLiterals(previous);
            return val;
        } else {
            return projection.getValue().toHqlQueryValue(params);
        }
    }

    /**
     * @return the value which was bound to the propertyPath of the selection dto.
     */
    public TypeSafeValue<?> getTypeSafeValue(String propertyPath, boolean buildingForDisplay) {
        for(TypeSafeValueProjection projection: getProjections()) {
            if (propertyPath.equals(projection.getPropertyPath())) {
                return projection.getValue();
            }
        }
        if (buildingForDisplay) {
            return new CustomTypeSafeValue<>(query, Object.class, "projection[" + propertyPath + "]");
        } else {
            throw new IllegalArgumentException("No value was bound for propertPath: " + propertyPath);
        }
    }

}
