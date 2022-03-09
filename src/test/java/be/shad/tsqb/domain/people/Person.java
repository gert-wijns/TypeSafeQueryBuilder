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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import be.shad.tsqb.domain.DomainObject;
import be.shad.tsqb.domain.Town;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Person")
@Getter
@Setter
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

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name = "Person_Town",
            joinColumns = { @JoinColumn(name = "PersonId") },
            inverseJoinColumns = { @JoinColumn(name = "TownId")}
    )
    private Set<Town> towns = new HashSet<>();
}
