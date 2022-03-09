package be.shad.tsqb.test;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.values.CaseTypeSafeValue;

public class CaseWhenTypeSafeValueTest extends TypeSafeQueryTest {

    /**
     * Tests TSQB issue 92
     */
    @Test
    public void testSelectAndInvocationMix() {
        Person person = query.from(Person.class);

        CaseTypeSafeValue<String> caseWhen = query.hqlFunction().caseWhen(String.class);
        caseWhen.is(query.toValue(person.getName()).select())
            .when("Bob").eq(person.getName());
        query.selectValue(caseWhen);

        validate("select (case when ('Bob' = hobj1.name) then hobj1.name end) from Person hobj1");
    }

}
