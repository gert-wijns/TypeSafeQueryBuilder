package be.shad.tsqb.test.restrictions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.DirectTypeSafeValue;

public class OnGoingNumberRestrictionTest extends TypeSafeQueryTest {

    @Test
    public void testLt() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).lt(40);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age < ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

    @Test
    public void testTypeSafeValueLt() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).lt(new DirectTypeSafeValue<Number>(query, 40));

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age < ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

    @Test
    public void testGt() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).gt(40);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age > ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

    @Test
    public void testTypeSafeValueGt() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).gt(new DirectTypeSafeValue<Number>(query, 40));

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age > ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

    @Test
    public void testLte() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).lte(40);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age <= ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

    @Test
    public void testTypeSafeValueLte() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).lte(new DirectTypeSafeValue<Number>(query, 40));

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age <= ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

    @Test
    public void testGte() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).gte(40);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age >= ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

    @Test
    public void testTypeSafeValueGte() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).gte(new DirectTypeSafeValue<Number>(query, 40));

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age >= ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

}