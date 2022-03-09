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

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyPropertyData;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;
import be.shad.tsqb.values.TypeSafeValue;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Couples a value to a selection alias.
 * <p>
 * The value can be anything, created by a function, a custom value, a subquery, a referenced value and so on.
 * <p>
 * The propertyName represents the alias of this value in the select clause.
 */
@Value
@RequiredArgsConstructor
public class TypeSafeValueProjection implements Copyable {
    TypeSafeValue<?> value;
    TypeSafeQuerySelectionProxyPropertyData<?> selectionData;
    SelectionValueTransformer<?, ?> transformer;
    String mapSelectionKey;

    /**
     * Copy constructor
     */
    protected TypeSafeValueProjection(CopyContext context, TypeSafeValueProjection original) {
        this.value = context.get(original.value);
        this.selectionData = context.get(original.selectionData);
        this.transformer = context.getOrOriginal(original.transformer);
        this.mapSelectionKey = original.mapSelectionKey;
    }

    public String getAlias() {
        return selectionData == null ? null: selectionData.getAlias();
    }

    public Object getPropertyPath() {
        return selectionData == null ? null: selectionData.getPropertyPath();
    }

    @Override
    public String toString() {
        HqlQueryBuilderParams params = new HqlQueryBuilderParamsImpl();
        params.setBuildingForDisplay(true);
        String valueHql = value.toHqlQueryValue(params).getHql();
        String alias = getAlias();

        StringBuilder sb = new StringBuilder();
        sb.append(valueHql);
        if (alias != null) {
            sb.append(" as ").append(getAlias());
        }
        return sb.toString();
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeValueProjection(context, this);
    }

}
