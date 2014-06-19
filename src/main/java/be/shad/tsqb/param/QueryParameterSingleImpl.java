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



public class QueryParameterSingleImpl<T> implements QueryParameterSingle<T> {
    private final String name;
    private final Class<T> valueClass;
    private String alias;
    private T value;
    
    public QueryParameterSingleImpl(String name, 
            Class<T> valueClass, T value) {
        this.name = name;
        this.valueClass = valueClass;
        this.value = value;
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
    public T getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(T value) {
        if (value != null && !valueClass.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(String.format("The value must be of type "
                    + "[%s] but was of type [%s].", valueClass, value.getClass()));
        }
        this.value = value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParameterValue() {
        return getValue();
    }
    
    @Override
    public String toString() {
        return String.format("PARAM [%s, %s, %s]", name, alias, value);
    }

}
