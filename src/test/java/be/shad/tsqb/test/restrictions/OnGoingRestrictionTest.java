package be.shad.tsqb.test.restrictions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.DirectTypeSafeValue;

public class OnGoingRestrictionTest extends TypeSafeQueryTest {

    @Test
    public void testIsNull() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getName()).isNull();
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.name is null "));
    }

    @Test
    public void testIsNotNull() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getName()).isNotNull();
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.name is not null "));
    }

    @Test
    public void testTypeSafeValueInCollection() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        List<String> names = Arrays.asList("Jos", "Marie", "Katrien");
        query.where(person.getName()).in(names);
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.name in (?, ?, ?)"));
        assertTrue(Arrays.asList(hql.getParams()).containsAll(names));
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

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.id in ("
                + "select hobj2.parent.id from Relation hobj2 "
                + "join hobj2.child hobj4 where hobj4.married = ?"
                + ")"));
        assertTrue(Arrays.asList(hql.getParams()).contains(Boolean.TRUE));
    }

    @Test
    public void testNotIn() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        List<String> names = Arrays.asList("Jos", "Marie", "Katrien");
        query.where(person.getName()).notIn(names);
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.name not in (?, ?, ?)"));
        assertTrue(Arrays.asList(hql.getParams()).containsAll(names));
    }

    @Test
    public void testTypeSafeValueNotIn() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        TypeSafeSubQuery<Number> subquery = query.subquery(Number.class);
        Relation relation = subquery.from(Relation.class);
        Person parent = subquery.join(relation.getParent(), JoinType.None);
        Person child = subquery.join(relation.getChild());
        subquery.where(child.isMarried()).isTrue();
        subquery.select(parent.getId());
        
        query.where(person.getId()).notIn(subquery);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.id not in ("
                + "select hobj2.parent.id from Relation hobj2 "
                + "join hobj2.child hobj4 where hobj4.married = ?"
                + ")"));
        assertTrue(Arrays.asList(hql.getParams()).contains(Boolean.TRUE));
    }

    @Test
    public void testEq() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).eq(40);
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age = ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

    @Test
    public void testTypeSafeValueEq() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).eq(new DirectTypeSafeValue<Number>(query, 40));
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age = ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

    @Test
    public void testNot() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).not(40);
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age <> ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }

    @Test
    public void testTypeSafeValueNot() {
        TypeSafeRootQuery query = createQuery();
        Person person = query.from(Person.class);
        
        query.where(person.getAge()).not(new DirectTypeSafeValue<Number>(query, 40));
        
        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Person hobj1 where hobj1.age <> ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(40));
    }
    
}