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

import static be.shad.tsqb.restrictions.predicate.RestrictionPredicate.IGNORE_NULL;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.DirectTypeSafeValue;

public class OnGoingNumberRestrictionTest extends TypeSafeQueryTest {
    private String NAMED_PARAM_1 = "NAMED_PARAM_1";

    @Test
    public void testLt() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).lt(40);
        validate(" from Person hobj1 where hobj1.age < :np1", 40);
    }
    
    @Test
    public void testLtIgnored() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).lt(null, IGNORE_NULL);
        validate(" from Person hobj1");
    }

    @Test
    public void testLtNamed() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).lt().named(NAMED_PARAM_1);
        
        query.named().setValue(NAMED_PARAM_1, 40);
        validate(" from Person hobj1 where hobj1.age < :np1", 40);
    }

    @Test
    public void testTypeSafeValueLt() {
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).lt(new DirectTypeSafeValue<Number>(query, 40));

        validate(" from Person hobj1 where hobj1.age < :np1", 40);
    }

    @Test
    public void testGt() {
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).gt(40);

        validate(" from Person hobj1 where hobj1.age > :np1", 40);
    }
    
    @Test
    public void testGtIgnored() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).gt(null, IGNORE_NULL);
        validate(" from Person hobj1");
    }
    
    @Test
    public void testGtNamed() {
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).gt().named(NAMED_PARAM_1);

        query.named().setValue(NAMED_PARAM_1, 40);
        validate(" from Person hobj1 where hobj1.age > :np1", 40);
    }

    @Test
    public void testTypeSafeValueGt() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).gt(new DirectTypeSafeValue<Number>(query, 40));
        validate(" from Person hobj1 where hobj1.age > :np1", 40);
    }

    @Test
    public void testLte() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).lte(40);
        validate(" from Person hobj1 where hobj1.age <= :np1", 40);
    }
    
    @Test
    public void testLteIgnored() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).lte(null, IGNORE_NULL);
        validate(" from Person hobj1");
    }

    @Test
    public void testLteNamed() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).lte().named(NAMED_PARAM_1);

        query.named().setValue(NAMED_PARAM_1, 40);
        validate(" from Person hobj1 where hobj1.age <= :np1", 40);
    }
    
    @Test
    public void testNgt() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).ngt(40);
        validate(" from Person hobj1 where hobj1.age <= :np1", 40);
    }
    
    @Test
    public void testNgtIgnored() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).ngt(null, IGNORE_NULL);
        validate(" from Person hobj1");
    }

    @Test
    public void testNgtNamed() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).ngt().named(NAMED_PARAM_1);

        query.named().setValue(NAMED_PARAM_1, 40);
        validate(" from Person hobj1 where hobj1.age <= :np1", 40);
    }

    @Test
    public void testTypeSafeValueLte() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).lte(new DirectTypeSafeValue<Number>(query, 40));
        validate(" from Person hobj1 where hobj1.age <= :np1", 40);
    }

    @Test
    public void testGte() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).gte(40);
        validate(" from Person hobj1 where hobj1.age >= :np1", 40);
    }
    
    @Test
    public void testGteIgnored() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).gte(null, IGNORE_NULL);
        validate(" from Person hobj1");
    }
    
    @Test
    public void testGteNamed() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).gte().named(NAMED_PARAM_1);

        query.named().setValue(NAMED_PARAM_1, 40);
        validate(" from Person hobj1 where hobj1.age >= :np1", 40);
    }
    
    @Test
    public void testNlt() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).nlt(40);
        validate(" from Person hobj1 where hobj1.age >= :np1", 40);
    }
    
    @Test
    public void testNltIgnored() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).nlt(null, IGNORE_NULL);
        validate(" from Person hobj1");
    }
    
    @Test
    public void testNltNamed() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).nlt().named(NAMED_PARAM_1);

        query.named().setValue(NAMED_PARAM_1, 40);
        validate(" from Person hobj1 where hobj1.age >= :np1", 40);
    }
    
    @Test
    public void testTypeSafeValueGte() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).gte(new DirectTypeSafeValue<Number>(query, 40));
        validate(" from Person hobj1 where hobj1.age >= :np1", 40);
    }

}