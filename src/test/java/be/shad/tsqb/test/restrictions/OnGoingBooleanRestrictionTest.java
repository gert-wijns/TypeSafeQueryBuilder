package be.shad.tsqb.test.restrictions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.test.TypeSafeQueryTest;

public class OnGoingBooleanRestrictionTest extends TypeSafeQueryTest {
    
    @Test
    public void testIsFalse() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.isMarried()).isFalse();

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.married = ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(Boolean.FALSE));
    }

    @Test
    public void testIsTrue() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.isMarried()).isTrue();

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.married = ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(Boolean.TRUE));
    }
    
}