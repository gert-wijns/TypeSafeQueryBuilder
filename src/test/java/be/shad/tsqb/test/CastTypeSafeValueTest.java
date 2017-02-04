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

import be.shad.tsqb.domain.Product;
import be.shad.tsqb.domain.people.Person;

public class CastTypeSafeValueTest extends TypeSafeQueryTest {

    /**
     * Cast a value of the select statement.
     */
    @Test
    public void testCastInSelect() {
        Person person = query.from(Person.class);

        query.selectValue(query.hqlFunction().cast(person.getAge(), String.class));

        validate("select cast(hobj1.age as string) from Person hobj1");
    }

    /**
     * Cast a value of the where statement.
     */
    @Test
    public void testCastInWhere() {
        Person person = query.from(Person.class);

        query.whereString(query.hqlFunction().cast(person.getAge(), String.class)).startsWith("10");

        validate(" from Person hobj1 where cast(hobj1.age as string) like '10%'");
    }

    /**
     * Cast a value of the select statement.
     */
    @Test
    public void testCastNumberSubclassCompiles() {
        Product product = query.from(Product.class);

        query.whereNumber(query.hqlFunction().cast(product.getManyProperties().getProperty1(), Long.class)).lt(10.0d);

        validate(" from Product hobj1 where cast(hobj1.manyProperties.property1 as long) < 10.0");
    }

}
