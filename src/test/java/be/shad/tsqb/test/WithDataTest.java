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
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.values.NullTypeSafeValue;

public class WithDataTest extends TypeSafeQueryTest {

    @Test
    public void testFindPerson() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestPerson(creator.createTestTown(), "Josh");

        Person fromProxy = query.from(Person.class);
        PersonDto selectProxy = query.select(PersonDto.class);
        selectProxy.setId(fromProxy.getId());
        validate("select hobj1.id as id from Person hobj1");
    }

    @Test
    public void testSelectNull() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestPerson(creator.createTestTown(), "Josh");

        Person personProxy = query.from(Person.class);
        PersonDto selectProxy = query.select(PersonDto.class);
        selectProxy.setId(new NullTypeSafeValue<>(query, Long.class).select());
        selectProxy.setThePersonsName(personProxy.getName());
        validate("select NULLIF(1,1) as id, hobj1.name as thePersonsName from Person hobj1");
        assertEquals(1, doQueryResult.size());
        PersonDto result = (PersonDto) doQueryResult.get(0);
        assertNull(result.getId());
        assertEquals("Josh", result.getThePersonsName());
    }

}
