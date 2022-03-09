package be.shad.tsqb.test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import org.hibernate.SessionFactory;

import be.shad.tsqb.domain.Apartment;
import be.shad.tsqb.domain.GeographicCoordinate;
import be.shad.tsqb.domain.House;
import be.shad.tsqb.domain.Product;
import be.shad.tsqb.domain.Style;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.PersonProperty;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.domain.properties.ManyProperties;
import be.shad.tsqb.domain.properties.PlanningProperties;
import be.shad.tsqb.domain.properties.ProductProperties;
import be.shad.tsqb.domain.properties.SalesProperties;
import be.shad.tsqb.domain.usertype.Address;
import be.shad.tsqb.domain.usertype.TextWrappingObject;

/**
 * Just some helper methods to easily insert some dummy data
 * so the queries can return a result to test the result transformer.
 */
public class TestDataCreator {
    private static final Random RANDOM = new Random();
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

    public Person createTestPerson() {
        return createTestPerson(createTestTown(), randomString(5));
    }

    public Person createTestPerson(Town town, String name) {
        Person person = new Person();
        person.setId(idGen++);
        person.setName(name);
        person.setTown(town);
        person.setAge(RANDOM.nextInt(30));
        if (town.getInhabitants() == null) {
            town.setInhabitants(new HashSet<>());
        }
        town.getInhabitants().add(person);
        person.getTowns().add(town);
        town.getInhabitantsMany().add(person);
        sessionFactory.getCurrentSession().save(person);
        return person;
    }

    public House createTestHouse(Town town, String name, int floors) {
        House house = new House();
        house.setTown(town);
        house.setId(idGen++);
        house.setName(name);
        house.setFloors(floors);
        house.setStyle(Style.values()[RANDOM.nextInt(Style.values().length)]);
        house.setAddress(new Address());
        house.getAddress().setNumber(randomString(2));
        house.getAddress().setStreet(randomString(2));
        house.setText(new TextWrappingObject(randomString(2)));
        sessionFactory.getCurrentSession().save(house);
        return house;
    }

    public House createRandomHouse() {
        return createTestHouse(createTestTown(), randomString(10), RANDOM.nextInt(10)) ;
    }

    public House createRandomHouse(Town town) {
        return createTestHouse(town, randomString(10), RANDOM.nextInt(10)) ;
    }

    public Apartment createTestApartment(Town town, BigDecimal revenue) {
        Apartment apartment = new Apartment();
        apartment.setTown(town);
        apartment.setId(idGen++);
        apartment.setRevenue(revenue);
        sessionFactory.getCurrentSession().save(apartment);
        return apartment;
    }

    public Town createTestTownWithPeople(Collection<String> names) {
        Town town = createTestTown();
        for(String name: names) {
            createTestPerson(town, name);
        }
        return town;
    }

    public PersonProperty createPersonProperty(Person person, String key) {
        return createPersonProperty(person, key, randomString(8));
    }

    public PersonProperty createPersonProperty(Person person, String key, String value) {
        PersonProperty property = new PersonProperty();
        property.setId(idGen++);
        property.setPerson(person);
        property.setPropertyKey(key);
        property.setPropertyValue(value);
        if (person.getProperties() == null) {
            person.setProperties(new HashSet<>());
        }
        person.getProperties().add(property);
        sessionFactory.getCurrentSession().save(property);
        return property;
    }

    private String randomString(int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        return RANDOM.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public Product createRandomProduct() {
        Product product = new Product();
        product.setId(idGen++);
        product.setName(randomString(5));
        product.setProductProperties(createRandomProductProperties());
        product.setManyProperties(createRandomManyProperties());
        sessionFactory.getCurrentSession().save(product);
        return product;
    }

    private ManyProperties createRandomManyProperties() {
        ManyProperties p = new ManyProperties();
        p.setProperty1(randomString(2));
        p.setProperty2(randomString(2));
        p.setProperty3(randomString(2));
        return p;
    }

    private ProductProperties createRandomProductProperties() {
        ProductProperties p = new ProductProperties();
        p.setPlanning(createRandomPlanningProperties());
        p.setSales(createRandomSalesProperties());
        return p;
    }

    private SalesProperties createRandomSalesProperties() {
        SalesProperties p = new SalesProperties();
        p.setSalesAllowed(RANDOM.nextBoolean());
        return p;
    }

    private PlanningProperties createRandomPlanningProperties() {
        PlanningProperties p = new PlanningProperties();
        p.setAlgorithm(randomString(4));
        return p;
    }

    public Relation createRelation(Person personAData, Person personBData) {
        Relation relation = new Relation();
        relation.setParent(personAData);
        relation.setChild(personBData);
        relation.setId(idGen++);
        sessionFactory.getCurrentSession().save(relation);
        if (personAData.getChildRelations() == null){
            personAData.setChildRelations(new HashSet<>());
        }
        personAData.getChildRelations().add(relation);
        if (personBData.getParentRelations() == null) {
            personBData.setParentRelations(new HashSet<>());
        }
        personBData.getParentRelations().add(relation);
        return relation;
    }
}
