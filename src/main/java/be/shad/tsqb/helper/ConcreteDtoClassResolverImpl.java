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
package be.shad.tsqb.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConcreteDtoClassResolverImpl implements ConcreteDtoClassResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getConcreteClass(Class<?> requestedClass) {
        if (requestedClass.isInterface()) {
            if (SortedMap.class.isAssignableFrom(requestedClass)) {
                return TreeMap.class;
            }
            if (Map.class.isAssignableFrom(requestedClass)) {
                return HashMap.class;
            }
            throw new IllegalArgumentException("Don't know implementation "
                    + "to use for interface: " + requestedClass);
        } else {
            return requestedClass;
        }
    }

}
