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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import org.junit.Test;

import be.shad.tsqb.domain.House;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.test.TypeSafeQueryTest;

public class OnGoingBooleanRestrictionTest extends TypeSafeQueryTest {
    private static final String NAMED_PARAM_1 = "NAMED_PARAM_1";

    @Test
    public void testIsFalse() {
        Person person = query.from(Person.class);
        query.where(person.isMarried()).isFalse();
        validate(" from Person hobj1 where hobj1.married = :np1", FALSE);
    }

    @Test
    public void testIsTrue() {
        Person person = query.from(Person.class);
        query.where(person.isMarried()).isTrue();
        validate(" from Person hobj1 where hobj1.married = :np1", TRUE);
    }

    @Test
    public void testAndAfterEq() {
        House house = query.from(House.class);
        query.where(house.isOccupied()).eq(FALSE).and(house.isOccupied()).isFalse();
        validate(" from House hobj1 where hobj1.occupied = :np1 and hobj1.occupied = :np2", FALSE, FALSE);
    }

    @Test
    public void testIsNamed() {
        Person person = query.from(Person.class);
        query.where(person.isMarried()).isNamed(NAMED_PARAM_1);

        query.named().setValue(NAMED_PARAM_1, TRUE);
        validate(" from Person hobj1 where hobj1.married = :np1", TRUE);

        query.named().setValue(NAMED_PARAM_1, FALSE);
        validate(" from Person hobj1 where hobj1.married = :np1", FALSE);
    }

}