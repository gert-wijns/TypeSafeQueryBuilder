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

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryScopeValidator;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.OperationTypeSafeValue;
import be.shad.tsqb.values.OperationTypeSafeValue.OperationTypeSafeValueBracketsPolicy;
import be.shad.tsqb.values.TypeSafeValue;
import be.shad.tsqb.values.TypeSafeValueContainer;
import be.shad.tsqb.values.TypeSafeValueImpl;

public class ArithmeticTypeSafeValueImpl extends TypeSafeValueImpl<Number> implements TypeSafeValueContainer, ArithmeticTypeSafeValue {
    private OperationTypeSafeValue<Number> combinedValue;

    public ArithmeticTypeSafeValueImpl(TypeSafeQuery query, TypeSafeValue<Number> firstValue) {
        super(query, firstValue.getValueClass());
        this.combinedValue = new OperationTypeSafeValue<>(query, firstValue, 
                OperationTypeSafeValueBracketsPolicy.WhenMoreThanOne);
    }

    /**
     * Copy constructor
     */
    protected ArithmeticTypeSafeValueImpl(CopyContext context, ArithmeticTypeSafeValueImpl original) {
        super(context, original);
        this.combinedValue = context.get(original.combinedValue);
    }

    @Override
    public ArithmeticTypeSafeValue add(Number value) {
        return add(query.toValue(value));
    }

    @Override
    public ArithmeticTypeSafeValue add(TypeSafeValue<? extends Number> value) {
        combinedValue.add("+", value);
        return this;
    }

    @Override
    public ArithmeticTypeSafeValue subtract(Number value) {
        return subtract(query.toValue(value));
    }

    @Override
    public ArithmeticTypeSafeValue subtract(TypeSafeValue<? extends Number> value) {
        combinedValue.add("-", value);
        return this;
    }

    @Override
    public ArithmeticTypeSafeValue multiply(Number value) {
        return multiply(query.toValue(value));
    }

    @Override
    public ArithmeticTypeSafeValue multiply(TypeSafeValue<? extends Number> value) {
        combinedValue.add("*", value);
        return this;
    }

    @Override
    public ArithmeticTypeSafeValue divide(Number value) {
        return divide(query.toValue(value));
    }

    @Override
    public ArithmeticTypeSafeValue divide(TypeSafeValue<? extends Number> value) {
        combinedValue.add("/", value);
        return this;
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        // when calculating a value, hibernate checks if all number types are exactly the same,
        // this problem is avoided by replacing the values with literals 
        boolean previous = params.setRequiresLiterals(true);
        HqlQueryValue hqlQueryValue = combinedValue.toHqlQueryValue(params);
        params.setRequiresLiterals(previous);
        return hqlQueryValue;
    }

    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        validator.validateInScope(combinedValue);
    }

    @Override
    public ArithmeticTypeSafeValue divide(
            TypeSafeValue<? extends Number> numerator, 
            TypeSafeValue<? extends Number> denominator) {
        return query.getArithmeticsBuilder().divide(numerator, denominator);
    }

    @Override
    public ArithmeticTypeSafeValue add(
            TypeSafeValue<? extends Number> value1, 
            TypeSafeValue<? extends Number> value2, 
            ArithmeticTypeSafeValue... values) {
        return query.getArithmeticsBuilder().add(value1, value2, values);
    }

    @Override
    public ArithmeticTypeSafeValue subtract(
            TypeSafeValue<? extends Number> value1, 
            TypeSafeValue<? extends Number> value2, 
            ArithmeticTypeSafeValue... values) {
        return query.getArithmeticsBuilder().subtract(value1, value2, values);
    }

    @Override
    public ArithmeticTypeSafeValue multiply(
            TypeSafeValue<? extends Number> value1, 
            TypeSafeValue<? extends Number> value2, 
            ArithmeticTypeSafeValue... values) {
        return query.getArithmeticsBuilder().multiply(value1, value2, values);
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new ArithmeticTypeSafeValueImpl(context, this);
    }

}
