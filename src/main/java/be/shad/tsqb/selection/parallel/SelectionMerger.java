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
public interface SelectionMerger<RES, PAR> {
    
    /**
     * The result has all values which were either selected into it directly,
     * or which  were already merged into the result.
     * <p>
     * The parallel result dto which had one of its properties filled first
     * will be the first to be merged, so mind this when using multiple mergers
     * which happen to depend on one another.
     * (though that sounds a bit too exotic maybe, that's how it works).
     * 
     * @param partialResult the (partially) transformed result at the time of conversion
     * @param parallelDto the populated parallel selected dto which is to be merged into the result
     */
    void mergeIntoResult(RES partialResult, PAR parallelDto);
    
}
