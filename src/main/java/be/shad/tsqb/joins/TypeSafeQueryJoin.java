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
package be.shad.tsqb.joins;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.restrictions.RestrictionsGroupImpl;

/**
 * Links a proxy data with its restrictions.
 */
public class TypeSafeQueryJoin<T> extends RestrictionsGroupImpl {
    private final TypeSafeQueryProxyData data;

    public TypeSafeQueryJoin(TypeSafeQueryInternal query, TypeSafeQueryProxyData data) {
        super(query, data);
        this.data = data;
    }
    
    @SuppressWarnings("unchecked")
    public T getProxy() {
        return (T) data.getProxy();
    }
    
    public TypeSafeQueryProxyData getData() {
        return data;
    }

}
