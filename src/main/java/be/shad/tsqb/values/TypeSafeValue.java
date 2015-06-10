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

/**
 * The TypeSafeValue is a wrapper which represents a generic value type.
 * <p>
 * Most method calls which accept Strings/Numbers/basic types convert the
 * input data and pass it on to a method which accepts a TypeSafeValue.
 * <p>
 * This wrapper extends the HqlQueryValueBuilder to be able to convert
 * it and add it to the HqlQuery when the query is converted.
 */
public interface TypeSafeValue<V> extends HqlQueryValueBuilder {

    /**
     * @return the type represented by this type safe value.
     */
    Class<V> getValueClass();

    /**
     * Returns a dummy value (typically <code>null</code>)
     * and informs the query to use this TypeSafeValue in the next
     * restriction or dto setter call.
     */
    V select();

}
