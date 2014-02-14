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

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Couples a value to a selection alias.
 * <p>
 * The value can be anything, created by a function, a custom value, a subquery, a referenced value and so on.
 * <p>
 * The propertyName represents the alias of this value in the select clause.
 */
public class TypeSafeValueProjection implements TypeSafeProjection {
    private final TypeSafeValue<?> value;
    private final String propertyName;

    public TypeSafeValueProjection(TypeSafeValue<?> value, String propertyName) {
        this.value = value;
        this.propertyName = propertyName;
    }

    @Override
    public void appendTo(HqlQuery query) {
        HqlQueryValue val = value.toHqlQueryValue();
        query.appendSelect(val.getHql() + (propertyName == null ? "": " as " + propertyName));
        query.addParams(val.getParams());
    }

}
