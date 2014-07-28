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
import static org.junit.Assert.fail;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.selection.parallel.SelectValue;
import be.shad.tsqb.selection.parallel.SelectionMerger1;

public class ParallelSelectionMergerTest extends TypeSafeQueryTest {

    @Test
    public void testSubselectValueMergerIsCalledAfterOtherSetters() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();
        creator.createTestPerson(town, "JohnyTheKid");
        creator.createTestPerson(town, "Josh");

        Person personProxy = query.from(Person.class);

        PersonDto personDtoProxy = query.select(PersonDto.class);
        personDtoProxy.setId(personProxy.getId());
        personDtoProxy.setPersonAge(personProxy.getAge());

        final MutableInt counter = new MutableInt();
        SelectValue<String> subselectProxy = query.selectMergeValues(personDtoProxy,
                new SelectionMerger1<PersonDto, String>() {
            @Override
            public void mergeValueIntoResult(PersonDto partialResult, String personName) {
                if (partialResult.getId() == null) {
                    fail("The ID property should be set before the value is merged into the dto.");
                }
                counter.increment();
            }
        });
        subselectProxy.setValue(personProxy.getName());

        validate("select hobj1.id as id, hobj1.age as personAge, hobj1.name as g1__value from Person hobj1");
        assertEquals("The merger should've been called for each row, expected 2 rows.", 2, counter.getValue().intValue());
    }

    /**
     * Test selecting the same property path in the same query multiple times
     * doesn't generate overlapping aliases.
     */
    @Test
    public void testMultipleSubselectValueMergersWithSameSubpath() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();
        creator.createTestPerson(town, "JohnyTheKid");
        creator.createTestPerson(town, "Josh");

        Person personProxy = query.from(Person.class);

        PersonDto personDtoProxy = query.select(PersonDto.class);
        personDtoProxy.setId(personProxy.getId());
        personDtoProxy.setPersonAge(personProxy.getAge());

        final MutableInt counter = new MutableInt();
        SelectValue<String> subselectProxy = query.selectMergeValues(personDtoProxy,
                new SelectionMerger1<PersonDto, String>() {
            @Override
            public void mergeValueIntoResult(PersonDto partialResult, String personName) {
                if (partialResult.getId() == null) {
                    fail("The ID property should be set before the value is merged into the dto.");
                }
                counter.increment();
            }
        });
        subselectProxy.setValue(personProxy.getName());

        final MutableInt counter2 = new MutableInt();
        SelectValue<Integer> subselectProxy2 = query.selectMergeValues(personDtoProxy,
                new SelectionMerger1<PersonDto, Integer>() {
            @Override
            public void mergeValueIntoResult(PersonDto partialResult, Integer personAge) {
                if (partialResult.getId() == null) {
                    fail("The ID property should be set before the value is merged into the dto.");
                }
                counter2.increment();
            }
        });
        subselectProxy2.setValue(personProxy.getAge());

        validate("select hobj1.id as id, hobj1.age as personAge, hobj1.name as g1__value, hobj1.age as g2__value from Person hobj1");
        assertEquals("The merger should've been called for each row, expected 2 rows.", 2, counter.getValue().intValue());
        assertEquals("The merger should've been called for each row, expected 2 rows.", 2, counter2.getValue().intValue());
    }

}
