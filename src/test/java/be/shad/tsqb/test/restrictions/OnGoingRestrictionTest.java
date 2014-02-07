package be.shad.tsqb.test.restrictions;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.DirectTypeSafeValue;

public class OnGoingRestrictionTest extends TypeSafeQueryTest {

    @Test
    public void testIsNull() {
        Person person = query.from(Person.class);
        query.where(person.getName()).isNull();
        validate(" from Person hobj1 where hobj1.name is null ");
    }

    @Test
    public void testIsNotNull() {
        Person person = query.from(Person.class);
        query.where(person.getName()).isNotNull();
        validate(" from Person hobj1 where hobj1.name is not null ");
    }

    @Test
    public void testTypeSafeValueInCollection() {
        Person person = query.from(Person.class);
        List<String> names = Arrays.asList("Jos", "Marie", "Katrien");
        query.where(person.getName()).in(names);
        validate(" from Person hobj1 where hobj1.name in (?, ?, ?)", names);
    }
    
    @Test
    public void testTypeSafeValueInSubQuery() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        TypeSafeSubQuery<Number> subquery = query.subquery(Number.class);
        Relation relation = subquery.from(Relation.class);
        Person parent = subquery.join(relation.getParent(), JoinType.None);
        Person child = subquery.join(relation.getChild());
        subquery.where(child.isMarried()).isTrue();
        subquery.select(parent.getId());
        
        query.where(person.getId()).in(subquery);

        validate(" from Person hobj1 where hobj1.id in ("
                + "select hobj2.parent.id from Relation hobj2 "
                + "join hobj2.child hobj4 where hobj4.married = ?"
                + ")", Boolean.TRUE);
    }

    @Test
    public void testNotIn() {
        Person person = query.from(Person.class);
        List<String> names = Arrays.asList("Jos", "Marie", "Katrien");
        query.where(person.getName()).notIn(names);
        validate(" from Person hobj1 where hobj1.name not in (?, ?, ?)", 
                "Jos", "Marie", "Katrien");
    }

    @Test
    public void testTypeSafeValueNotIn() {
        Person person = query.from(Person.class);
        
        TypeSafeSubQuery<Number> subquery = query.subquery(Number.class);
        Relation relation = subquery.from(Relation.class);
        Person parent = subquery.join(relation.getParent(), JoinType.None);
        Person child = subquery.join(relation.getChild());
        subquery.where(child.isMarried()).isTrue();
        subquery.select(parent.getId());
        
        query.where(person.getId()).notIn(subquery);

        validate(" from Person hobj1 where hobj1.id not in ("
                + "select hobj2.parent.id from Relation hobj2 "
                + "join hobj2.child hobj4 where hobj4.married = ?"
                + ")", Boolean.TRUE);
    }

    @Test
    public void testEq() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).eq(40);
        validate(" from Person hobj1 where hobj1.age = ?", 40);
    }

    @Test
    public void testTypeSafeValueEq() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).eq(new DirectTypeSafeValue<Number>(query, 40));
        validate(" from Person hobj1 where hobj1.age = ?", 40);
    }

    @Test
    public void testNot() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).not(40);
        validate(" from Person hobj1 where hobj1.age <> ?", 40);
    }

    @Test
    public void testTypeSafeValueNot() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).not(new DirectTypeSafeValue<Number>(query, 40));
        validate(" from Person hobj1 where hobj1.age <> ?", 40);
    }
    
}