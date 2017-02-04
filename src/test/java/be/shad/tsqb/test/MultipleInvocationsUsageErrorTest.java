package be.shad.tsqb.test;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;

public class MultipleInvocationsUsageErrorTest extends TypeSafeQueryTest {

    @Test(expected=IllegalStateException.class)
    public void testMultipleInvocationsWithoutUsageThrows() {
        Person person = query.from(Person.class);
        person.getName();
        query.where(person.getName()).equals("Hugo");
    }

}
