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
import be.shad.tsqb.selection.parallel.SelectValue;


public class OrderByCopyTest extends TypeSafeQueryCopyTest {

    /**
     * Test order bys are copied and the order bys on the original
     * are not influenced by the order bys on the copy.
     */
    @Test
    public void testCopyOrderBy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        query.orderBy().desc(personProxy.getName());

        Person personProxyCopy = validateAndCopy(PERSON_OBJ,
                " from Person hobj1 "
                + "order by hobj1.name desc");

        copy.orderBy().desc(personProxyCopy.getAge());
        validateChangedCopy(" from Person hobj1 "
                + "order by hobj1.name desc, hobj1.age desc");

        query.orderBy().desc(personProxy.getId());
        validateChangedOriginal(" from Person hobj1 "
                + "order by hobj1.name desc, hobj1.id desc");
    }

    @Test
    public void testCopyOrderByProjectedValue() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);

        @SuppressWarnings("unchecked")
        SelectValue<String> name = query.select(SelectValue.class);
        name.setValue(personProxy.getName());
        query.orderBy().desc(name.getValue());

        Person personProxyCopy = validateAndCopy(PERSON_OBJ,
                "select hobj1.name as value from Person hobj1 "
                + "order by hobj1.name desc");

        copy.orderBy().asc(personProxyCopy.getSex());
        validateChangedCopy(
                "select hobj1.name as value from Person hobj1 "
                + "order by hobj1.name desc, hobj1.sex");
    }

}
