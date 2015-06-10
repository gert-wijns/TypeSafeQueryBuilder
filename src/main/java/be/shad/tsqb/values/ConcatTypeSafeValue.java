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
 * Value representing the 'concat' hql function, with append function to add extra values to concatenate.
 */
public class ConcatTypeSafeValue extends TypeSafeValueImpl<String> implements TypeSafeValueContainer {
    private List<TypeSafeValue<?>> values = new LinkedList<>();

    public ConcatTypeSafeValue(CopyContext context, TypeSafeValueImpl<String> original) {
        super(context, original);
    }

    public ConcatTypeSafeValue(TypeSafeQuery query) {
        super(query, String.class);
    }

    /**
     * Copy constructor
     */
    protected ConcatTypeSafeValue(CopyContext context, ConcatTypeSafeValue original) {
        super(context, original);
        for(TypeSafeValue<?> value: original.values) {
            values.add(context.get(value));
        }
    }

    /**
     * Adds a value to be added to the concatenate function
     */
    public ConcatTypeSafeValue append(String val) {
        return append(query.toValue(val));
    }

    /**
     * Adds a value to be added to the concatenate function
     */
    public ConcatTypeSafeValue append(Enum<?> val) {
        return append(query.toValue(val));
    }

    /**
     * Adds a value to be added to the concatenate function
     */
    public ConcatTypeSafeValue append(Number val) {
        return append(query.toValue(val));
    }

    /**
     * Adds a value to be added to the concatenate function
     */
    public ConcatTypeSafeValue append(TypeSafeValue<?> val) {
        values.add(val);
        return this;
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams parameters) {
        StringBuilder concat = new StringBuilder();
        List<Object> params = new LinkedList<>();
        for(TypeSafeValue<?> value: values) {
            if (concat.length() > 0) {
                concat.append(", ");
            } else {
                concat.append("concat(");
            }
            HqlQueryValue valueHql = value.toHqlQueryValue(parameters);
            concat.append(valueHql.getHql());
            for(Object param: valueHql.getParams()) {
                params.add(param);
            }
        }
        String hql = "";
        if (concat.length() > 0) {
            hql = concat.append(")").toString();
        }
        return new HqlQueryValueImpl(hql, params);
    }

    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        for(TypeSafeValue<?> value: values) {
            validator.validateInScope(value);
        }
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new ConcatTypeSafeValue(context, this);
    }
}
