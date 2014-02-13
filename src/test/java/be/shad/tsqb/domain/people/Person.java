package be.shad.tsqb.domain.people;

import static javax.persistence.FetchType.LAZY;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import be.shad.tsqb.domain.DomainObject;
import be.shad.tsqb.domain.Town;

@Entity
@Table(name = "Person")
public class Person extends DomainObject {
    private static final long serialVersionUID = -3748330536304370152L;

    public enum Sex {
        Male,
        Female,
        Other
    }

    @OneToMany(fetch = LAZY, mappedBy = "child", targetEntity = Relation.class)
    private Set<Relation> childRelations;
    
    @OneToMany(fetch = LAZY, mappedBy = "parent", targetEntity = Relation.class)
    private Set<Relation> parentRelations;

    @OneToMany(fetch = LAZY, mappedBy = "person", targetEntity = PersonProperty.class)
    private Set<PersonProperty> properties;

    @Column
    private Town town;
    
    @Column
    private String name;
    
    @Column
    private int age;
    
    @Column
    private Sex sex;
    
    @Column
    private boolean married;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }
    
    public boolean isMarried() {
        return married;
    }
    
    public void setMarried(boolean married) {
        this.married = married;
    }

    public Set<Relation> getChildRelations() {
        return childRelations;
    }

    public void setChildRelations(Set<Relation> childRelations) {
        this.childRelations = childRelations;
    }

    public Set<Relation> getParentRelations() {
        return parentRelations;
    }

    public void setParentRelations(Set<Relation> parentRelations) {
        this.parentRelations = parentRelations;
    }
    
    public Set<PersonProperty> getProperties() {
        return properties;
    }
    
    public void setProperties(Set<PersonProperty> properties) {
        this.properties = properties;
    }
    
    public Town getTown() {
        return town;
    }
    
    public void setTown(Town town) {
        this.town = town;
    }
}
