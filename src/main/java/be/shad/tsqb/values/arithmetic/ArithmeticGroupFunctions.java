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

public interface ArithmeticGroupFunctions {
    
    /**
     * Divides a numerator value by the denominator value
     * The returned ArithmeticTypeSafeValue will be wrapped with brackets. 
     * 
     * @return (a/b) when divide(a, b)
     */
    ArithmeticTypeSafeValue divide(
            TypeSafeValue<? extends Number> numerator, 
            TypeSafeValue<? extends Number> denominator);

    /**
     * Adds multiple values together.
     * The returned ArithmeticTypeSafeValue will be wrapped with brackets.
     *  
     * @return (a+b+c+d) when add(a, b, c, d)
     */
    ArithmeticTypeSafeValue add(
            TypeSafeValue<? extends Number> value1, 
            TypeSafeValue<? extends Number> value2, 
            ArithmeticTypeSafeValue... values);

    /**
     * Subtracts multiple values from the first value.
     * The returned ArithmeticTypeSafeValue will be wrapped with brackets. 
     * 
     * @return (a-b-c-d) When subtract(a, b, c, d)
     */
    ArithmeticTypeSafeValue subtract(
            TypeSafeValue<? extends Number> value1, 
            TypeSafeValue<? extends Number> value2, 
            ArithmeticTypeSafeValue... values);

    /**
     * Multiplies multiple values together.
     * The returned ArithmeticTypeSafeValue will be wrapped with brackets.
     * 
     * @return (a*b*c*d) when multiply(a, b, c, d)
     */
    ArithmeticTypeSafeValue multiply(
            TypeSafeValue<? extends Number> value1, 
            TypeSafeValue<? extends Number> value2, 
            ArithmeticTypeSafeValue... values);

}
