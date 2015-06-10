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
package be.shad.tsqb.values.arithmetic;

import be.shad.tsqb.values.TypeSafeValue;

/**
 * Provides the basic arithmetic operations to build a calculated value as a result.
 */
public interface ArithmeticTypeSafeValue extends TypeSafeValue<Number>, ArithmeticGroupFunctions {

    /**
     * Adds a value to the arithmetic calculation. This can be a direct value
     * (an actual number value), or a value of a TypeSafeQueryProxy getter.
     *
     * @see #add(TypeSafeValue)
     */
    ArithmeticTypeSafeValue add(Number value);

    /**
     * Adds a value which is '+'ed with the existing arithmetic calculation.
     */
    ArithmeticTypeSafeValue add(TypeSafeValue<? extends Number> value);

    /**
     * Adds a value to the arithmetic calculation. This can be a direct value
     * (an actual number value), or a value of a TypeSafeQueryProxy getter.
     */
    ArithmeticTypeSafeValue subtract(Number value);

    /**
     * Adds a value which is '-'ed with the existing arithmetic calculation.
     */
    ArithmeticTypeSafeValue subtract(TypeSafeValue<? extends Number> value);

    /**
     * Adds a value to the arithmetic calculation. This can be a direct value
     * (an actual number value), or a value of a TypeSafeQueryProxy getter.
     */
    ArithmeticTypeSafeValue multiply(Number value);

    /**
     * Adds a value which is '*'ed with the existing arithmetic calculation.
     */
    ArithmeticTypeSafeValue multiply(TypeSafeValue<? extends Number> value);

    /**
     * Adds a value to the arithmetic calculation. This can be a direct value
     * (an actual number value), or a value of a TypeSafeQueryProxy getter.
     */
    ArithmeticTypeSafeValue divide(Number value);

    /**
     * Adds a value which is '/'ed with the existing arithmetic calculation.
     */
    ArithmeticTypeSafeValue divide(TypeSafeValue<? extends Number> value);

}
