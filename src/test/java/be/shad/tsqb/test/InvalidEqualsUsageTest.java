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
import be.shad.tsqb.exceptions.EqualsNotAllowedException;

public class InvalidEqualsUsageTest extends TypeSafeQueryTest {

    @Test(expected=EqualsNotAllowedException.class)
    public void equalsNotAllowedOnRestrictionTest() {
        Person person = query.from(Person.class);
        query.where(person.getName()).equals("Hugo");
    }

    @Test(expected=EqualsNotAllowedException.class)
    public void equalsNotAllowedOnTypeSafeValue() {
        Person person = query.from(Person.class);
        query.where(query.toValue(person.getName()).equals("Hugo"));
    }
}
