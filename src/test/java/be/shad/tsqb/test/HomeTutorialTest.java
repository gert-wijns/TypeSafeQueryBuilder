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
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.dto.PersonDto;

@SuppressWarnings("unused")
public class HomeTutorialTest extends TypeSafeQueryTest {

    @Test
    public void testFrom() {
        Person person = query.from(Person.class);
        
        validate("from Person hobj1");
    }

    @Test
    public void testSelect() {
        Person person = query.from(Person.class);
        PersonDto personDto = query.select(PersonDto.class); // proxy instance of dto class
        personDto.setPersonAge(person.getAge());
        
        validate("select hobj1.age as personAge from Person hobj1");
    }
    
    @Test
    public void testWhere() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).gt(50);
        
        validate("from Person hobj1 where hobj1.age > :np1", 50);
    }
    
    @Test
    public void testJoin() {
        Person parent = query.from(Person.class);
        
        // join and obtain a proxy of a collection element
        Relation childRelation = query.join(parent.getChildRelations()); 
        // join implicitly, returns a proxy of the getter type
        Person child = childRelation.getChild(); 
        
        validate("from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3");
    }

    @Test
    public void testOrderBy() {
        Person person = query.from(Person.class);
        query.orderBy().desc(person.getName()).
                         asc(person.getAge());
        
        validate("from Person hobj1 order by hobj1.name desc, hobj1.age");
    }
    
    @Test
    public void testGroupBy() {
        Person person = query.from(Person.class);
        PersonDto personDto = query.select(PersonDto.class); // proxy instance of dto class
        personDto.setPersonAge(person.getAge());
        query.groupBy(person.getAge());
        
        validate("select hobj1.age as personAge from Person hobj1 group by hobj1.age");
    }
}
