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
package be.shad.tsqb.selection.group;

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyData;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.selection.collection.ResultIdentifierProvider;
import be.shad.tsqb.selection.parallel.SelectionMerger;

public class TypeSafeQuerySelectionGroupImpl implements TypeSafeQuerySelectionGroup, Copyable {

    private final String aliasPrefix;
    private final Class<?> resultClass;
    private final boolean resultGroup;
    private final SelectionMerger<?, ?> subselectValueMerger;
    private final ResultIdentifierProvider<?> resultIdentifierProvider;
    private final TypeSafeQuerySelectionGroup collectionGroup;
    private final String collectionPropertyPath;

    public TypeSafeQuerySelectionGroupImpl(String aliasPrefix, Class<?> resultClass,
            ResultIdentifierProvider<?> resultIdentifierProvider,
            boolean resultGroup, SelectionMerger<?, ?> subselectValueMerger,
            TypeSafeQuerySelectionProxyData collectionData) {
        this.aliasPrefix = aliasPrefix;
        this.resultClass = resultClass;
        this.resultGroup = resultGroup;
        this.resultIdentifierProvider = resultIdentifierProvider;
        this.subselectValueMerger = subselectValueMerger;
        if (collectionData != null) {
            this.collectionPropertyPath = collectionData.getEffectivePropertyPath();
            this.collectionGroup = collectionData.getGroup();
        } else {
            this.collectionPropertyPath = null;
            this.collectionGroup = null;
        }
    }

    /**
     * Copy constructor
     */
    protected TypeSafeQuerySelectionGroupImpl(CopyContext context, TypeSafeQuerySelectionGroupImpl original) {
        this.aliasPrefix = original.aliasPrefix;
        this.resultClass = original.resultClass;
        this.resultGroup = original.resultGroup;
        this.subselectValueMerger = context.getOrOriginal(original.subselectValueMerger);
        this.resultIdentifierProvider = context.get(original.resultIdentifierProvider);
        this.collectionGroup = context.get(original.collectionGroup);
        this.collectionPropertyPath = original.collectionPropertyPath;
    }

    @Override
    public TypeSafeQuerySelectionGroup getCollectionGroup() {
        return collectionGroup;
    }
    
    @Override
    public String getCollectionPropertyPath() {
        return collectionPropertyPath;
    }
    
    @Override
    public ResultIdentifierProvider<?> getResultIdentifierProvider() {
        return resultIdentifierProvider;
    }

    @Override
    public String getAliasPrefix() {
        return aliasPrefix;
    }

    @Override
    public Class<?> getResultClass() {
        return resultClass;
    }

    @Override
    public boolean isResultGroup() {
        return resultGroup;
    }

    @Override
    public SelectionMerger<?, ?> getSelectionMerger() {
        return subselectValueMerger;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeQuerySelectionGroupImpl(context, this);
    }

    @Override
    public int hashCode() {
        return aliasPrefix.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeSafeQuerySelectionGroupImpl
                && aliasPrefix.equals(((TypeSafeQuerySelectionGroupImpl) obj).aliasPrefix);
    }
}
