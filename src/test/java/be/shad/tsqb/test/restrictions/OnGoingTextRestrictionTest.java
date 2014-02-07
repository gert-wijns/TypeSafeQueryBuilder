package be.shad.tsqb.test.restrictions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.DirectTypeSafeValue;

public class OnGoingTextRestrictionTest extends TypeSafeQueryTest {

    @Test
    public void likeValueTest() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        query.where(person.getName()).like(new DirectTypeSafeValue<String>(query, "Jos%h"));
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.name like ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains("Jos%h"));
    }

    @Test
    public void containsTest() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getName()).contains("isto"); // Kristof, Christophe, ...
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.name like ?"));
        assertTrue("contains is a like with 2 wildcards", Arrays.asList(hql.getParams()).contains("%isto%"));
    }

    @Test
    public void startsWithTest() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getName()).startsWith("Kris");
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.name like ?"));
        assertTrue("starts with is a like with 1 wildcard on the right side", Arrays.asList(hql.getParams()).contains("Kris%"));
    }
    
    @Test
    public void endsWithTest() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getName()).endsWith("e");
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.name like ?"));
        assertTrue("ends with is a like with 1 wildcard on the left side", Arrays.asList(hql.getParams()).contains("%e"));
    }
    
}