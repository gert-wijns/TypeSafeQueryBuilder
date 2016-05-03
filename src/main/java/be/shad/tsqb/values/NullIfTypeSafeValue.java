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

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;

/**
 * Represents the nullif function, wrapping a first and second value.
 * When <code>first</code> = <code>second</code>, then null is returned,
 * otherwise <code>first</code> is returned.
 */
public class NullIfTypeSafeValue<T> extends FunctionTypeSafeValue<T> {

    protected NullIfTypeSafeValue(CopyContext context, FunctionTypeSafeValue<T> original) {
        super(context, original);
    }

    public NullIfTypeSafeValue(TypeSafeQuery query, TypeSafeValue<T> v1, TypeSafeValue<T> v2) {
        super(query, "nullif", v1);
        add(v2);
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new NullIfTypeSafeValue<>(context, this);
    }
}
