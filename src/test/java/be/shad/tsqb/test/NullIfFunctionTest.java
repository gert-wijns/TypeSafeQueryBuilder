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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.values.TypeSafeValueFunctions;

/**
 * @author Gert
 *
 */
public class NullIfFunctionTest extends TypeSafeQueryTest {

    @Test
    public void testNullifEqual() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestPerson(creator.createTestTown(), "Josh");

        TypeSafeValueFunctions fun = query.hqlFunction();

        Person personPx = query.from(Person.class);
        query.selectValue(fun.nullIf(personPx.getName()).equalTo("Josh"));

        validate("select nullif (hobj1.name,:np1) from Person hobj1", "Josh");
        assertNull(doQueryResult.get(0));
    }

    @Test
    public void testFirstWhenNotEqual() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestPerson(creator.createTestTown(), "Josh");

        TypeSafeValueFunctions fun = query.hqlFunction();

        Person personPx = query.from(Person.class);
        query.selectValue(fun.nullIf(personPx.getName()).equalTo("Emma"));

        validate("select nullif (hobj1.name,:np1) from Person hobj1", "Emma");
        assertEquals("Josh", doQueryResult.get(0));
    }

    @Test
    public void testFirstWhenNotEqualDirectValue() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestPerson(creator.createTestTown(), "Josh");

        TypeSafeValueFunctions fun = query.hqlFunction();

        Person personPx = query.from(Person.class);
        query.selectValue(fun.nullIf("Emma").equalTo(personPx.getName()));

        validate("select nullif ('Emma',hobj1.name) from Person hobj1");
        assertEquals("Emma", doQueryResult.get(0));
    }
}
