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
import java.util.Map;
import java.util.Set;

import be.shad.tsqb.data.TypeSafeQuerySelectionProxyPropertyData;
import be.shad.tsqb.helper.SelectionBuilderSpec;
import be.shad.tsqb.proxy.TypeSafeQuerySelectionProxy;
import be.shad.tsqb.selection.parallel.SelectionMerger;

public interface TypeSafeQuerySelectionGroupInternal<SB, SR> {

    /**
     * The alias prefix to use for this group, this is done so
     * the same propertyPath can appear in multiple groups.
     */
    String getId();

    /**
     * Get the builder spec containing info on which selection builder/how to build.
     */
    SelectionBuilderSpec<SB, SR> getSelectionBuilderSpec();

    /**
     * The result group will be the group of which the values
     * will appear in the returned list after querying
     */
    boolean isResultGroup();

    /**
     * Sets this group to be the result group.
     * Can be useful when generating all groups using subBuilder
     * and then selecting one of them using query.selectValue.
     */
    void setResultGroup(boolean resultGroup);

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
     * Get the propertyData for the given propertyName within this selection group.
     */
    <T> TypeSafeQuerySelectionProxyPropertyData<T> getChild(String propertyName);

    /**
     * Put the propertyData for the given propertyName within this selection group.
     */
    void putChild(TypeSafeQuerySelectionProxyPropertyData<?> child);

    /**
     * Get all propertyData known within this selection group.
     */
    Collection<TypeSafeQuerySelectionProxyPropertyData<?>> getChildren();

    /**
     * Put a merger so that during result transformation a sub selected group of data may be included
     * in this selection result.
     */
    <SUBB, SUBR> void putMerger(TypeSafeQuerySelectionGroupInternal<SUBB, SUBR> sub, SelectionMerger<SR, SUBR> merger);

    /**
     * Get all mergers known within this selection group.
     * @return
     */
    Map<TypeSafeQuerySelectionGroupInternal<?, ?>, SelectionMerger<SR, ?>> getMergers();

    /**
     * Get the proxy representing this selection group.
     */
    TypeSafeQuerySelectionProxy<SB> getProxy();

    /**
     * Set the proxy representing this selection group.
     */
    void setProxy(TypeSafeQuerySelectionProxy<SB> proxy);
}
