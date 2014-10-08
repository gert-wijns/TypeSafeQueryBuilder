package be.shad.tsqb.test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;

import be.shad.tsqb.domain.Apartment;
import be.shad.tsqb.domain.GeographicCoordinate;
import be.shad.tsqb.domain.House;
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
        if (town.getInhabitants() == null) {
            town.setInhabitants(new HashSet<Person>());
        }
        town.getInhabitants().add(person);
        sessionFactory.getCurrentSession().save(person);
        return person;
    }

    public House createTestHouse(Town town, String name, int floors) {
        House house = new House();
        house.setTown(town);
        house.setId(idGen++);
        house.setName(name);
        house.setFloors(floors);
        sessionFactory.getCurrentSession().save(house);
        return house;
    }

    public Apartment createTestApartment(Town town, BigDecimal revenue) {
        Apartment apartment = new Apartment();
        apartment.setTown(town);
        apartment.setId(idGen++);
        apartment.setRevenue(revenue);
        sessionFactory.getCurrentSession().save(apartment);
        return apartment;
    }

    public Town createTestTownWithPeople(Set<String> names) {
        Town town = createTestTown();
        for(String name: names) {
            createTestPerson(town, name);
        }
        return town;
    }

}
