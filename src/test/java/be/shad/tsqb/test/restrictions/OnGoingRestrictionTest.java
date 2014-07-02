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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.DirectTypeSafeValue;

public class OnGoingRestrictionTest extends TypeSafeQueryTest {
    private String NAMED_PARAM_1 = "NAMED_PARAM_1";

    @Test
    public void testIsNull() {
        Person person = query.from(Person.class);
        query.where(person.getName()).isNull();
        validate(" from Person hobj1 where hobj1.name is null ");
    }

    @Test
    public void testIsNotNull() {
        Person person = query.from(Person.class);
        query.where(person.getName()).isNotNull();
        validate(" from Person hobj1 where hobj1.name is not null ");
    }

    @Test
    public void testTypeSafeValueInCollection() {
        Person person = query.from(Person.class);
        List<String> names = Arrays.asList("Jos", "Marie", "Katrien");
        query.where(person.getName()).in(names);
        validate(" from Person hobj1 where hobj1.name in :np1", names);
    }

    @Test
    public void testTypeSafeValueInNamedCollection() {
        Person person = query.from(Person.class);
        query.where(person.getName()).in().named(NAMED_PARAM_1);

        List<String> names1 = Arrays.asList("Jos", "Marie", "Katrien");
        query.named().setValue(NAMED_PARAM_1, names1);
        validate(" from Person hobj1 where hobj1.name in :np1", names1);

        List<String> names2 = Arrays.asList("Marie", "Joseph");
        query.named().setValue(NAMED_PARAM_1, names2);
        validate(" from Person hobj1 where hobj1.name in :np1", names2);
    }
    
    @Test
    public void testTypeSafeValueInSubQuery() {
        Person person = query.from(Person.class);
        
        TypeSafeSubQuery<Number> subquery = query.subquery(Number.class);
        Relation relation = subquery.from(Relation.class);
        Person parent = subquery.join(relation.getParent(), JoinType.None);
        Person child = subquery.join(relation.getChild());
        subquery.where(child.isMarried()).isTrue();
        subquery.select(parent.getId());
        
        query.where(person.getId()).in(subquery);

        validate(" from Person hobj1 where hobj1.id in ("
                + "select hobj2.parent.id from Relation hobj2 "
                + "join hobj2.child hobj4 where hobj4.married = :np1"
                + ")", Boolean.TRUE);
    }

    @Test
    public void testNotIn() {
        Person person = query.from(Person.class);
        List<String> names = Arrays.asList("Jos", "Marie", "Katrien");
        query.where(person.getName()).notIn(names);
        validate(" from Person hobj1 where hobj1.name not in :np1", names);
    }

    @Test
    public void testNotInNamedCollection() {
        Person person = query.from(Person.class);
        query.where(person.getName()).notIn().named(NAMED_PARAM_1);

        List<String> names1 = Arrays.asList("Jos", "Marie", "Katrien");
        query.named().setValue(NAMED_PARAM_1, names1);
        validate(" from Person hobj1 where hobj1.name not in :np1", names1);

        List<String> names2 = Arrays.asList("Marie", "Joseph");
        query.named().setValue(NAMED_PARAM_1, names2);
        validate(" from Person hobj1 where hobj1.name not in :np1", names2);
    }

    @Test
    public void testTypeSafeValueNotIn() {
        Person person = query.from(Person.class);
        
        TypeSafeSubQuery<Number> subquery = query.subquery(Number.class);
        Relation relation = subquery.from(Relation.class);
        Person parent = subquery.join(relation.getParent(), JoinType.None);
        Person child = subquery.join(relation.getChild());
        subquery.where(child.isMarried()).isTrue();
        subquery.select(parent.getId());
        
        query.where(person.getId()).notIn(subquery);

        validate(" from Person hobj1 where hobj1.id not in ("
                + "select hobj2.parent.id from Relation hobj2 "
                + "join hobj2.child hobj4 where hobj4.married = :np1"
                + ")", Boolean.TRUE);
    }

    @Test
    public void testEq() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).eq(40);
        validate(" from Person hobj1 where hobj1.age = :np1", 40);
    }

    @Test
    public void testEqNamed() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).eq().named(NAMED_PARAM_1);
        
        query.named().setValue(NAMED_PARAM_1, 40);
        validate(" from Person hobj1 where hobj1.age = :np1", 40);
    }

    @Test
    public void testTypeSafeValueEq() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).eq(new DirectTypeSafeValue<Number>(query, 40));
        validate(" from Person hobj1 where hobj1.age = :np1", 40);
    }

    @Test
    public void testNot() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).not(40);
        validate(" from Person hobj1 where hobj1.age <> :np1", 40);
    }

    @Test
    public void testNotNamed() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).notEq().named(NAMED_PARAM_1);
        
        query.named().setValue(NAMED_PARAM_1, 40);
        validate(" from Person hobj1 where hobj1.age <> :np1", 40);
    }

    @Test
    public void testTypeSafeValueNot() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).notEq(new DirectTypeSafeValue<Number>(query, 40));
        validate(" from Person hobj1 where hobj1.age <> :np1", 40);
    }
    
}