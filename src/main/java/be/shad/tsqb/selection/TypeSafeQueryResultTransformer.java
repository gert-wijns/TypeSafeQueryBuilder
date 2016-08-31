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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.BasicTransformerAdapter;

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyData;
import be.shad.tsqb.helper.ConcreteDtoClassResolver;
import be.shad.tsqb.selection.group.SelectionTreeGroup;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroup;

/**
 * Implementation to set values on nested select dtos.
 * Seems to be faster than the alias to bean result transformer too.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TypeSafeQueryResultTransformer extends BasicTransformerAdapter {
    private static final long serialVersionUID = 4686800769621139636L;

    private final SelectionTreeGroup[] treeGroups;
    private final int resultArraySize;

    /**
     * Compares by depth (so groups without parents are first)
     * and then by alias in case multiple groups with the same depth exist.
     */
    private static final Comparator<TypeSafeQuerySelectionGroup> SELECTION_GROUPS_COMPARATOR =
            new Comparator<TypeSafeQuerySelectionGroup>() {
        @Override
        public int compare(TypeSafeQuerySelectionGroup o1, TypeSafeQuerySelectionGroup o2) {
            int dc = Integer.compare(depth(o1), depth(o2));
            if (dc != 0) {
                return dc;
            }
            return o1.getAliasPrefix().compareTo(o2.getAliasPrefix());
        }

        private int depth(TypeSafeQuerySelectionGroup group) {
            TypeSafeQuerySelectionGroup current = group;
            int depth = 0;
            while (current.getParent() != null) {
                current = current.getParent();
                depth++;
            }
            return depth;
        }
    };

    public TypeSafeQueryResultTransformer(
            ConcreteDtoClassResolver concreteDtoClassResolver,
            List<TypeSafeQuerySelectionProxyData> selectionDatas,
            List<SelectionValueTransformer<?, ?>> transformers) {
        try {
            // Group selection data by group (transformed into selectionTreeValues):
            int tupleValueIndex = 0;
            Iterator<SelectionValueTransformer<?, ?>> valueTransformersIt = transformers.iterator();
            Map<TypeSafeQuerySelectionGroup, List<SelectionTreeValue>> dataByGroup = new HashMap<>();
            for(TypeSafeQuerySelectionProxyData selectionData: selectionDatas) {
                List<SelectionTreeValue> groupData = dataByGroup.get(selectionData.getGroup());
                if (groupData == null) {
                    groupData = new LinkedList<>();
                    dataByGroup.put(selectionData.getGroup(), groupData);
                }
                String effectivePropertyPath = selectionData.getEffectivePropertyPath();
                String mapSelectionKey = null;
                if (Map.class.isAssignableFrom(selectionData.getParent().getPropertyType())) {
                    String parentEffectivePath = selectionData.getParent().getEffectivePropertyPath();
                    if (parentEffectivePath == null) {
                        mapSelectionKey = effectivePropertyPath;
                        effectivePropertyPath = "value";
                    } else {
                        mapSelectionKey = effectivePropertyPath.substring(parentEffectivePath.length()+1);
                        effectivePropertyPath = parentEffectivePath + ".value";
                    }
                }
                groupData.add(new SelectionTreeValue(tupleValueIndex++,
                        effectivePropertyPath, mapSelectionKey,
                        valueTransformersIt.next()));
            }

            // Sort all groups by depth/alias to create groups
            List<TypeSafeQuerySelectionGroup> selectionGroups = new ArrayList<>(dataByGroup.keySet());
            Collections.sort(selectionGroups, SELECTION_GROUPS_COMPARATOR);
            for(TypeSafeQuerySelectionGroup selectionGroup: selectionGroups) {
                TypeSafeQuerySelectionGroup parentGroup = selectionGroup.getParent();
                if (parentGroup!= null && ! dataByGroup.containsKey(parentGroup)) {
                    throw new IllegalStateException("Selecting [" + parentGroup.getResultClass()
                            + "#" + selectionGroup.getCollectionPropertyPath() + "] is not configured correctly. "
                            + "At least identity paths [" + parentGroup.getResultIdentifierPropertyPaths()
                            + "] should be selected for [" + parentGroup.getResultClass() + "]");
                }
            }

            int parentResultIndex = -1;
            int treeGroupIdx = 1;
            this.treeGroups = new SelectionTreeGroup[dataByGroup.size()];
            Map<TypeSafeQuerySelectionGroup, SelectionTreeGroup> treeGroupsMap = new HashMap<>();
            for(TypeSafeQuerySelectionGroup group: selectionGroups) {
                // Create group (with any parent it may have) and save it for treeGroup iteration
                SelectionTreeGroup tree = new SelectionTreeGroup(concreteDtoClassResolver,
                        group, dataByGroup.get(group), treeGroupsMap.get(group.getParent()));
                // NOTE: A treeGroup may have child SelectionTrees for embedded/composite objects,
                //       this means the treeGroups is potentially smaller than the result array,
                //       because the treeGroups only contains explicitly selected dtos.
                parentResultIndex = tree.assignResultIndexes(parentResultIndex);
                if (tree.getGroup().isResultGroup()) {
                    this.treeGroups[0] = tree;
                } else {
                    this.treeGroups[treeGroupIdx++] = tree;
                }
                treeGroupsMap.put(group, tree);
            }
            this.resultArraySize = parentResultIndex + 1;
        } catch (SecurityException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Do nothing, result tranformation will be handled in transformList.
     */
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return tuple;
    }

    @Override
    public List transformList(List list) {
        if (list.isEmpty()) {
            // no values found, though then this transformList is most likely not called.
            return list;
        } else if (!(list.iterator().next() instanceof Object[])) {
            // only one value was selected, nothing needs to be done
            return list;
        }

        // prepare result array and set up dataArray to contain the current
        // value objects and identity trees
        List result = new ArrayList(list.size());
        SelectionTreeData[] data = new SelectionTreeData[resultArraySize];
        for(int i=0; i < resultArraySize; i++) {
            data[i] = new SelectionTreeData();
        }

        try {
            for(Object obj: list) {
                for(SelectionTreeGroup treeGroup: treeGroups) {
                    treeGroup.createFromTuple(data, (Object[]) obj);
                }
                if (!data[0].isDuplicate()) {
                    // only include main result selection if it was not duplicate.
                    result.add(data[0].getCurrentValue());
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
