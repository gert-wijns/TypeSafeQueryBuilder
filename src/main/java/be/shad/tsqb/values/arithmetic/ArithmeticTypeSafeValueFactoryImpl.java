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

import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.TypeSafeValue;

public class ArithmeticTypeSafeValueFactoryImpl implements ArithmeticTypeSafeValueFactory {
    private final TypeSafeQueryInternal query;

    public ArithmeticTypeSafeValueFactoryImpl(TypeSafeQueryInternal query) {
        this.query = query;
    }

    @Override
    public ArithmeticTypeSafeValue value(Number number) {
        return value((TypeSafeValue<Number>)query.toValue(number));
    }

    /**
     * Create a ArithmeticTypeSafeValueImpl which has the chaining arithmetic methods.
     */
    @SuppressWarnings("unchecked")
    public ArithmeticTypeSafeValue value(TypeSafeValue<? extends Number> numberVal) {
        return new ArithmeticTypeSafeValueImpl(query, (TypeSafeValue<Number>) numberVal);
    }

    /**
     * Wraps the values into another ArithmeticTypeSafeValue to generate brackets.
     * This method is only here to mark the intention, because it is the same as calling #value.
     */
    private ArithmeticTypeSafeValue wrap(TypeSafeValue<? extends Number> numberVal) {
        return value(numberVal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArithmeticTypeSafeValue divide(
            TypeSafeValue<? extends Number> numerator,
            TypeSafeValue<? extends Number> denominator) {
        return value(value(numerator).divide(denominator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArithmeticTypeSafeValue add(
            TypeSafeValue<? extends Number> value1,
            TypeSafeValue<? extends Number> value2,
            ArithmeticTypeSafeValue... values) {
        ArithmeticTypeSafeValue combined = value(value1);
        combined.add(value2);
        if (values != null && values.length > 0) {
            for(int i=0; i < values.length; i++) {
                combined.add(values[i]);
            }
        }
        return wrap(combined);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArithmeticTypeSafeValue subtract(
            TypeSafeValue<? extends Number> value1,
            TypeSafeValue<? extends Number> value2,
            ArithmeticTypeSafeValue... values) {
        ArithmeticTypeSafeValue combined = value(value1);
        combined.subtract(value2);
        if (values != null && values.length > 0) {
            for(int i=0; i < values.length; i++) {
                combined.subtract(values[i]);
            }
        }
        return wrap(combined);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArithmeticTypeSafeValue multiply(
            TypeSafeValue<? extends Number> value1,
            TypeSafeValue<? extends Number> value2,
            ArithmeticTypeSafeValue... values) {
        ArithmeticTypeSafeValue combined = value(value1);
        combined.multiply(value2);
        if (values != null && values.length > 0) {
            for(int i=0; i < values.length; i++) {
                combined.multiply(values[i]);
            }
        }
        return wrap(combined);
    }

}
