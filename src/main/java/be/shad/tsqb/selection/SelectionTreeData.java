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
import java.util.Map;

import be.shad.tsqb.helper.BuildFn;
import lombok.Getter;
import lombok.Setter;

/**
 * Data container used during result transformation by the {@link TypeSafeQueryResultTransformer}.
 */
public class SelectionTreeData {
    // using IdentityHashMap so root so nested collection results are not considered duplicate if they have a different parent
    public final SelectionIdentityTree identityTree = new SelectionIdentityTree();
    public final Map<Object, Map<Object, Object>> collectionValues = new IdentityHashMap<>();
    private @Getter @Setter SelectionTreeResult result;
    private BuildFn<Object, Object> groupResultTf;

    /**
     * When the value already existed, and the current value
     * was updated with contents found in the identityTree,
     * then duplicate is set to true.
     */
    private @Getter @Setter boolean duplicate;

    public void setGroupResultTf(BuildFn<Object, Object> groupResultTf) {
        this.groupResultTf = groupResultTf;
    }

    /**
     * The value for this data element during the processing of
     * a result row tuple.
     */
    public Object getCurrentValue() {
        return result.getCurrentValue();
    }

    public Object getBuiltValue() {
        Object currentValue = result.getCurrentValue();
        if (currentValue == null || groupResultTf == null) {
            return currentValue;
        }
        Object builtValue = result.getBuiltValue();
        if (builtValue == null) {
            result.setBuiltValue(groupResultTf.build(currentValue));
        }
        return result.getBuiltValue();
    }
}
