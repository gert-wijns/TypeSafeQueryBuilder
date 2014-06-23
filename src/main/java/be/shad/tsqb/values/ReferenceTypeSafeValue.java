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

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;

/**
 * The proxy data represents a getter on one of the proxies
 * created while building a query.
 * <p>
 * The data can be converted to a property path by calling its getAlias method.
 */
public class ReferenceTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private final TypeSafeQueryProxyData data;

    /**
     * Copy constructor
     */
    protected ReferenceTypeSafeValue(CopyContext context, ReferenceTypeSafeValue<T> original) {
        super(context, original);
        this.data = context.get(original.data);
    }
    
    @SuppressWarnings("unchecked")
    public ReferenceTypeSafeValue(TypeSafeQuery query, TypeSafeQueryProxyData data) {
        super(query, (Class<T>) data.getPropertyType());
        this.data = data;
    }
    
    public TypeSafeQueryProxyData getData() {
        return data;
    }
    
    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        return new HqlQueryValueImpl(data.getAlias());
    }
    
    @Override
    public Copyable copy(CopyContext context) {
        return new ReferenceTypeSafeValue<>(context, this);
    }

}
