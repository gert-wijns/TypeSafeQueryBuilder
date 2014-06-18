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

import be.shad.tsqb.param.QueryParameterSingle;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryInternal;

/**
 * The value is an actual value, not a proxy or property path.
 * This value is added as param to the query.
 */
public class DirectTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private final QueryParameterSingle<T> parameter;
    
    @SuppressWarnings("unchecked")
    public DirectTypeSafeValue(TypeSafeQuery query, T value) {
        this(query, ((TypeSafeQueryInternal) query).createSingleNamedParam((Class<T>) value.getClass()));
        setValue(value);
    }
    
    public DirectTypeSafeValue(TypeSafeQuery query, Class<T> valueClass) {
        this(query, ((TypeSafeQueryInternal) query).createSingleNamedParam(valueClass));
    }

    public DirectTypeSafeValue(TypeSafeQuery query, QueryParameterSingle<T> parameter) {
        super(query, parameter.getValueClass());
        this.parameter = parameter;
    }
    
    public QueryParameterSingle<T> getParameter() {
        return parameter;
    }

    public T getValue() {
        return parameter.getValue();
    }

    public void setValue(T value) {
        parameter.setValue(value);
    }

    @Override
    public HqlQueryValueImpl toHqlQueryValue() {
        if (parameter.getValue() == null) {
            throw new IllegalStateException("The value must be set before calling toHqlQueryValue. "
                    + "This was not the case for parameter: " + parameter);
        }
        return new HqlQueryValueImpl(":" + parameter.getName(), parameter);
    }

}
