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

/**
 * Groups data related to a value in the tuple array.
 * The index is saved so the tuple array can be used out of order,
 * so identity fields are used before other fields and
 * objects are created/populated in sequence rather than all at once.
 */
public class SelectionTreeValue {
    public final int tupleValueIndex;
    public final String propertyPath;
    public final SelectionValueTransformer<?, ?> valueTransformer;

    public SelectionTreeValue(int tupleValueIndex, String propertyPath,
            SelectionValueTransformer<?, ?> valueTransformer) {
        this.tupleValueIndex = tupleValueIndex;
        this.propertyPath = propertyPath;
        this.valueTransformer = valueTransformer;
    }
}
