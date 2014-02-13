package be.shad.tsqb.domain;

import static javax.persistence.FetchType.LAZY;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import be.shad.tsqb.domain.people.Person;

@Entity
@Table(name = "Town")
public class Town extends DomainObject {
    private static final long serialVersionUID = 6589282628865449146L;

    @OneToMany(fetch = LAZY, mappedBy = "town", targetEntity = Building.class)
    private Set<Building> buildings;

    @OneToMany(fetch = LAZY, mappedBy = "town", targetEntity = Person.class)
    private Set<Person> inhabitants;
    
    @Embedded
    private GeographicCoordinate geographicCoordinate;
    
    @Column
    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeographicCoordinate getGeographicCoordinate() {
        return geographicCoordinate;
    }

    public void setGeographicCoordinate(GeographicCoordinate geographicCoordinate) {
        this.geographicCoordinate = geographicCoordinate;
    }

    public Set<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(Set<Building> buildings) {
        this.buildings = buildings;
    }
    
    public Set<Person> getInhabitants() {
        return inhabitants;
    }
    
    public void setInhabitants(Set<Person> inhabitants) {
        this.inhabitants = inhabitants;
    }
}
