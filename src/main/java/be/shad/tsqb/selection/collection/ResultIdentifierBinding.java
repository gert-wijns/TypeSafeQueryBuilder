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
package be.shad.tsqb.selection.collection;

public interface ResultIdentifierBinding {

    /**
     * Binds the value of the result proxy getter as (part of the) identity value.
     * <p>
     * Can be called one or more times. During result transformation,
     * all values are checked in the sequence bind was called.
     * When all values are equal, the result object will be considered
     * equal.
     */
    void bind(Object valueOfResultProxyGetter);
}
