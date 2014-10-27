/*
 * Copyright Gert Wijns gert.wijns@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.shad.tsqb.domain.people;

import static javax.persistence.FetchType.LAZY;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

    @OneToMany(fetch = LAZY, mappedBy = "parent", targetEntity = Relation.class)
    private Set<Relation> childRelations;
    
    @OneToMany(fetch = LAZY, mappedBy = "child", targetEntity = Relation.class)
    private Set<Relation> parentRelations;

    @OneToMany(fetch = LAZY, mappedBy = "person", targetEntity = PersonProperty.class)
    private Set<PersonProperty> properties;
    
    @OneToOne
    @JoinColumn(name = "SpouseId")
    private Person spouse;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "TownId", nullable = false)
    private Town town;
    
    @Column
    private String name;

    @Column
    private String nickname;
    
    @Column
    private int age;
    
    @Column
    private Sex sex;
    
    @Column
    private boolean married;

    public Person getSpouse() {
        return spouse;
    }
    
    public void setSpouse(Person spouse) {
        this.spouse = spouse;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
