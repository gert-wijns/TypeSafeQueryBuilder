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

public class HqlQueryBuilderParamsImpl implements HqlQueryBuilderParams {
    private int namedParamCount = 1;
    private boolean requiresLiterals;
    private boolean creatingOrderingBy;
    private boolean buildingForDisplay;

    @Override
    public boolean isRequiresLiterals() {
        return requiresLiterals;
    }

    public boolean setRequiresLiterals(boolean requiresLiterals) {
        boolean previous = this.requiresLiterals;
        this.requiresLiterals = requiresLiterals;
        return previous;
    }

    @Override
    public boolean isCreatingOrderingBy() {
        return creatingOrderingBy;
    }

    @Override
    public void setCreatingOrderingBy(boolean creatingOrderingBy) {
        this.creatingOrderingBy = creatingOrderingBy;
    }

    @Override
    public boolean isBuildingForDisplay() {
        return buildingForDisplay;
    }

    @Override
    public void setBuildingForDisplay(boolean buildingForDisplay) {
        this.buildingForDisplay = buildingForDisplay;
    }

    @Override
    public String createNamedParameter() {
        return "np" + namedParamCount++;
    }

}
