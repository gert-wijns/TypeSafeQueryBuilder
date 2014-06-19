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
package be.shad.tsqb.param;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public final class QueryParameterCollectionImpl<T> implements QueryParameterCollection<T> {
    private final String name;
    private String alias;
    private final Class<T> valueClass;
    private Collection<T> values;
    
    public QueryParameterCollectionImpl(String queryAlias, Class<T> valueClass, T value) {
        this.name = queryAlias;
        this.valueClass = valueClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAlias() {
        return alias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getValueClass() {
        return valueClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> getValues() {
        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(T object) {
        if (object != null) {
            setValue(Collections.singleton(object));
        } else {
            this.values = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParameterValue() {
        return getValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <ST extends T> void setValue(Collection<ST> values) {
        if (values != null) {
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Collection may not be empty when set.");
            }
            for(Object value: values) {
                if (value == null) {
                    throw new IllegalArgumentException(String.format("Null value in "
                            + "collection is not allowed. Collection: %s.", values));
                }
                if (!valueClass.isAssignableFrom(value.getClass())) {
                    throw new IllegalArgumentException(String.format("The value must be of type "
                            + "[%s] but was of type [%s].", valueClass, value.getClass()));
                }
            }
            this.values = new ArrayList<T>(values);
        } else {
            this.values = null;
        }
    }
    
    @Override
    public String toString() {
        return String.format("PARAMS [%s, %s, %s]", name, alias, values);
    }
}
