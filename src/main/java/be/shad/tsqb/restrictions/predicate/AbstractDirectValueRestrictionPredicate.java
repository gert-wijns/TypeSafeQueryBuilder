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

import be.shad.tsqb.values.DirectTypeSafeValueWrapper;
import be.shad.tsqb.values.TypeSafeValue;

public abstract class AbstractDirectValueRestrictionPredicate<T> implements RestrictionPredicate {

    private Class<T> valueClass;

    public AbstractDirectValueRestrictionPredicate(Class<T> valueClass) {
        this.valueClass = valueClass;
    }

    /**
     * Check if the value is a direct value wrapper, if it is and its wrapped value is not null,
     * then it is passed on to the extending predicate.
     */
    @Override
    public final boolean isValueApplicable(TypeSafeValue<?> value) {
        if (value instanceof DirectTypeSafeValueWrapper) {
            Object wrappedValue = ((DirectTypeSafeValueWrapper<?>) value).getWrappedValue();
            if (wrappedValue != null && valueClass.isAssignableFrom(wrappedValue.getClass())) {
                return isValueApplicable(valueClass.cast(wrappedValue));
            }
        }
        return true;
    }

    /**
     * Implement to ignore value in case it matches some filter.
     */
    protected abstract boolean isValueApplicable(T value);

}
