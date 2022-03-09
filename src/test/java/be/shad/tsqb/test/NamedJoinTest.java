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

import static be.shad.tsqb.joins.JoinParams.defaultJoin;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.query.ClassJoinType;

public class NamedJoinTest extends TypeSafeQueryTest {

    @Test(expected=IllegalArgumentException.class)
    public void joinNamedWithBlankNameNotAllowed() {
        Person personProxy = query.from(Person.class);
        query.join(personProxy.getTown(), defaultJoin().name("   ").build());
    }

    @Test
    public void joinNamedTest() {
        Person personProxy = query.from(Person.class);
        query.join(personProxy.getTown(), defaultJoin().name("theTown").build());

        Town town = query.named().get("theTown");
        assertNotNull(town);
    }

    @Test
    public void namedClassJoinTest() {
        Person personProxy = query.from(Person.class);
        query.join(personProxy, Town.class, ClassJoinType.Inner, "theTown");

        Town town = query.named().get("theTown");
        assertNotNull(town);
    }
}
