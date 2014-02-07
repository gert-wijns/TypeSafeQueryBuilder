package be.shad.tsqb.test.restrictions;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.test.TypeSafeQueryTest;

public class OnGoingBooleanRestrictionTest extends TypeSafeQueryTest {
    
    @Test
    public void testIsFalse() {
        Person person = query.from(Person.class);
        query.where(person.isMarried()).isFalse();
        validate(" from Person hobj1 where hobj1.married = ?", Boolean.FALSE);
    }

    @Test
    public void testIsTrue() {
        Person person = query.from(Person.class);
        query.where(person.isMarried()).isTrue();
        validate(" from Person hobj1 where hobj1.married = ?", Boolean.TRUE);
    }
    
}