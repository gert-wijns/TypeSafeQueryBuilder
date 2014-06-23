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
package be.shad.tsqb.query;

import be.shad.tsqb.values.CaseTypeSafeValue;
import be.shad.tsqb.values.CustomTypeSafeValue;
import be.shad.tsqb.values.HqlQueryValueImpl;


public class TypeSafeQueryValuesImpl implements TypeSafeQueryValues {
    private TypeSafeQueryInternal query;

    public TypeSafeQueryValuesImpl(TypeSafeQueryInternal query) {
        this.query = query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CaseTypeSafeValue<T> caseWhen(Class<T> valueType) {
        return new CaseTypeSafeValue<>(query, valueType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CustomTypeSafeValue<T> custom(Class<T> valueType, String hql, Object... params) {
        return new CustomTypeSafeValue<>(query, valueType, HqlQueryValueImpl.hql(hql, params));
    }


}
