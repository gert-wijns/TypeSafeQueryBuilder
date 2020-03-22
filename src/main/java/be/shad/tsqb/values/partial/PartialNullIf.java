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
package be.shad.tsqb.values.partial;

import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.NullIfTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Captured the first part of the nullif, accepting another value before this becomes a
 * complete function in order to prevent constructing a function without
 * the proper amount of arguments.
 */
public class PartialNullIf<VAL> {
    private final TypeSafeQueryInternal query;
    private final TypeSafeValue<VAL> first;

    public PartialNullIf(TypeSafeQueryInternal query, TypeSafeValue<VAL> first) {
        this.query = query;
        this.first = first;
    }

    public TypeSafeValue<VAL> equalTo(VAL second) {
        return equalTo(query.toValue(second));
    }

    public TypeSafeValue<VAL> equalTo(TypeSafeValue<VAL> second) {
        return new NullIfTypeSafeValue<>(query, first, second);
    }
}
