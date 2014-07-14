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

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.domain.GeographicCoordinate;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.TownProperty;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Person.Sex;
import be.shad.tsqb.domain.people.PersonProperty;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.dto.StringToDateTransformer;
import be.shad.tsqb.dto.TownDetailsDto;
import be.shad.tsqb.exceptions.JoinException;
import be.shad.tsqb.ordering.OrderByProjection;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.restrictions.RestrictionsGroupFactory;
import be.shad.tsqb.restrictions.WhereRestrictions;
import be.shad.tsqb.values.CustomTypeSafeValue;

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

        validate(" from Person hobj1 where hobj1.age > :np1", 50);
    }
    
    /**
     * Select male married people
     */
    @Test
    public void testFilteringMore() {
        Person person = query.from(Person.class);
        
        query.where(person.isMarried()).  // type based checks available
                and(person.getSex()).eq(Sex.Male); // can chain restrictions

        validate(" from Person hobj1 where hobj1.married = :np1 and hobj1.sex = :np2", Boolean.TRUE, Sex.Male);
    }

    /**
     * Filter group (create where parts in brackets)
     */
    @Test
    public void testFilteringGroup() {
        RestrictionsGroupFactory rb = query.getGroupedRestrictionsBuilder();
        Person person = query.from(Person.class);
        
        query.where(person.isMarried()).and(
            rb.where(person.getName()).startsWith("Jef").or().startsWith("John")
        );

        validate(" from Person hobj1 where hobj1.married = :np1 and (hobj1.name like :np2 or hobj1.name like :np3)", 
                Boolean.TRUE, "Jef%", "John%");
    }
    
    /**
     * Filter group (create where parts in brackets)
     */
    @Test
    public void testFilteringGroupAlternative() {
        Person person = query.from(Person.class);
        
        RestrictionsGroup nameOrs = query.getGroupedRestrictionsBuilder().createRestrictionsGroup();
        nameOrs.where(person.getName()).startsWith("Jef").
                   or(person.getName()).startsWith("John");
        
        query.where(person.isMarried()).and(nameOrs);

        validate(" from Person hobj1 where hobj1.married = :np1 and (hobj1.name like :np2 or hobj1.name like :np3)", 
                Boolean.TRUE, "Jef%", "John%");
    }

    /**
     * Can build a query restriction entirely without chaining, using the 
     * restriction group factory to create all restrictions.
     */
    @Test
    public void testRestrictionsGroupFactory() {
        RestrictionsGroupFactory rb = query.getGroupedRestrictionsBuilder();
        Person person = query.from(Person.class);
        
        query.and(
            rb.where(person.getName()).startsWith("Jef"),
            rb.or(
                rb.and(
                    rb.where(person.getAge()).lt(10),
                    rb.where(person.getName()).startsWith("John")
                ),
                rb.and(
                    rb.where(person.getAge()).gt(20),
                    rb.where(person.getName()).startsWith("Emily")
                )
            )
        );

        validate(" from Person hobj1 where hobj1.name like :np1 and "
                + "((hobj1.age < :np2 and hobj1.name like :np3) or "
                + "(hobj1.age > :np4 and hobj1.name like :np5))", 
                "Jef%", 10, "John%", 20, "Emily%");
    }

    /**
     * It's also possible to mix the chaining and the combination approach
     */
    @Test
    public void testRestrictionsGroupFactoryAlternative() {
        RestrictionsGroupFactory rb = query.getGroupedRestrictionsBuilder();
        Person person = query.from(Person.class);

        query.where(person.getName()).startsWith("Jef");
        query.where(rb.or(
            rb.where(person.getAge()).lt(10).and(person.getName()).startsWith("John"),
            rb.where(person.getAge()).gt(20).and(person.getName()).startsWith("Emily")));

        validate(" from Person hobj1 where hobj1.name like :np1 and "
                + "((hobj1.age < :np2 and hobj1.name like :np3) or "
                + "(hobj1.age > :np4 and hobj1.name like :np5))", 
                "Jef%", 10, "John%", 20, "Emily%");
    }
    
    @Test
    public void testRestrictionsGroupFactoryAlternative2() {
        RestrictionsGroupFactory rb = query.getGroupedRestrictionsBuilder();
        Person person = query.from(Person.class);
        
//        RestrictionsGroup ageCheck = rb.or(
//            rb.where(person.getAge()).gt(80), 
//            rb.where(person.getAge()).lt(10));

        query.where(person.getName()).startsWith("G").and(
                rb.where(person.getAge()).gt(80).or(person.getAge()).lt(10));
        
        validate(" from Person hobj1 where hobj1.name like :np1 and "
                + "(hobj1.age > :np2 or hobj1.age < :np3)", "G%", 80, 10);
    }
    
    /**
     * Reuse a query and replace a captured restriction 
     */
    @Test
    public void testOngoingRestrictionsCapture() {
        List<String> names = Arrays.asList("Josh", "Emily");
        Person person = query.from(Person.class);

        OnGoingTextRestriction nameCheck = query.where(person.getName());
        
        nameCheck.in(names);
        validate(" from Person hobj1 where hobj1.name in (:np1)", names);

        nameCheck.isNull();
        validate(" from Person hobj1 where hobj1.name is null ");
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

        query.select(personSQ);
        query.select(person.isMarried());

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
        
        WhereRestrictions childJoin = query.joinWith(child);
        childJoin.where(child.getName()).eq("Bob");

        validate(" from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3 with hobj3.name = :np1", "Bob");
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

        validate(" from Person hobj1 where hobj1.age < :np1 and hobj1.name like :np2", 20, "Alex%");
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

        query.select(person);
        query.select(favoriteColorSQ);

        validate("select hobj1, (select hobj2.propertyValue from PersonProperty hobj2 where hobj1.id = hobj2.person.id and hobj2.propertyKey = :np1) from Person hobj1", 
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
        
        validate(" from Person hobj1 where (select hobj2.propertyValue from PersonProperty hobj2 where hobj1.id = hobj2.person.id and hobj2.propertyKey = :np1) = :np2",
                "FavColorKey", "Blue");
    }
    
    /**
     * JoinType.None omits the join from the hql query and
     * uses the nested property path instead. 
     */
    @Test
    public void testJoinTypeNone() {
        Relation relation = query.from(Relation.class);
        Person parent = query.join(relation.getParent(), JoinType.None);
        query.where(parent.getId()).eq(1L);
        
        validate(" from Relation hobj1 where hobj1.parent.id = :np1", 1L);
    }

    /**
     * When filtering a date between two dates, the continued ongoing date restriction allows
     * you to add an extra restriction using the left value of the previous restriction.
     * Just read the test code to understand...
     */
    @Test
    public void testContinuedOngoingDateRestriction() {
        Date date1 = DateUtils.addYears(new Date(), -5);
        Date date2 = DateUtils.addYears(new Date(), -3);
        
        Building building = query.from(Building.class);
        
        // construction date will be used for the 'after' and the 'before' date check.
        query.where(building.getConstructionDate()).after(date1).before(date2);

        validate(" from Building hobj1 where hobj1.constructionDate > :np1 and hobj1.constructionDate < :np2", date1, date2);
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
        
        validate(" from Relation hobj1 where hobj1.parent.id = :np1", 1L);
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
        
        validate(" from Relation hobj1 join hobj1.parent hobj2 where hobj2.id = :np1 and hobj2.name = :np2", 1L, "Iain");
    }
    
    @Test
    public void testSelectMaxAge() {
        Session session = getSessionFactory().getCurrentSession();
        Town town = new Town();
        town.setId(1L);
        town.setGeographicCoordinate(new GeographicCoordinate());
        town.getGeographicCoordinate().setLattitude(1d);
        town.getGeographicCoordinate().setLongitude(1d);
        session.save(town);
        
        Person person = new Person();
        person.setId(1L);
        person.setAge(50);
        person.setTown(town);
        session.save(person);
        
        Person personProxy = query.from(Person.class);
        
        PersonDto dto = query.select(PersonDto.class);
        dto.setPersonAge(query.hqlFunction().max(personProxy.getAge()).select());

        validate("select max(hobj1.age) as personAge from Person hobj1");
    }

    @Test
    public void testSelectCoalesce() {
        Person person = query.from(Person.class);
        
        PersonDto dto = query.select(PersonDto.class);
        dto.setThePersonsName(query.hqlFunction().coalesce(person.getName()).or("Bert").select());

        validate("select coalesce (hobj1.name,:np1) as thePersonsName from Person hobj1", "Bert");
    }

    @Test
    public void testGroupBy() {
        Person person = query.from(Person.class);
        query.select(person.getName());
        query.select(person.getAge());
        
        query.groupBy(person.getName());
        query.groupBy(person.getAge());

        validate("select hobj1.name, hobj1.age from Person hobj1 group by hobj1.name, hobj1.age");
    }

    @Test
    public void testOrderBy() {
        Person person = query.from(Person.class);
        query.select(person.getName());
        query.select(person.getAge());
        
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
        
        query.whereString(query.hqlFunction().upper(person.getName())).eq("TOM");

        validate(" from Person hobj1 where upper(hobj1.name) = :np1", "TOM");
    }

    @Test
    public void testImplicitJoin() {
        Relation relation = query.from(Relation.class);
        query.where(relation.getParent().getName()).startsWith("Sam"); // implicit join on parent

        validate(" from Relation hobj1 join hobj1.parent hobj2 where hobj2.name like :np1", "Sam%");
    }

    @Test
    public void testImplicitJoinIdentifierOnly() {
        Relation relation = query.from(Relation.class);
        query.where(relation.getParent().getId()).eq(1L); // implicit join on parent

        validate(" from Relation hobj1 where hobj1.parent.id = :np1", 1L);
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
    
    @Test
    public void testShowcaseSelectOptions() {
        Town town = query.from(Town.class);
        TownProperty dateProp = query.join(town.getProperties(), JoinType.Left);
        query.joinWith(dateProp).where(dateProp.getPropertyKey()).eq("LastUfoSpottingDate");
        
        TypeSafeSubQuery<Long> cntInhabitantsSQ = query.subquery(Long.class);
        Person inhabitant = cntInhabitantsSQ.from(Person.class);
        cntInhabitantsSQ.where(inhabitant.getTown().getId()).eq(town.getId());
        cntInhabitantsSQ.select(cntInhabitantsSQ.hqlFunction().count());
        
        TownDetailsDto dto = query.select(TownDetailsDto.class);
        
        // select a subselected value into a dto property
        dto.setInhabitants(cntInhabitantsSQ.select());
        
        // select an embeddable property value into a nested dto property:
        dto.getNestedDto().setLattitude(town.getGeographicCoordinate().getLattitude());
        
        // select the upper case name into the dto, this upper will be found in the query
        dto.setName(query.hqlFunction().upper(town.getName()).select());
        
        // select a value of a different type using a converter to convert the selected value, 
        // this will not be seen in the query and is only a post processor
        dto.setLastUfoSpottingDate(query.select(Date.class, dateProp.getPropertyValue(), 
                new StringToDateTransformer(DateFormat.getDateInstance())));
        
        // when no function is available and the value can't be retrieved another way it's still possible to just inject hql
        // it doesn't look pretty, and it isn't supposed to.. because you probably shouldn't do this!
        dto.setCustomString(new CustomTypeSafeValue<>(query, String.class, "'SomeCustomHql'").select());

        validate("select " + 
                 "(select count(*) from Person hobj3 where hobj3.town.id = hobj1.id) as inhabitants, " +
                 "hobj1.geographicCoordinate.lattitude as nestedDto_lattitude, " +
                 "upper(hobj1.name) as name, " +
                 "hobj2.propertyValue as lastUfoSpottingDate, " +
                 "'SomeCustomHql' as customString " +
                 "from Town hobj1 left join hobj1.properties hobj2 with hobj2.propertyKey = :np1", "LastUfoSpottingDate");
    }
    
}
