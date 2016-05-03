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

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryScopeValidator;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;

/**
 * Represents a function with a list of values.
 */
public class FunctionTypeSafeValue<T> extends TypeSafeValueImpl<T> implements TypeSafeValueContainer {
    private List<TypeSafeValue<T>> values = new LinkedList<>();
    private String function;

    /**
     * Copy constructor
     */
    protected FunctionTypeSafeValue(CopyContext context, FunctionTypeSafeValue<T> original) {
        super(context, original);
        this.function = original.function;
        for(TypeSafeValue<T> value: original.values) {
            values.add(context.get(value));
        }
    }

    public FunctionTypeSafeValue(TypeSafeQuery query, String function, TypeSafeValue<T> value) {
        super(query, value.getValueClass());
        this.function = function;
        add(value);
    }

    protected void add(T value) {
        add(query.toValue(value));
    }

    protected void add(TypeSafeValue<T> value) {
        this.values.add(value);
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams parameters) {
        StringBuilder coalesce = new StringBuilder();
        List<Object> params = new LinkedList<>();
        boolean requiresLiterals = parameters.isRequiresLiterals();
        for(TypeSafeValue<T> value: values) {
            if (coalesce.length() > 0) {
                coalesce.append(",");
                parameters.setRequiresLiterals(requiresLiterals);
            } else {
                coalesce.append(function).append(" (");
                parameters.setRequiresLiterals(true);
            }
            HqlQueryValue valueHql = value.toHqlQueryValue(parameters);
            coalesce.append(valueHql.getHql());
            for(Object param: valueHql.getParams()) {
                params.add(param);
            }
        }
        String hql = "";
        if (coalesce.length() > 0) {
            hql = coalesce.append(")").toString();
        }
        return new HqlQueryValueImpl(hql, params);
    }

    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        for(TypeSafeValue<T> value: values) {
            validator.validateInScope(value);
        }
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new FunctionTypeSafeValue<>(context, this);
    }

}
