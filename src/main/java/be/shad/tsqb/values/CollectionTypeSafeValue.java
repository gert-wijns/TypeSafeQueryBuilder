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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.shad.tsqb.param.QueryParameter;
import be.shad.tsqb.query.TypeSafeQuery;

/**
 * The value is a collection of actual values, not proxies or property paths.
 * These values are added to the query as params.
 */
public class CollectionTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private final String parameterName;
    private List<T> values;
    
    public CollectionTypeSafeValue(TypeSafeQuery query, Collection<T> value) {
        super(query, null);
        setValues(value);
        this.parameterName = this.query.createNamedParam();
    }

    /**
     * Validate the values are not null or empty.
     */
    public void setValues(Collection<T> values) {
        if (values == null) {
            throw new IllegalArgumentException("Value may not be null.");
        }
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Value may not be empty.");
        }
        for(T value: values) {
            if(value == null) {
                throw new IllegalArgumentException(String.format("Null value in "
                        + "collection is not allowed. Collection: %s.", values));
            }
        }
        this.values = new ArrayList<>(values);
    }

    @Override
    public HqlQueryValueImpl toHqlQueryValue() {
        return new HqlQueryValueImpl(":" + parameterName, new QueryParameter(parameterName, values));
    }

}
