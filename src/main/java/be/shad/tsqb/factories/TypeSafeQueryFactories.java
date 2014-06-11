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
package be.shad.tsqb.factories;

import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.restrictions.RestrictionsGroupFactory;
import be.shad.tsqb.restrictions.RestrictionsGroupFactoryImpl;
import be.shad.tsqb.values.TypeSafeValueFactory;
import be.shad.tsqb.values.TypeSafeValueFactoryImpl;
import be.shad.tsqb.values.arithmetic.ArithmeticTypeSafeValueFactory;
import be.shad.tsqb.values.arithmetic.ArithmeticTypeSafeValueFactoryImpl;

public class TypeSafeQueryFactories {
    private final TypeSafeValueFactory typeSafeValueFactory;
    private final RestrictionsGroupFactory restrictionsGroupFactory;
    private final ArithmeticTypeSafeValueFactory arithmeticTypeSafeValueFactory;

    public TypeSafeQueryFactories(TypeSafeQueryInternal query) {
        this.restrictionsGroupFactory = new RestrictionsGroupFactoryImpl(query);
        this.arithmeticTypeSafeValueFactory = new ArithmeticTypeSafeValueFactoryImpl(query);
        this.typeSafeValueFactory = new TypeSafeValueFactoryImpl(query);
    }

    public RestrictionsGroupFactory getRestrictionsGroupFactory() {
        return restrictionsGroupFactory;
    }
    
    public ArithmeticTypeSafeValueFactory getArithmeticTypeSafeValueFactory() {
        return arithmeticTypeSafeValueFactory;
    }
    
    public TypeSafeValueFactory getTypeSafeValueFactory() {
        return typeSafeValueFactory;
    }
    
}
