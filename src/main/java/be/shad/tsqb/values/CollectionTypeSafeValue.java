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

import java.util.Collection;

import be.shad.tsqb.param.QueryParameterCollection;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryInternal;

/**
 * The value is a collection of actual values, not proxies or property paths.
 * These values are added to the query as params.
 */
public class CollectionTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private final QueryParameterCollection<T> parameter;
    
    public CollectionTypeSafeValue(TypeSafeQuery query, Class<T> valueClass, Collection<T> value) {
        this(query, ((TypeSafeQueryInternal) query).createCollectionNamedParam(valueClass));
        setValues(value);
    }
    
    public CollectionTypeSafeValue(TypeSafeQuery query, Class<T> valueClass) {
        this(query, ((TypeSafeQueryInternal) query).createCollectionNamedParam(valueClass));
    }

    public CollectionTypeSafeValue(TypeSafeQuery query, QueryParameterCollection<T> parameter) {
        super(query, parameter.getValueClass());
        this.parameter = parameter;
    }
    
    public QueryParameterCollection<T> getParameter() {
        return parameter;
    }

    /**
     * Validate the values are not null or empty.
     */
    public void setValues(Collection<T> values) {
        parameter.setValue(values);
    }

    @Override
    public HqlQueryValueImpl toHqlQueryValue() {
        if (parameter.getValues() == null) {
            throw new IllegalStateException("The value must be set before calling toHqlQueryValue. "
                    + "This was not the case for parameter: " + parameter);
        }
        return new HqlQueryValueImpl(":" + parameter.getName(), parameter);
    }

}
