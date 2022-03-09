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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hibernate.transform.BasicTransformerAdapter;

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyPropertyData;
import be.shad.tsqb.exceptions.TsqbException;
import be.shad.tsqb.helper.BuildFn;
import be.shad.tsqb.helper.SelectionBuilderSpec;
import be.shad.tsqb.helper.ConcreteDtoClassResolver;
import be.shad.tsqb.result.ResultGroupProjection;
import be.shad.tsqb.selection.group.SelectionTreeGroup;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupInternal;

/**
 * Implementation to set values on nested select dtos.
 * Seems to be faster than the alias to bean result transformer too.
 */
@SuppressWarnings("unchecked")
public class TypeSafeQueryResultTransformer extends BasicTransformerAdapter {
    private static final long serialVersionUID = 4686800769621139636L;

    private final ConcreteDtoClassResolver concreteDtoClassResolver;
    private final SelectionTreeGroup[] treeGroups;
    private final int resultArraySize;
    private int resultIndex = -1;

    /**
     * Compares by depth (so groups without parents are first)
     * and then by alias in case multiple groups with the same depth exist.
     */
    private static final Comparator<ResultGroupProjection> SELECTION_GROUPS_COMPARATOR =
            (o1, o2) -> {
                TypeSafeQuerySelectionGroupInternal<?, ?> g1 = o1.getGroup();
                TypeSafeQuerySelectionGroupInternal<?, ?> g2 = o2.getGroup();
                if (g1.isResultGroup() != g2.isResultGroup()) {
                    return g1.isResultGroup() ? -1: 1;
                }
                int dc = Integer.compare(o1.getGroupIndex(), o2.getGroupIndex());
                if (dc != 0) {
                    return dc;
                }
                return g1.getId().compareTo(g2.getId());
            };

    public TypeSafeQueryResultTransformer(
            ConcreteDtoClassResolver concreteDtoClassResolver,
            List<ResultGroupProjection> groups) {
        this.concreteDtoClassResolver = concreteDtoClassResolver;
        try {
            // Sort all groups by depth/alias to create groups
            groups.sort(SELECTION_GROUPS_COMPARATOR);

            this.treeGroups = new SelectionTreeGroup[groups.size()];
            for(ResultGroupProjection resultGroup: groups) {
                SelectionBuilderSpec<?, ?> selectionBuilderSpec = resultGroup.getGroup().getSelectionBuilderSpec();
                Class<?> resultType = selectionBuilderSpec.getSelectionBuilderClass();
                Supplier<?> newResultProducer = selectionBuilderSpec.getBuilderSup();
                SelectionTreeGroup tree = new SelectionTreeGroup(resultType, newResultProducer, resultGroup,
                        resultGroup.getGroupIndex(), createSelectionTreeValues(resultGroup));
                if (tree.getGroup().isResultGroup()) {
                    resultIndex = resultGroup.getGroupIndex();
                }
                this.treeGroups[resultGroup.getGroupIndex()] = tree;
            }
            this.resultArraySize = treeGroups.length;
        } catch (SecurityException e) {
            throw new TsqbException(e);
        }
    }

    private List<SelectionTreeValue> createSelectionTreeValues(ResultGroupProjection group) {
        return group.getProjections().stream().map(p -> {
                TypeSafeQuerySelectionProxyPropertyData<?> selectionData = p.getData();
                String effectivePropertyPath = selectionData.getPropertyPath();
                String mapSelectionKey = null;
                if (Map.class.isAssignableFrom(selectionData.getGroup().getSelectionBuilderSpec().getSelectionBuilderClass())) {
                    mapSelectionKey = effectivePropertyPath;
                    effectivePropertyPath = "value";
                }
                return new SelectionTreeValue(p.getProjectionIndex(),
                        effectivePropertyPath, mapSelectionKey,
                        p.getTransformer());
            }).collect(Collectors.toList());
    }

    /**
     * Do nothing, result tranformation will be handled in transformList.
     */
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return tuple;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List transformList(List list) {
        if (list.isEmpty()) {
            // no values found, though then this transformList is most likely not called.
            return list;
        }

        // prepare result array and set up dataArray to contain the current
        // value objects and identity trees
        List<Object> result = new ArrayList<>(list.size());
        SelectionTreeData[] data = initDataArray();

        try {
        	Object[] singleValue = new Object[1];
            for(Object obj: list) {
            	if (!(obj instanceof Object[])) {
            		singleValue[0] = obj;
            		obj = singleValue;
            	}
                for(SelectionTreeGroup treeGroup: treeGroups) {
                    treeGroup.createFromTuple(data, (Object[]) obj);
                }
                if (!data[resultIndex].isDuplicate()) {
                    // only include main result selection if it was not duplicate.
                    result.add(data[resultIndex].getBuiltValue());
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new TsqbException(e);
        }
        return result;
    }

    private SelectionTreeData[] initDataArray() {
        SelectionTreeData[] data = new SelectionTreeData[resultArraySize];
        for(int i=0; i < resultArraySize; i++) {
            data[i] = new SelectionTreeData();
        }
        for(SelectionTreeGroup treeGroup: treeGroups) {
            BuildFn<Object, Object> buildFn = treeGroup.getGroup().getSelectionBuilderSpec().getBuildFn();
            if (buildFn != null) {
                data[treeGroup.getResultIndex()].setGroupResultTf(buildFn);
            }
        }
        return data;
    }
}
