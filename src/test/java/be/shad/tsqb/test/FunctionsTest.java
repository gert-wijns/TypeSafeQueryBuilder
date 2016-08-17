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

public class FunctionsTest extends TypeSafeQueryTest {

    @Test
    public void testLengthFunction() {
        Person fromProxy = query.from(Person.class);
        query.select(query.hqlFunction().length(fromProxy.getName()));
        validate("select length(hobj1.name) from Person hobj1");
    }

    @Test
    public void testAvgFunction() {
        Person person = query.from(Person.class);
        query.select(query.hqlFunction().avg(person.getAge()));
        validate("select avg(hobj1.age) from Person hobj1");
    }

    @Test
    public void testMaxFunction() {
        Person person = query.from(Person.class);
        query.select(query.hqlFunction().max(person.getAge()));
        validate("select max(hobj1.age) from Person hobj1");
    }

    @Test
    public void testMinFunction() {
        Person person = query.from(Person.class);
        query.select(query.hqlFunction().min(person.getAge()));
        validate("select min(hobj1.age) from Person hobj1");
    }

    @Test
    public void testMinStringFunction() {
        Person person = query.from(Person.class);
        query.select(query.hqlFunction().min(person.getName()));
        validate("select min(hobj1.name) from Person hobj1");
    }

    @Test
    public void testSumFunction() {
        Person person = query.from(Person.class);
        query.select(query.hqlFunction().sum(person.getAge()));
        validate("select sum(hobj1.age) from Person hobj1");
    }

    @Test
    public void testLowerFunction() {
        Person person = query.from(Person.class);
        query.select(query.hqlFunction().lower(person.getName()));
        validate("select lower(hobj1.name) from Person hobj1");
    }

    @Test
    public void testUpperFunction() {
        Person person = query.from(Person.class);
        query.select(query.hqlFunction().lower(person.getName()));
        validate("select lower(hobj1.name) from Person hobj1");
    }

    @Test
    public void testWrapFunction() {
        Person person = query.from(Person.class);
        query.select(query.hqlFunction().wrap(query.toValue(person.getName())));
        validate("select (hobj1.name) from Person hobj1");
    }
}
