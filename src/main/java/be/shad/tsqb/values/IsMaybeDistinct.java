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

public interface IsMaybeDistinct {

    /**
     * The return value is used during the query projection.
     * When a value is distinct, the projection will be moved
     * to the front of the projections list.
     * <p>
     * There is a validation so there can only be one distinct value
     * and the projections.
     */
    boolean isDistinct();

}
