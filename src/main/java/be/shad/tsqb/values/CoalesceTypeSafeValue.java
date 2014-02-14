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

/**
 * Represents a coalesce function. A coalesce is a fallback where the first 
 * value in the list which is not null is selected.
 */
public class CoalesceTypeSafeValue<T> extends TypeSafeValueImpl<T> implements TypeSafeValueContainer {
    private List<TypeSafeValue<T>> values = new LinkedList<>();
    
    public CoalesceTypeSafeValue(TypeSafeQuery query, Class<T> valueType) {
        super(query, valueType);
    }

    public CoalesceTypeSafeValue<T> or(T value) {
        return or(query.toValue(value));
    }
    
    public CoalesceTypeSafeValue<T> or(TypeSafeValue<T> value) {
        this.values.add(value);
        return this;
    }

    @Override
    public HqlQueryValue toHqlQueryValue() {
        StringBuilder coalesce = new StringBuilder();
        List<Object> params = new LinkedList<>();
        for(TypeSafeValue<T> value: values) {
            if( coalesce.length() > 0 ) {
                coalesce.append(",");
            } else {
                coalesce.append("coalesce (");
            }
            HqlQueryValue valueHql = value.toHqlQueryValue();
            coalesce.append(valueHql.getHql());
            for(Object param: valueHql.getParams()) {
                params.add(param);
            }
        }
        String hql = "";
        if( coalesce.length() > 0 ) {
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
}
