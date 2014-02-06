package be.shad.tsqb.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Person.Sex;
import be.shad.tsqb.domain.people.PersonProperty;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.joins.TypeSafeQueryJoin;
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
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1"));
    }
    
    /**
     * Select people over 50.
     */
    @Test
    public void testFiltering() {
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).gt(50);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age > ?"));
        assertTrue(Arrays.asList(hql.getParams()).equals(Arrays.asList(50)));
    }
    
    /**
     * Select male married people
     */
    @Test
    public void testFilteringMore() {
        Person person = query.from(Person.class);
        
        query.where(person.isMarried()).isTrue().  // type based checks available
                and(person.getSex()).eq(Sex.Male); // can chain restrictions

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.married = ? and hobj1.sex = ?"));
        assertTrue(Arrays.asList(hql.getParams()).equals(Arrays.asList(Boolean.TRUE, Sex.Male)));
    }
    
    /**
     * Filter group (create where parts in brackets)
     */
    @Test
    public void testFilteringGroup() {
        Person person = query.from(Person.class);
        
        query.where(person.isMarried()).isTrue().
            and(RestrictionsGroup.group(query).
                 and(person.getName()).startsWith("Jef").
                 or(person.getName()).startsWith("John"));

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.married = ? and (hobj1.name like ? or hobj1.name like ?)"));
        assertTrue(Arrays.asList(hql.getParams()).equals(Arrays.asList(Boolean.TRUE, "Jef%", "John%")));
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

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals("select hobj1.age as personAge, hobj1.name as thePersonsName from Person hobj1"));
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

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals("select (select hobj2.name from Person hobj2 where hobj1.id = hobj2.id), hobj1.married from Person hobj1"));
    }
    
    @Test
    @SuppressWarnings("unused")
    public void testJoin() {
        Person parent = query.from(Person.class);
        
        Relation childRelation = query.join(parent.getChildRelations());
        Person child = query.join(childRelation.getChild());
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3"));
    }

    @Test
    @SuppressWarnings("unused")
    public void testJoinLeftFetch() {
        Person parent = query.from(Person.class);
        
        Relation childRelation = query.join(parent.getChildRelations(), JoinType.LeftFetch);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 left join fetch hobj1.childRelations hobj2"));
    }
    
    @Test
    public void testJoinWith() {
        Person parent = query.from(Person.class);
        
        Relation childRelation = query.join(parent.getChildRelations());
        Person child = query.join(childRelation.getChild());
        
        TypeSafeQueryJoin<Person> childJoin = query.getJoin(child);
        childJoin.with(child.getName()).eq("Bob");

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3 with hobj3.name = ?"));
        assertTrue(Arrays.asList(hql.getParams()).equals(Arrays.asList("Bob")));
    }

    @Test
    public void testMultiFrom() {
        Person parent = query.from(Person.class);
        
        Relation childRelation = query.join(parent.getChildRelations());
        Person child = query.join(childRelation.getChild());
        
        query.where(child.getName()).eq(parent.getName());

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3 where hobj3.name = hobj1.name"));
    }

    @Test
    public void testRestrictionChaining() {
        Person person = query.from(Person.class);

        query.where(person.getAge()).lt(20).
                and(person.getName()).startsWith("Alex");

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age < ? and hobj1.name like ?"));
        assertTrue(Arrays.asList(hql.getParams()).equals(Arrays.asList(20, "Alex%")));
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

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals("select hobj1, (select hobj2.propertyValue from PersonProperty hobj2 where hobj1.id = hobj2.person.id and hobj2.propertyKey = ?) from Person hobj1"));
        assertTrue(Arrays.asList(hql.getParams()).equals(Arrays.asList("FavColorKey")));
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

        query.wheret(favoriteColorSQ).eq("Blue");
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where (select hobj2.propertyValue from PersonProperty hobj2 where hobj1.id = hobj2.person.id and hobj2.propertyKey = ?) = ?"));
        assertTrue(Arrays.asList(hql.getParams()).equals(Arrays.asList("FavColorKey", "Blue")));
    }
    
    @Test
    public void testJoinTypeNone() {
        Relation relation = query.from(Relation.class);
        Person parent = query.join(relation.getParent(), JoinType.None);
        query.where(parent.getId()).eq(1L);
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Relation hobj1 where hobj1.parent.id = ?"));
        assertTrue(Arrays.asList(hql.getParams()).equals(Arrays.asList(1L)));
    }

    @Test
    public void testSelectMaxAge() {
        Person person = query.from(Person.class);
        
        PersonDto dto = query.select(PersonDto.class);
        dto.setPersonAge(query.function().max(person.getAge()).select());

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals("select max(hobj1.age) as personAge from Person hobj1"));
    }

    @Test
    public void testSelectCoalesce() {
        Person person = query.from(Person.class);
        
        PersonDto dto = query.select(PersonDto.class);
        dto.setThePersonsName(query.function().coalesce(person.getName()).or("Bert").select());

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals("select coalesce (hobj1.name,?) as thePersonsName from Person hobj1"));
    }
}
