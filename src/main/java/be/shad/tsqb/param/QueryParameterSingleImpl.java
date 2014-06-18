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

import java.util.Collection;


public class QueryParameterSingleImpl<T> implements QueryParameterSingle<T> {
    private final String name;
    private final Class<T> valueClass;
    private String userAlias;
    private T value;
    
    public QueryParameterSingleImpl(String name, 
            Class<T> valueClass, T value) {
        this.name = name;
        this.valueClass = valueClass;
        this.value = value;
    }
    
    @Override
    public boolean isCollectionRepresentative() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getAlias() {
        return userAlias;
    }

    @Override
    public void setAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    @Override
    public Class<T> getValueClass() {
        return valueClass;
    }

    @Override
    public T getValue() {
        return value;
    }
    
    @Override
    public void setValue(T value) {
        if (value != null && !valueClass.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(String.format("The value must be of type "
                    + "[%s] but was of type [%s].", valueClass, value.getClass()));
        }
        this.value = value;
    }

    @Override
    public <ST extends T> void setValue(Collection<ST> values) {
        throw new IllegalArgumentException("Cannot set a collection. "
                + "This is not a collection representative.");
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QueryParameterSingleImpl<?> other = (QueryParameterSingleImpl<?>) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("PARAM [%s, %s]", name, value);
    }

}
