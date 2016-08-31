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
package be.shad.tsqb.selection.parallel;

import java.util.Map;

/**
 * @author Gert
 *
 */
public abstract class MapSelectionMerger<RESULT, K, V> implements SelectionMerger<RESULT, Map<K, V>> {

    /**
     * Delegates to {@link #mergeMapIntoResult(Object, Object)} with the map of extra selected values.
     */
    @Override
    public final void mergeIntoResult(RESULT partialResult, Map<K, V> parallelDto) {
        mergeMapIntoResult(partialResult, parallelDto);
    }

    /**
     * Merge a map of selected values into the result dto manually.
     */
    public abstract void mergeMapIntoResult(RESULT partialResult, Map<K, V> value);

}
