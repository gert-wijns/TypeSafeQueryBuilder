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
package be.shad.tsqb.proxy;

import be.shad.tsqb.data.TypeSafeQueryProxyData;

/**
 * The interface which is additionally implemented by the proxied entities.
 */
public interface TypeSafeQueryProxy {

    /**
     * Retrieve all relevant data of this proxy.
     * <p>
     * It is expected this method name will not clash with any
     * existing domain object wherever this library may be used.
     * <p>
     * This could in theory be a restriction, but won't be in practice.
     */
    TypeSafeQueryProxyData getTypeSafeProxyData();
    
}
