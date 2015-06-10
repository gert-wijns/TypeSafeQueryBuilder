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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryScopeValidator;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;

/**
 * TypeSafeValue to build a value using other values.
 */
public class OperationTypeSafeValue<T> extends TypeSafeValueImpl<T> implements TypeSafeValueContainer {
    public enum OperationTypeSafeValueBracketsPolicy {
        Always,
        Never,
        WhenMoreThanOne;
    }

    private List<TypeSafeValue<? extends T>> values = new LinkedList<>();
    private List<String> operations = new LinkedList<>();
    private OperationTypeSafeValueBracketsPolicy bracketsPolicy;

    /**
     * Copy constructor
     */
    protected OperationTypeSafeValue(CopyContext context, OperationTypeSafeValue<T> original) {
        super(context, original);
        this.bracketsPolicy = original.bracketsPolicy;
        for(TypeSafeValue<? extends T> value: original.values) {
            this.values.add(context.get(value));
        }
        for(String operation: original.operations) {
            this.operations.add(operation);
        }
    }

    public OperationTypeSafeValue(TypeSafeQuery query, TypeSafeValue<T> firstValue,
            OperationTypeSafeValueBracketsPolicy bracketsPolicy) {
        super(query, firstValue.getValueClass());
        this.values.add(firstValue);
        this.bracketsPolicy = bracketsPolicy;
    }

    @SuppressWarnings("unchecked")
    public void add(String operation, TypeSafeValue<? extends T> value) {
        values.add((TypeSafeValue<T>) value);
        operations.add(operation);
    }

    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        for(TypeSafeValue<? extends T> value: values) {
            validator.validateInScope(value);
        }
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        boolean addBrackets = isAddBrackets();
        HqlQueryValueImpl combined = new HqlQueryValueImpl();
        if (addBrackets) {
            combined.appendHql("(");
        }

        Iterator<TypeSafeValue<? extends T>> valuesIt = values.iterator();
        Iterator<String> operationsIt = operations.iterator();

        HqlQueryValue valueHql = valuesIt.next().toHqlQueryValue(params);
        combined.appendHql(valueHql.getHql());
        combined.addParams(valueHql.getParams());

        while (valuesIt.hasNext()) {
            valueHql = valuesIt.next().toHqlQueryValue(params);
            String operation = operationsIt.next();
            combined.appendHql(" ").append(operation).append(" ");
            combined.appendHql(valueHql.getHql());
            combined.addParams(valueHql.getParams());
        }
        if (addBrackets) {
            combined.appendHql(")");
        }

        return combined;
    }

    /**
     * Evaluates brackets policy to decide whether to add brackets or not.
     */
    private boolean isAddBrackets() {
        switch (bracketsPolicy) {
            case WhenMoreThanOne: return values.size() > 1;
            case Never:           return false;
            case Always:
            default:              return true;
        }
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new OperationTypeSafeValue<>(context, this);
    }

}
