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

import java.util.Set;

import be.shad.tsqb.selection.parallel.SelectionMerger;



public interface TypeSafeQuerySelectionGroup {

    /**
     * The alias prefix to use for this group, this is done so
     * the same propertyPath can appear in multiple groups.
     */
    String getAliasPrefix();

    /**
     * The type of the dto to be created when values are
     * selected for this group.
     */
    Class<?> getResultClass();

    /**
     * The result group will be the group of which the values
     * will appear in the returned list after querying
     */
    boolean isResultGroup();

    /**
     * @return when this group is not the result group, a merger should
     *         merge the result value of this group into the resultDto.
     */
    SelectionMerger<?, ?> getSelectionMerger();

    /**
     * The properties of this groups' result dto to take into
     * account when checking if the value of a result row
     * represents the same result dto as a previous row.
     */
    Set<String> getResultIdentifierPropertyPaths();

    /**
     * Adds a property which should be used to determine the equality of result dtos.
     */
    void addResultIdentifierPropertyPath(String resultIdentifierPropertyPath);

    /**
     * The parent group, when set either the collection property path or the selection merger must be present.
     * <p>
     * In case the collection property path is set, this group represents a
     * subselected result to be added to a collection on the parent.
     * <p>
     * In case the selection merger is set, this group represents an
     * extra result which will be merged into the parent.
     */
    TypeSafeQuerySelectionGroup getParent();

    /**
     * The property path of the collection on the parent to which the result
     * should be added during query result transformation.
     */
    String getCollectionPropertyPath();

}
