package be.shad.tsqb.test;

import org.hibernate.SessionFactory;

import be.shad.tsqb.domain.GeographicCoordinate;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;

/**
 * Just some helper methods to easily insert some dummy data
 * so the queries can return a result to test the result transformer.
 */
public class TestDataCreator {
    private final SessionFactory sessionFactory;
    private long idGen = 1;
    
    public TestDataCreator(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Town createTestTown() {
        Town town = new Town();
        town.setId(idGen);
        town.setName("TestTown");
        town.setGeographicCoordinate(new GeographicCoordinate());
        town.getGeographicCoordinate().setLattitude(1d);
        town.getGeographicCoordinate().setLongitude(1d);
        sessionFactory.getCurrentSession().save(town);
        return town;
    }

    public void addChildRelation(Person parent, Person child) {
        Relation relation = new Relation();
        relation.setId(idGen++);
        relation.setParent(parent);
        relation.setChild(child);
        sessionFactory.getCurrentSession().save(relation);
    }

    public Person createTestPerson(Town town, String name) {
        Person person = new Person();
        person.setId(idGen++);
        person.setName(name);
        person.setTown(town);
        sessionFactory.getCurrentSession().save(person);
        return person;
    }

}
