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

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyData;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Couples a value to a selection alias.
 * <p>
 * The value can be anything, created by a function, a custom value, a subquery, a referenced value and so on.
 * <p>
 * The propertyName represents the alias of this value in the select clause.
 */
public class TypeSafeValueProjection implements Copyable {
    private final TypeSafeValue<?> value;
    private final TypeSafeQuerySelectionProxyData selectionData;
    private final SelectionValueTransformer<?, ?> transformer;

    public TypeSafeValueProjection(TypeSafeValue<?> value, 
            TypeSafeQuerySelectionProxyData selectionData, 
            SelectionValueTransformer<?, ?> transformer) {
        this.selectionData = selectionData;
        this.transformer = transformer;
        this.value = value;
    }
    
    /**
     * Copy constructor
     */
    protected TypeSafeValueProjection(CopyContext context, TypeSafeValueProjection original) {
        this.value = context.get(original.value);
        this.selectionData = context.get(original.selectionData);
        this.transformer = context.getOrOriginal(original.transformer);
    }

    public SelectionValueTransformer<?, ?> getTransformer() {
        return transformer;
    }

    public TypeSafeValue<?> getValue() {
        return value;
    }

    public TypeSafeQuerySelectionProxyData getSelectionData() {
        return selectionData;
    }
    
    public String getAlias() {
        if (selectionData != null) {
            return selectionData.getAlias();
        }
        return null;
    }

    public Object getPropertyPath() {
        if (selectionData != null) {
            return selectionData.getPropertyPath();
        }
        return null;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeValueProjection(context, this);
    }
    
}
