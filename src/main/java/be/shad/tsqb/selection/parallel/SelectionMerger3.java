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

/**
 *
 */
public abstract class SelectionMerger3<RESULT, A, B, C> implements SelectionMerger<RESULT, SelectTriplet<A, B, C>> {

    /**
     * Delegates to {@link #mergeValuesIntoResult(Object, Object, Object, Object)} with the triple of selected values.
     */
    @Override
    public final void mergeIntoResult(RESULT partialResult, SelectTriplet<A, B, C> parallelDto) {
        mergeValuesIntoResult(partialResult, parallelDto.getFirst(), parallelDto.getSecond(), parallelDto.getThird());
    }

    /**
     * Merge three parallel selected values into the result dto manually.
     */
    public abstract void mergeValuesIntoResult(RESULT partialResult, A left, B middle, C right);

}
