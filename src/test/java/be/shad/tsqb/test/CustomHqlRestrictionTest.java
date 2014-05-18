package be.shad.tsqb.test;

import java.util.Arrays;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.values.HqlQueryValueImpl;

public class CustomHqlRestrictionTest extends TypeSafeQueryTest {
    private static final String PARENT_CUSTOM_ALIAS = "parentAlias";

    /**
     * 
     */
    @Test
    public void testInjectCustomHqlRestriction() {
        Person person = query.from(Person.class);
        query.registerCustomAliasForProxy(person, PARENT_CUSTOM_ALIAS);
        query.where().and(new HqlQueryValueImpl("parentAlias.id = ?", Arrays.asList((Object) 1L)));
        validate(" from Person parentAlias where parentAlias.id = ?", 1L);
    }

    /**
     * 
     */
    @Test
    public void testInjectMultipleCustomHqlRestriction() {
        Person person = query.from(Person.class);
        query.registerCustomAliasForProxy(person, PARENT_CUSTOM_ALIAS);
        query.where().and(new HqlQueryValueImpl("parentAlias.id = ?", Arrays.asList((Object) 1L)));
        query.where().and(new HqlQueryValueImpl("parentAlias.name like ?", Arrays.asList((Object) "Josh%")));
        validate(" from Person parentAlias where parentAlias.id = ? and parentAlias.name like ?", 1L, "Josh%");
    }

}
