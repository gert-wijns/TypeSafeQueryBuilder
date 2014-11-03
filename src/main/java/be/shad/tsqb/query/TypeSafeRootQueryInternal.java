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
package be.shad.tsqb.query;

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyData;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Additional methods added to the TypeSafeRootQuery for internal use.
 * <p>
 * They are omitted from the TypeSafeRootQuery interface so it is not cluttered
 * with methods which hold no meaning to the users of the library.
 */
public interface TypeSafeRootQueryInternal extends TypeSafeRootQuery, TypeSafeQueryInternal {

    /**
     * Sets the queued projection path back to null and returns the value
     * if there was any.
     */
    String dequeueInvokedProjectionPath();

    /**
     * Queues the invoked projection path when this projection path was
     * that of a basic type. This projection path is used later during order by
     * and is reset when an invocation is queued to remove ambiguity.
     */
    void queueInvokedSelection(TypeSafeQuerySelectionProxyData lastInvokedSelection);

    /**
     * Clears the last invoked selection so no selection is still pending
     * when a setter is called.
     */
    void clearInvokedSelection();

    /**
     * Queues the value as a selected value, this value will
     * take precedence over everything else when a proxy call to
     * a resultDto setter handled.
     */
    <T> T queueValueSelected(TypeSafeValue<T> selected);

    /**
     * Sets the queued value back to null and returns the value
     * if there was any.
     */
    TypeSafeValue<?> dequeueSelectedValue();

    /**
     * Unique group name to be used when values are selected
     * into a grouped collection dto.
     */
    String createSelectGroupAlias();

}
