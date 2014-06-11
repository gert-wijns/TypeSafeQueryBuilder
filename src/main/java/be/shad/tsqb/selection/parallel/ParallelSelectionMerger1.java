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


public abstract class ParallelSelectionMerger1<RESULT, A> implements ParallelSelectionMerger<RESULT, SelectValue<A>> {

    /**
     * Delegates to {@link #mergeValueIntoResult(Object, Object)} with the single selected value.
     */
    @Override
    public final void mergeIntoResult(RESULT partialResult, SelectValue<A> parallelDto) {
        mergeValueIntoResult(partialResult, parallelDto.getValue());
    }

    /**
     * Merge a single selected value into the result dto manually.
     */
    public abstract void mergeValueIntoResult(RESULT partialResult, A value);

}
