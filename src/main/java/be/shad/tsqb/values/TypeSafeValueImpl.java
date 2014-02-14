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

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryInternal;

/**
 * Base implementation of the TypeSafeValue.
 * 
 * It was created to reduce the amount of code duplication.
 */
public abstract class TypeSafeValueImpl<T> implements TypeSafeValue<T> {
    protected final TypeSafeQueryInternal query;
    private final Class<T> valueType;
    
    protected TypeSafeValueImpl(TypeSafeQuery query, Class<T> valueType) {
        // all queries are internal queries - the internal query just hides some methods from the API 
        this.query = (TypeSafeQueryInternal) query; 
        this.valueType = valueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getValueClass() {
        return valueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T select() {
        return query.getRootQuery().queueValueSelected(this);
    }

}
