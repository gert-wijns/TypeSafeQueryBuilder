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

public class SelectionValueTransformerException extends RuntimeException {
    private static final long serialVersionUID = -8551190372690304747L;

    public SelectionValueTransformerException() {
        super();
    }

    public SelectionValueTransformerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SelectionValueTransformerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SelectionValueTransformerException(String message) {
        super(message);
    }

    public SelectionValueTransformerException(Throwable cause) {
        super(cause);
    }

}
