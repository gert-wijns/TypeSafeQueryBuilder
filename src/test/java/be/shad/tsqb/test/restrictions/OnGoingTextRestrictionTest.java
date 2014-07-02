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
package be.shad.tsqb.test.restrictions;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.DirectTypeSafeValue;

public class OnGoingTextRestrictionTest extends TypeSafeQueryTest {
    private String NAMED_PARAM_1 = "NAMED_PARAM_1";
    private String NAMED_PARAM_2 = "NAMED_PARAM_2";

    @Test
    public void likeValueTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).like(new DirectTypeSafeValue<String>(query, "Jos%h"));
        validate(" from Person hobj1 where hobj1.name like :np1", "Jos%h");
    }

    @Test
    public void containsTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).contains("isto"); // Kristof, Christophe, ...
        validate(" from Person hobj1 where hobj1.name like :np1", "%isto%");
    }
    
    @Test
    public void namedContainsTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).contains().named(NAMED_PARAM_1);
        
        query.named().setValue(NAMED_PARAM_1, "isto");
        validate(" from Person hobj1 where hobj1.name like :np1", "%isto%");

        query.named().setValue(NAMED_PARAM_1, "ar");
        validate(" from Person hobj1 where hobj1.name like :np1", "%ar%");
    }

    @Test
    public void startsWithTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).startsWith("Kris");
        validate(" from Person hobj1 where hobj1.name like :np1", "Kris%");
    }

    @Test
    public void namedStartsWithTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).startsWith().named(NAMED_PARAM_1);
        
        query.named().setValue(NAMED_PARAM_1, "Kris");
        validate(" from Person hobj1 where hobj1.name like :np1", "Kris%");

        query.named().setValue(NAMED_PARAM_1, "John");
        validate(" from Person hobj1 where hobj1.name like :np1", "John%");
    }
    
    @Test
    public void endsWithTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).endsWith("e");
        validate(" from Person hobj1 where hobj1.name like :np1", "%e");
    }
    
    @Test
    public void namedEndsWithTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).endsWith().named(NAMED_PARAM_1);
        
        query.named().setValue(NAMED_PARAM_1, "e");
        validate(" from Person hobj1 where hobj1.name like :np1", "%e");

        query.named().setValue(NAMED_PARAM_1, "f");
        validate(" from Person hobj1 where hobj1.name like :np1", "%f");
    }

    @Test
    public void startsWithOrStartsWithTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).startsWith("Jos").or().startsWith("Kris");
        validate(" from Person hobj1 where hobj1.name like :np1 or hobj1.name like :np2", "Jos%", "Kris%");
    }
    
    @Test
    public void namedStartsWithOrStartsWithTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).startsWith().named(NAMED_PARAM_1).or().startsWith().named(NAMED_PARAM_2);

        query.named().setValue(NAMED_PARAM_1, "Jos");
        query.named().setValue(NAMED_PARAM_2, "Kris");
        validate(" from Person hobj1 where hobj1.name like :np1 or hobj1.name like :np2", "Jos%", "Kris%");

        query.named().setValue(NAMED_PARAM_1, "Manny");
        query.named().setValue(NAMED_PARAM_2, "Victor");
        validate(" from Person hobj1 where hobj1.name like :np1 or hobj1.name like :np2", "Manny%", "Victor%");
    }
    
}