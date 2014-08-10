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

import java.util.IdentityHashMap;

/**
 * Data container used during result transformation by the {@link TypeSafeQueryResultTransformer}.
 */
public class SelectionTreeData {
    // using IdentityHashMap so root so nested collection results are not considered duplicate if they have a different parent
    public final IdentityHashMap<Object, SelectionIdentityTree> identityTrees = new IdentityHashMap<>();
    private Object currentValue;
    private boolean duplicate;
    
    /**
     * The value for this data element during the processing of 
     * a result row tuple. 
     */
    public Object getCurrentValue() {
        return currentValue;
    }
    
    /**
     * Update during the processing of the result row tuple.
     * This can be either a newly decorated value or a
     * value found in the identityTree when applicable.
     */
    public void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }

    /**
     * When the value already existed, and the current value
     * was updated with contents found in the identityTree,
     * then duplicate is set to true.
     */
    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }
}
