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

import org.junit.Before;
import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.arithmetic.ArithmeticTypeSafeValueFactory;

public class ArithmeticOperationsTest extends TypeSafeQueryTest {

    private ArithmeticTypeSafeValueFactory arithmetics;

    @Before
    public void setup() {
        arithmetics = query.getArithmeticsBuilder();
    }

    @Test
    public void testAddDirectValue() {
        Person person = query.from(Person.class);
        query.selectValue(arithmetics.value(person.getId()).add(10d));
        validate("select (hobj1.id + 10.0) from Person hobj1");
    }

    @Test
    public void testAddReferencedValue() {
        Person person = query.from(Person.class);
        query.selectValue(arithmetics.value(person.getId()).add(person.getAge()));
        validate("select (hobj1.id + hobj1.age) from Person hobj1");
    }

    @Test
    public void testSubtractDirectValue() {
        Person person = query.from(Person.class);
        query.selectValue(arithmetics.value(person.getId()).subtract(10d));
        validate("select (hobj1.id - 10.0) from Person hobj1");
    }

    @Test
    public void testSubtractReferencedValue() {
        Person person = query.from(Person.class);
        query.selectValue(arithmetics.value(person.getId()).subtract(person.getAge()));
        validate("select (hobj1.id - hobj1.age) from Person hobj1");
    }

    @Test
    public void testMultiplyDirectValue() {
        Person person = query.from(Person.class);
        query.selectValue(arithmetics.value(person.getId()).multiply(10d));
        validate("select (hobj1.id * 10.0) from Person hobj1");
    }

    @Test
    public void testMultiplyReferencedValue() {
        Person person = query.from(Person.class);
        query.selectValue(arithmetics.value(person.getId()).multiply(person.getAge()));
        validate("select (hobj1.id * hobj1.age) from Person hobj1");
    }

    @Test
    public void testDivideDirectValue() {
        Person person = query.from(Person.class);
        query.selectValue(arithmetics.value(person.getId()).divide(10d));
        validate("select (hobj1.id / 10.0) from Person hobj1");
    }

    @Test
    public void testDivideReferencedValue() {
        Person person = query.from(Person.class);
        query.selectValue(arithmetics.value(person.getId()).divide(person.getAge()));
        validate("select (hobj1.id / hobj1.age) from Person hobj1");
    }

    @Test
    public void testWrappedDenominatorDivide() {
        Person person = query.from(Person.class);

        query.selectValue(
            arithmetics.divide(
                arithmetics.value(person.getId()),
                arithmetics.value(10d).subtract(person.getAge())
        ));

        validate("select (hobj1.id / (10.0 - hobj1.age)) from Person hobj1");
    }

    @Test
    public void testaArithmeticsWithoutChaining() {
        Person person = query.from(Person.class);

        query.selectValue(
            arithmetics.add(
                arithmetics.value(person.getId()),
                arithmetics.divide(
                    arithmetics.value(1D),
                    arithmetics.subtract(
                        arithmetics.value(10d),
                        arithmetics.value(person.getAge()))),
                arithmetics.value(person.getId())
        ));

        validate("select (hobj1.id + (1.0 / (10.0 - hobj1.age)) + hobj1.id) from Person hobj1");
    }

    @Test
    public void testaArithmeticsWithChaining() {
        Person person = query.from(Person.class);

        query.selectValue(
            arithmetics.value(person.getId()).
            add(arithmetics.value(1D).divide(
                arithmetics.value(10d).subtract(person.getAge()))).
            add(arithmetics.value(person.getId())
        ));

        validate("select (hobj1.id + (1.0 / (10.0 - hobj1.age)) + hobj1.id) from Person hobj1");
    }

    @Test
    public void testaArithmeticsWithSubqueryValue() {
        Person person = query.from(Person.class);

        TypeSafeSubQuery<Integer> childrenSQ = query.subquery(Integer.class);
        Person child = childrenSQ.from(Person.class);
        Relation relations = childrenSQ.join(child.getParentRelations());
        childrenSQ.where(relations.getParent().getId()).eq(person.getId());
        childrenSQ.select(query.hqlFunction().max(child.getAge()));

        query.selectValue(
            arithmetics.value(person.getId()).
            add(arithmetics.value(1D).divide(
                arithmetics.value(childrenSQ).subtract(person.getAge()))).
            add(arithmetics.value(person.getId())
        ));

        String expectedSQHql = "(select max(hobj2.age) from Person hobj2 join hobj2.parentRelations hobj3 where hobj3.parent.id = hobj1.id)";
        validate("select (hobj1.id + (1.0 / (" + expectedSQHql + " - hobj1.age)) + hobj1.id) from Person hobj1");

    }
}
