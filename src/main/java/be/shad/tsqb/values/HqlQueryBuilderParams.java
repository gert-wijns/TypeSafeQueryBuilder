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
package be.shad.tsqb.values;

public interface HqlQueryBuilderParams {

    String createNamedParameter();

    /**
     * When set, values need to be transformed to literals.
     */
    boolean isRequiresLiterals();

    /**
     * Returns the previous value.
     */
    boolean setRequiresLiterals(boolean requiresLiterals);

    /**
     * When ordering by is being transformed to an HQL value,
     * this flag can be used for additional validation.
     */
    boolean isCreatingOrderingBy();

    /**
     * Mark the start/end of creating the order by hql value.
     */
    void setCreatingOrderingBy(boolean creatingOrderingBy);

    /**
     * Whether to replace missing projected type safe value (because
     * it is not bound yet) with the projection path or not.
     * <p>
     * This flag is used to not throw errors on toFormattedString,
     * which is only used to inspect the query in its current state.
     */
    boolean isBuildingForDisplay();

    /**
     * @see #isBuildingForDisplay()
     */
    void setBuildingForDisplay(boolean buildingForDisplay);
}
