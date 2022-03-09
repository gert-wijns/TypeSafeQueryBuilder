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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyPropertyData;
import be.shad.tsqb.helper.SelectionBuilderSpec;
import be.shad.tsqb.proxy.TypeSafeQuerySelectionProxy;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.selection.parallel.SelectionMerger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@RequiredArgsConstructor
public class TypeSafeQuerySelectionGroupImpl<SB, SR> implements TypeSafeQuerySelectionGroupInternal<SB, SR>, Copyable {
    private final @Getter String id;
    private final @Getter SelectionBuilderSpec<SB, SR> selectionBuilderSpec;

    private @Getter @Setter TypeSafeQuerySelectionProxy<SB> proxy;
    private @Getter @Setter boolean resultGroup;

    private final @Getter Map<TypeSafeQuerySelectionGroupInternal<?, ?>, SelectionMerger<SR, ?>> mergers = new HashMap<>();
    private final @Getter Set<String> resultIdentifierPropertyPaths = new HashSet<>();
    private final LinkedHashMap<String, TypeSafeQuerySelectionProxyPropertyData<?>> children = new LinkedHashMap<>();

    /**
     * Copy constructor
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected TypeSafeQuerySelectionGroupImpl(CopyContext context, TypeSafeQuerySelectionGroupImpl<SB, SR> original) {
        this.id = original.id;
        this.selectionBuilderSpec = original.selectionBuilderSpec;
        this.resultGroup = original.resultGroup;
        this.resultIdentifierPropertyPaths.addAll(original.resultIdentifierPropertyPaths);
        original.mergers.forEach((sub, merger) -> putMerger((TypeSafeQuerySelectionGroupInternal) context.get(sub), merger));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeSafeQuerySelectionProxyPropertyData<T> getChild(String propertyName) {
        return (TypeSafeQuerySelectionProxyPropertyData<T>) children.get(propertyName);
    }

    @Override
    public void putChild(TypeSafeQuerySelectionProxyPropertyData<?> child) {
        children.put(child.getPropertyPath(), child);
    }

    @Override
    public Collection<TypeSafeQuerySelectionProxyPropertyData<?>> getChildren() {
        return children.values();
    }

    @Override
    public <SUBB, SUBR> void putMerger(TypeSafeQuerySelectionGroupInternal<SUBB, SUBR> sub,
                                      SelectionMerger<SR, SUBR> merger) {
        mergers.put(sub, merger);
    }

    @Override
    public void addResultIdentifierPropertyPath(String resultIdentifierPropertyPath) {
        this.resultIdentifierPropertyPaths.add(resultIdentifierPropertyPath);
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeQuerySelectionGroupImpl<>(context, this);
    }
}
