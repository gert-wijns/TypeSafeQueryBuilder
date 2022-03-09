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

import java.util.Collection;
import java.util.function.Supplier;

import be.shad.tsqb.selection.group.TypeSafeQuerySelectionGroupInternal;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = {"group", "propertyPath"})
@Getter
public class TypeSafeQuerySelectionProxyPropertyData<T> {

    private final TypeSafeQuerySelectionGroupInternal<?, ?> group;
    private final Class<T> propertyType;
    private final String propertyPath;
    private @Setter TypeSafeQuerySelectionGroupInternal<?, T> subGroup;
    private @Setter Supplier<Collection<T>> collectionSupplier;

    public String getAlias() {
        StringBuilder alias = new StringBuilder();
        if (!group.isResultGroup()) {
            alias.append(group.getId()).append("__");
        }
        return alias.append(getPropertyPath().replace(".", "_")).toString();
    }

    public boolean isCollection() {
        return collectionSupplier != null;
    }

}
