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

import be.shad.tsqb.query.TypeSafeQuery;

/**
 * The value is a collection of actual values, not proxies or property paths.
 * These values are added to the query as params.
 */
public class CollectionTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private Collection<T> value;
    
    public CollectionTypeSafeValue(TypeSafeQuery query, Collection<T> value) {
        super(query, null);
        this.value = value;
    }

    public Collection<T> getValue() {
        return value;
    }

    public void setValue(Collection<T> value) {
        this.value = value;
    }

    @Override
    public HqlQueryValueImpl toHqlQueryValue() {
        StringBuilder sb = new StringBuilder("(");
        for(int i=0; i < value.size(); i++) {
            if( sb.length() > 1 ) {
                sb.append(", ");
            }
            sb.append("?");
        }
        sb.append(")");
        return new HqlQueryValueImpl(sb.toString(), value.toArray());
    }

}
