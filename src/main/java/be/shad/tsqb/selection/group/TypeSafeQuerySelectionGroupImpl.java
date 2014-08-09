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

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyData;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.selection.parallel.SelectionMerger;

public class TypeSafeQuerySelectionGroupImpl implements TypeSafeQuerySelectionGroup, Copyable {

    private final String aliasPrefix;
    private final Class<?> resultClass;
    private final boolean resultGroup;
    private final SelectionMerger<?, ?> subselectValueMerger;
    private final TypeSafeQuerySelectionGroup parent;
    private final String collectionPropertyPath;
    private final List<String> resultIdentifierPropertyPaths;

    public TypeSafeQuerySelectionGroupImpl(String aliasPrefix, Class<?> resultClass,
            boolean resultGroup, SelectionMerger<?, ?> subselectValueMerger,
            TypeSafeQuerySelectionProxyData parent) {
        this.aliasPrefix = aliasPrefix;
        this.resultClass = resultClass;
        this.resultGroup = resultGroup;
        this.subselectValueMerger = subselectValueMerger;
        this.resultIdentifierPropertyPaths = new LinkedList<>();
        if (parent != null) {
            if (subselectValueMerger == null) {
                this.collectionPropertyPath = parent.getEffectivePropertyPath();
            } else {
                this.collectionPropertyPath = null;
            }
            this.parent = parent.getGroup();
        } else {
            this.collectionPropertyPath = null;
            this.parent = null;
        }
    }

    /**
     * Copy constructor
     */
    protected TypeSafeQuerySelectionGroupImpl(CopyContext context, TypeSafeQuerySelectionGroupImpl original) {
        this.aliasPrefix = original.aliasPrefix;
        this.resultClass = original.resultClass;
        this.resultGroup = original.resultGroup;
        this.resultIdentifierPropertyPaths = new LinkedList<>(original.resultIdentifierPropertyPaths);
        this.subselectValueMerger = context.getOrOriginal(original.subselectValueMerger);
        this.collectionPropertyPath = original.collectionPropertyPath;
        this.parent = context.get(original.parent);
    }

    @Override
    public List<String> getResultIdentifierPropertyPaths() {
        return resultIdentifierPropertyPaths;
    }
    
    @Override
    public void addResultIdentifierPropertyPath(String resultIdentifierPropertyPath) {
        this.resultIdentifierPropertyPaths.add(resultIdentifierPropertyPath);
    } 
    
    @Override
    public TypeSafeQuerySelectionGroup getParent() {
        return parent;
    }
    
    @Override
    public String getCollectionPropertyPath() {
        return collectionPropertyPath;
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
    
    @Override
    public String toString() {
        return aliasPrefix + " [" + parent + "]";
    }
}
