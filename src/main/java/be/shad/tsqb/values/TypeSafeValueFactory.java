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

public interface TypeSafeValueFactory {

    /**
     * Dequeues pending invocations:
     * <ul>
     * <li>If a pending invocation exists, returns a value representing this invocation.</li>
     * <li>If no pending invocation exists, returns a direct value.</li>
     * <li>IllegalStateException when more than one pending invocation.</li>
     * </ul>
     * In general, pending invocations are added when methods of proxied entities or typesare called,
     * except when the method returns another proxy.
     * <p>
     * Using the query restrictions clause building will automatically use the toValue method behind the scenes.
     * The use of this method in custom code is probably a rare thing and is not really encouraged,
     * but there may be cases when this can be useful.
     * <p>
     * An example when this can be used externally is when the value is when grouping by a custom hibernate Type.
     * 
     * @throws IllegalStateException when more than one invocation is pending
     */
    <VAL> TypeSafeValue<VAL> toValue(VAL value) throws IllegalStateException;
    
}
