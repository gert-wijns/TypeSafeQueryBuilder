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
package be.shad.tsqb.exceptions;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;

public class EqualsNotAllowedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EqualsNotAllowedException(String message) {
        super(message);
    }

    public static EqualsNotAllowedException create(TypeSafeQueryInternal query, Object that, String msg) {
        TypeSafeQueryProxyData lastInvocation = query.dequeueInvocation();
        if (lastInvocation != null) {
            that = lastInvocation.getPropertyPath();
        }
        return new EqualsNotAllowedException(
                "Attempting to equals [" + that + "] with [" + msg + "]. "
                + "You probably meant to use .eq(...) instead.");
    }

}
