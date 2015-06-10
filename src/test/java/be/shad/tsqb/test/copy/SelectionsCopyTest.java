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
package be.shad.tsqb.test.copy;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.dto.PersonDto;


public class SelectionsCopyTest extends TypeSafeQueryCopyTest {

    @Test
    public void testCopySelect() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);

        PersonDto personDtoProxy = query.select(PersonDto.class);
        personDtoProxy.setId(personProxy.getId());

        Person personProxyCopy = validateAndCopy(PERSON_OBJ,
                "select hobj1.id as id from Person hobj1");

        PersonDto personDtoProxyCopy = copy.select(PersonDto.class);
        personDtoProxyCopy.setPersonAge(personProxyCopy.getAge());
        validateChangedCopy("select hobj1.id as id, hobj1.age as personAge from Person hobj1");

        personDtoProxy.setThePersonsName(personProxy.getName());
        validateChangedOriginal("select hobj1.id as id, hobj1.name as thePersonsName from Person hobj1");
    }

}
