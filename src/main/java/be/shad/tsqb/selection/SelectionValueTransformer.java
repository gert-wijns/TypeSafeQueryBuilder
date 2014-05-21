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
package be.shad.tsqb.selection;

/**
 * Transforms a queried value in memory before setting it on the select dto.
 * Some example cases:
 * <ul>
 * <li>Select a value of type String which represents a date from the database, and convert it to a Date to set on a dto.
 * <li>Select an ID which can used to retrieve some cached value to set on the dto instead.
 * </ul>
 * <p>
 * This transformer is not absolutely necessary because you could also capture the values into the dto
 * and transform them afterwards, but this does mean the dto is contaminated with fields which are
 * not interesting in the final result.
 */
public interface SelectionValueTransformer<A, B> {

    /**
     * Transforms a queried value in memory before setting it on the select dto.
     */
    B convert(A a) throws SelectionValueTransformerException;
    
}
