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

    @Test
    public void likeValueTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).like(new DirectTypeSafeValue<String>(query, "Jos%h"));
        validate(" from Person hobj1 where hobj1.name like ?", "Jos%h");
    }

    @Test
    public void containsTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).contains("isto"); // Kristof, Christophe, ...
        validate(" from Person hobj1 where hobj1.name like ?", "%isto%");
    }

    @Test
    public void startsWithTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).startsWith("Kris");
        validate(" from Person hobj1 where hobj1.name like ?", "Kris%");
    }
    
    @Test
    public void endsWithTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).endsWith("e");
        validate(" from Person hobj1 where hobj1.name like ?", "%e");
    }
    
}