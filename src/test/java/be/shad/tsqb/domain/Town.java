package be.shad.tsqb.domain;

import static javax.persistence.FetchType.LAZY;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Town")
public class Town extends DomainObject {
    private static final long serialVersionUID = 6589282628865449146L;

    @OneToMany(fetch = LAZY, mappedBy = "town", targetEntity = Building.class)
    private Set<Building> buildings;

    public Set<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(Set<Building> buildings) {
        this.buildings = buildings;
    }
    
}
