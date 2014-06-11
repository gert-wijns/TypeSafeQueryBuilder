package be.shad.tsqb.test;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.dto.PersonDto;

public class WithDataTest extends TypeSafeQueryTest {

    @Test
    public void testFindPerson() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestPerson(creator.createTestTown(), "Josh");
        
        Person fromProxy = query.from(Person.class);
        PersonDto selectProxy = query.select(PersonDto.class);
        selectProxy.setId(fromProxy.getId());
        validate("select hobj1.id as id from Person hobj1");
    }
    
}
