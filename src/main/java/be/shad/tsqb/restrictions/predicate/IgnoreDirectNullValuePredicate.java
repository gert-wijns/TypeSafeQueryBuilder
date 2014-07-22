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
package be.shad.tsqb.restrictions.predicate;

import be.shad.tsqb.query.copy.Stateless;
import be.shad.tsqb.values.CollectionTypeSafeValue;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Ignores direct null values.
 */
public final class IgnoreDirectNullValuePredicate implements RestrictionValuePredicate, Stateless {

    @Override
    public boolean isValueApplicable(TypeSafeValue<?> value) {
        if (value instanceof DirectTypeSafeValue) {
            return ((DirectTypeSafeValue<?>) value).getValue() != null;
        }
        if (value instanceof CollectionTypeSafeValue<?>) {
            return ((CollectionTypeSafeValue<?>) value).getValues() != null;
        }
        return true;
    }

}
