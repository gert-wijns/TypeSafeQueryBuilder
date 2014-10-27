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
package be.shad.tsqb.test;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.values.TypeSafeValueFunctions;

public class ConcatTypeSafeValueTest extends TypeSafeQueryTest {

    @Test
    public void testConcatDirectStrings() {
        TypeSafeValueFunctions fun = query.hqlFunction();
        
        query.from(Person.class);
        query.select(fun.concat("A").append("B"));
        
        validate("select concat(:np1, :np2) from Person hobj1", "A", "B");
    }

    @Test
    public void testConcatStringProperties() {
        TypeSafeValueFunctions fun = query.hqlFunction();
        
        Person person = query.from(Person.class);
        query.select(fun.concat(person.getName()).append(" aka ").append(person.getNickname()));
        
        validate("select concat(hobj1.name, :np1, hobj1.nickname) from Person hobj1", " aka ");
    }
    
    @Test
    public void testConcatNonStringProperties() {
        TypeSafeValueFunctions fun = query.hqlFunction();
        
        Person person = query.from(Person.class);
        query.select(fun.concat(person.getName()).
                append(" (").append(person.getAge()).append(")"));
        
        validate("select concat(hobj1.name, :np1, hobj1.age, :np2) from Person hobj1", " (", ")");
    }

    @Test
    public void testConcatNonStringDirectValue() {
        TypeSafeValueFunctions fun = query.hqlFunction();
        
        Person person = query.from(Person.class);
        query.select(fun.concat(person.getName()).
                append(" (").append(5.0).append(")"));
        
        validate("select concat(hobj1.name, :np1, :np2, :np3) from Person hobj1", " (", 5.0, ")");
    }
    
}
