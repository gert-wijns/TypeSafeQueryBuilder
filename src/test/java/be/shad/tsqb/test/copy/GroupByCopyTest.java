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


public class GroupByCopyTest extends TypeSafeQueryCopyTest {

    /**
     * Test group bys are copied and the group bys on the original
     * are not influenced by the group bys on the copy.
     */
    @Test
    public void testCopyGroupBy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        query.select(query.groupBy(personProxy.getName()));

        Person personProxyCopy = validateAndCopy(PERSON_OBJ,
                "select hobj1.name from Person hobj1 group by hobj1.name");

        copy.select(copy.groupBy(personProxyCopy.getAge()));
        validateChangedCopy("select hobj1.name, hobj1.age "
                + "from Person hobj1 "
                + "group by hobj1.name, hobj1.age");

        query.select(query.groupBy(personProxy.getId()));
        validateChangedOriginal(
                "select hobj1.name, hobj1.id "
                + "from Person hobj1 "
                + "group by hobj1.name, hobj1.id");
    }

}
