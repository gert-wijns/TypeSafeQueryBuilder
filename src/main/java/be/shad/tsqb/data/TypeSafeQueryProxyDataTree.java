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
package be.shad.tsqb.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.joins.TypeSafeQueryJoin;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQueryProxyType;
import be.shad.tsqb.proxy.TypeSafeQuerySelectionProxy;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.restrictions.WhereRestrictions;
import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroup;
import be.shad.tsqb.values.HqlQueryBuilderParams;

/**
 * Contains the proxy data, the from and the joined entities data known in the query.
 */
public class TypeSafeQueryProxyDataTree implements HqlQueryBuilder {
    private final List<TypeSafeQueryFrom> froms = new ArrayList<>();
    private final Map<TypeSafeQueryProxyData, TypeSafeQueryJoin<?>> joins = new HashMap<>();
    private final Set<TypeSafeQueryProxyData> queryData = new LinkedHashSet<>();
    private final List<TypeSafeQuerySelectionProxyData> selectionData = new LinkedList<>();
    private final TypeSafeQueryHelper helper;
    private final TypeSafeQueryInternal query;

    public TypeSafeQueryProxyDataTree(TypeSafeQueryHelper helper, TypeSafeQueryInternal query) {
        this.helper = helper;
        this.query = query;
    }

    /**
     * Replays the original data tree into this datatree.
     * This data tree should still be empty when replay is called.
     */
    public void replay(CopyContext context, TypeSafeQueryProxyDataTree original) {
        if (!queryData.isEmpty()) {
            throw new IllegalStateException("Replaying on a non-empty tree is not supported.");
        }
        // query data contains the history of created proxy data,
        // so replaying this results in the same data tree.
        for(TypeSafeQueryProxyData originalData: original.queryData) {
            TypeSafeQueryProxyData copyData;
            if (originalData.getParent() == null) {
                // is a from:
                copyData = ((TypeSafeQueryProxy) helper.createTypeSafeFromProxy(query,
                        originalData.getPropertyType())).getTypeSafeProxyData();
            } else {
                // is a join or select, both call the 'join' method:
                TypeSafeQueryProxyData parent = context.get(originalData.getParent());
                copyData = helper.createTypeSafeJoinProxy(query, parent,
                        originalData.getPropertyPath(),
                        originalData.getPropertyType());
                // alias and jointype may have been changed:
                copyData.setJoinType(originalData.getJoinType());
            }
            copyData.setCustomAlias(originalData.getCustomAlias());
            context.put(originalData, copyData);
            if (originalData.getProxy() != null) {
                context.put(originalData.getProxy(), copyData.getProxy());
            }
        }
        for(TypeSafeQuerySelectionProxyData originalData: original.selectionData) {
            TypeSafeQuerySelectionProxyData copyData = null;
            if (originalData.getParent() == null) {
                copyData = ((TypeSafeQuerySelectionProxy) helper.createTypeSafeSelectProxy(
                        query.getRootQuery(), originalData.getPropertyType(),
                        context.get(originalData.getGroup()))).getTypeSafeQuerySelectionProxyData();
                context.put(originalData.getProxy(), copyData.getProxy());
            } else {
                copyData = helper.createTypeSafeSelectSubProxy(query.getRootQuery(),
                        context.get(originalData.getParent()),
                        originalData.getPropertyPath(),
                        originalData.getPropertyType(),
                        originalData.getProxy() != null);
            }
            context.put(originalData, copyData);
        }
    }

    public <T> WhereRestrictions getJoinRestrictions(TypeSafeQueryProxyData data) {
        return (WhereRestrictions) joins.get(data);
    }

    /**
     * Create the selection proxy data, store it in the list of created datas and return.
     */
    public TypeSafeQuerySelectionProxyData createSelectionData(TypeSafeQuerySelectionProxyData parent,
            String propertyPath, Class<?> propertyType, TypeSafeQuerySelectionGroup group,
            TypeSafeQuerySelectionProxy proxy) {
        TypeSafeQuerySelectionProxyData selectionProxyData = new TypeSafeQuerySelectionProxyData(
                parent, propertyPath, propertyType, group, proxy);
        selectionData.add(selectionProxyData);
        return selectionProxyData;
    }

    /**
     * Create proxy data for the given proxy.
     * <p>
     * This data is added as child of the parent and is added to the root
     * when the parent is null. When data is part of the root, the data
     * is used to construct a FROM part of the query.
     */
    public TypeSafeQueryProxyData createData(TypeSafeQueryProxyData parent,
            String propertyName, Class<?> propertyType, TypeSafeQueryProxyType proxyType,
            String identifierPath, TypeSafeQueryProxy proxy) {
        TypeSafeQueryProxyData child = new TypeSafeQueryProxyData(parent, propertyName,
                propertyType, proxyType, proxy, identifierPath, query.createEntityAlias());
        if (propertyName != null) {
            child.setJoinType(JoinType.Default); // default join type
        }
        if (parent == null) {
            froms.add(new TypeSafeQueryFrom(helper, child));
        } else {
            parent.putChild(child);
            TypeSafeQueryProxyData root = parent;
            while( root.getParent() != null) {
                root = root.getParent();
            }
            for(TypeSafeQueryFrom from: froms) {
                if (from.getRoot().equals(root)) {
                    TypeSafeQueryJoin<Object> join = new TypeSafeQueryJoin<>(query, child);
                    joins.put(child, join);
                    from.addJoin(join);
                }
            }
        }
        queryData.add(child);
        return child;
    }

    /**
     * Create proxy data for a non-hibernate entity type which can be used
     * as leaf in restrictions.
     */
    public TypeSafeQueryProxyData createData(TypeSafeQueryProxyData parent,
            String propertyName, Class<?> propertyType) {
        TypeSafeQueryProxyData child = new TypeSafeQueryProxyData(
                parent, propertyName, propertyType);
        if (parent == null) {
            throw new IllegalArgumentException("");
        }
        parent.putChild(child);
        queryData.add(child);
        return child;
    }

    /**
     * Check if this dataTree contains the data.
     *
     * @param join optional parameter, in case of creating a where clause in a join
     *             then the join data must be the data or must be added after data.
     */
    public boolean isInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join) {
        if (join == null) {
            return queryData.contains(data);
        } else if (!queryData.contains(data)) {
            return false;
        }

        // if the property is not an entity, then use the parent instead,
        // the parent should be part of one of the joins in the froms.
        TypeSafeQueryProxyData entityData = data;
        while( !entityData.getProxyType().isEntity()) {
            entityData = entityData.getParent();
        }
        if (entityData.equals(join)) {
            return true;
        }

        TypeSafeQueryProxyData dataRoot = entityData;
        while( dataRoot.getParent() != null) {
            dataRoot = dataRoot.getParent();
        }
        if (join.equals(dataRoot)) {
            return true;
        }

        // check if data was joined before join.
        for(TypeSafeQueryFrom from: froms) {
            if (dataRoot.equals(from.getRoot())) {
                if (from.getRoot().equals(entityData)) {
                    return true;
                }
                for(TypeSafeQueryJoin<?> joined: from.getJoins()) {
                    if (joined.getData().equals(entityData)) {
                        return true;
                    }
                    if (joined.getData().equals(join)) {
                        return false; // found join before data
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void appendTo(HqlQuery query, HqlQueryBuilderParams params) {
        for(TypeSafeQueryFrom from: froms) {
            from.appendTo(query, params);
        }
    }

}
