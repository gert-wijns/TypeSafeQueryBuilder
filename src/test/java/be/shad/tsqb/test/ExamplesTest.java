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

import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Person.Sex;
import be.shad.tsqb.domain.people.PersonProperty;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.exceptions.JoinException;
import be.shad.tsqb.joins.TypeSafeQueryJoin;
import be.shad.tsqb.ordering.OrderByProjection;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.restrictions.RestrictionsGroup;

public class ExamplesTest extends TypeSafeQueryTest {

    /**
     * Select people
     */
    @Test
    @SuppressWarnings("unused")
    public void testObtainQuery() {
        Person person = query.from(Person.class);
        
        validate(" from Person hobj1");
    }
    
    /**
     * Select people over 50.
     */
    @Test
    public void testFiltering() {
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).gt(50);

        validate(" from Person hobj1 where hobj1.age > ?", 50);
    }
    
    /**
     * Select male married people
     */
    @Test
    public void testFilteringMore() {
        Person person = query.from(Person.class);
        
        query.where(person.isMarried()).  // type based checks available
                and(person.getSex()).eq(Sex.Male); // can chain restrictions

        validate(" from Person hobj1 where hobj1.married = ? and hobj1.sex = ?", Boolean.TRUE, Sex.Male);
    }
    
    /**
     * Filter group (create where parts in brackets)
     */
    @Test
    public void testFilteringGroup() {
        Person person = query.from(Person.class);
        
        RestrictionsGroup nameOrs = query.whereGroup();
        nameOrs.where(person.getName()).startsWith("Jef").
                   or(person.getName()).startsWith("John");
        
        query.where(person.isMarried()).and(nameOrs);

        validate(" from Person hobj1 where hobj1.married = ? and (hobj1.name like ? or hobj1.name like ?)", 
                Boolean.TRUE, "Jef%", "John%");
    }

    /**
     * Selecting into a dto by creating a proxy and
     * setting the fields
     */
    @Test
    public void testSelectFieldsIntoDto() {
        Person person = query.from(Person.class);
        
        PersonDto personDto = query.select(PersonDto.class); // proxy instance of dto class
        personDto.setPersonAge(person.getAge());
        personDto.setThePersonsName(person.getName());

        validate("select hobj1.age as personAge, hobj1.name as thePersonsName from Person hobj1");
    }

    @Test
    public void testSelectValues() {
        Person person = query.from(Person.class);
        
        TypeSafeSubQuery<String> personSQ = query.subquery(String.class);
        Person personSub = personSQ.from(Person.class);
        personSQ.where(person.getId()).eq(personSub.getId());
        personSQ.select(personSub.getName());

        query.selectValue(personSQ);
        query.selectValue(person.isMarried());

        validate("select (select hobj2.name from Person hobj2 where hobj1.id = hobj2.id), hobj1.married from Person hobj1");
    }
    
    @Test
    @SuppressWarnings("unused")
    public void testJoin() {
        Person parent = query.from(Person.class);
        
        Relation childRelation = query.join(parent.getChildRelations());
        Person child = childRelation.getChild();
        
        validate(" from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3");
    }

    @Test
    @SuppressWarnings("unused")
    public void testJoinLeftFetch() {
        Person parent = query.from(Person.class);
        
        Relation childRelation = query.join(parent.getChildRelations(), JoinType.LeftFetch);

        validate(" from Person hobj1 left join fetch hobj1.childRelations hobj2");
    }
    
    @Test
    public void testJoinWith() {
        Person parent = query.from(Person.class);
        
        Relation childRelation = query.join(parent.getChildRelations());
        Person child = query.join(childRelation.getChild());
        
        TypeSafeQueryJoin<Person> childJoin = query.getJoin(child);
        childJoin.with(child.getName()).eq("Bob");

        validate(" from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3 with hobj3.name = ?", "Bob");
    }

    @Test
    public void testMultiFrom() {
        Person parent1 = query.from(Person.class);
        Person parent2 = query.from(Person.class);
        
        query.where(parent1.getName()).eq(parent2.getName());

        validate(" from Person hobj1, Person hobj2 where hobj1.name = hobj2.name");
    }

    @Test
    public void testRestrictionChaining() {
        Person person = query.from(Person.class);

        query.where(person.getAge()).lt(20).
                and(person.getName()).startsWith("Alex");

        validate(" from Person hobj1 where hobj1.age < ? and hobj1.name like ?", 20, "Alex%");
    }

    @Test
    public void testSelectWithSubQuery() {
        Person person = query.from(Person.class);

        TypeSafeSubQuery<String> favoriteColorSQ = query.subquery(String.class);
        PersonProperty favColor = favoriteColorSQ.from(PersonProperty.class);
        Person personSQ = favoriteColorSQ.join(favColor.getPerson(), JoinType.None); // see comment above code block

        favoriteColorSQ.select(favColor.getPropertyValue());
        favoriteColorSQ.where(person.getId()).eq(personSQ.getId()).
                          and(favColor.getPropertyKey()).eq("FavColorKey");

        query.selectValue(person);
        query.selectValue(favoriteColorSQ);

        validate("select hobj1, (select hobj2.propertyValue from PersonProperty hobj2 where hobj1.id = hobj2.person.id and hobj2.propertyKey = ?) from Person hobj1", 
                "FavColorKey");
    }

    @Test
    public void testRestrictWithSubQuery() {
        Person person = query.from(Person.class);

        TypeSafeSubQuery<String> favoriteColorSQ = query.subquery(String.class);
        PersonProperty favColor = favoriteColorSQ.from(PersonProperty.class);
        Person personSQ = favoriteColorSQ.join(favColor.getPerson(), JoinType.None); // see comment above code block

        favoriteColorSQ.select(favColor.getPropertyValue());
        favoriteColorSQ.where(person.getId()).eq(personSQ.getId()).
                          and(favColor.getPropertyKey()).eq("FavColorKey");

        query.whereString(favoriteColorSQ).eq("Blue");
        
        validate(" from Person hobj1 where (select hobj2.propertyValue from PersonProperty hobj2 where hobj1.id = hobj2.person.id and hobj2.propertyKey = ?) = ?",
                "FavColorKey", "Blue");
    }
    
    @Test
    public void testJoinTypeNone() {
        Relation relation = query.from(Relation.class);
        Person parent = query.join(relation.getParent(), JoinType.None);
        query.where(parent.getId()).eq(1L);
        
        validate(" from Relation hobj1 where hobj1.parent.id = ?", 1L);
    }

    /**
     * When only the identifier property (retrieved from the hibernate meta data) is used,
     * and the join type was not explicitly set by the user, then the 
     * JoinType.None is automatically used.
     */
    @Test
    public void testJoinTypeNoneByDefaultWhenOnlyIdentifierPropertyIsUsed() {
        Relation relation = query.from(Relation.class);
        query.where(relation.getParent().getId()).eq(1L);
        
        validate(" from Relation hobj1 where hobj1.parent.id = ?", 1L);
    }

    /**
     * More than the identifier property is used, so the join type defaults to
     * inner in case it was not set explicitly.
     */
    @Test
    public void testJoinTypeInnerByDefaultWhenNonIdentifierPropertyIsUsed() {
        Relation relation = query.from(Relation.class);
        query.where(relation.getParent().getId()).eq(1L);
        query.where(relation.getParent().getName()).eq("Iain");
        
        validate(" from Relation hobj1 join hobj1.parent hobj2 where hobj2.id = ? and hobj2.name = ?", 1L, "Iain");
    }
    
    @Test
    public void testSelectMaxAge() {
        Person person = query.from(Person.class);
        
        PersonDto dto = query.select(PersonDto.class);
        dto.setPersonAge(query.function().max(person.getAge()).select());

        validate("select max(hobj1.age) as personAge from Person hobj1");
    }

    @Test
    public void testSelectCoalesce() {
        Person person = query.from(Person.class);
        
        PersonDto dto = query.select(PersonDto.class);
        dto.setThePersonsName(query.function().coalesce(person.getName()).or("Bert").select());

        validate("select coalesce (hobj1.name,?) as thePersonsName from Person hobj1", "Bert");
    }

    @Test
    public void testGroupBy() {
        Person person = query.from(Person.class);
        query.selectValue(person.getName());
        query.selectValue(person.getAge());
        
        query.groupBy(person.getName()).
              and(person.getAge());

        validate("select hobj1.name, hobj1.age from Person hobj1 group by hobj1.name, hobj1.age");
    }

    @Test
    public void testOrderBy() {
        Person person = query.from(Person.class);
        query.selectValue(person.getName());
        query.selectValue(person.getAge());
        
        query.orderBy().desc(person.getName()).
                         asc(person.getAge());

        validate("select hobj1.name, hobj1.age from Person hobj1 order by hobj1.name desc, hobj1.age");
    }
    
    @Test
    public void testOrderByProjection() {
        Person person = query.from(Person.class);
        
        PersonDto dto = query.select(PersonDto.class);
        dto.setThePersonsName(person.getName());

        query.orderBy().by(new OrderByProjection(query, "thePersonsName", true));

        validate("select hobj1.name as thePersonsName from Person hobj1 order by hobj1.name desc");
    }

    @Test
    public void testUpperFunction() {
        Person person = query.from(Person.class);
        
        query.whereString(query.function().upper(person.getName())).eq("TOM");

        validate(" from Person hobj1 where upper(hobj1.name) = ?", "TOM");
    }

    @Test
    public void testImplicitJoin() {
        Relation relation = query.from(Relation.class);
        query.where(relation.getParent().getName()).startsWith("Sam"); // implicit join on parent

        validate(" from Relation hobj1 join hobj1.parent hobj2 where hobj2.name like ?", "Sam%");
    }

    @Test
    public void testImplicitJoinIdentifierOnly() {
        Relation relation = query.from(Relation.class);
        query.where(relation.getParent().getId()).eq(1L); // implicit join on parent

        validate(" from Relation hobj1 where hobj1.parent.id = ?", 1L);
    }

    @Test
    public void testMultiFromIdentifierOnlyShouldntOmmit() {
        Person person1 = query.from(Person.class);
        Person person2 = query.from(Person.class);
        query.where(person1.getId()).eq(person2.getId());

        validate(" from Person hobj1, Person hobj2 where hobj1.id = hobj2.id");
    }
    
    @Test(expected=JoinException.class)
    public void testJoinCompositeTypeShouldFail() {
        Town town = query.from(Town.class);
        query.join(town.getGeographicCoordinate());
    }
}
