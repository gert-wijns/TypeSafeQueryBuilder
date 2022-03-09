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
package be.shad.tsqb.domain;

import static javax.persistence.FetchType.LAZY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import be.shad.tsqb.domain.people.Person;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Town")
@Getter
@Setter
public class Town extends DomainObject {
    private static final long serialVersionUID = 6589282628865449146L;

    @OneToMany(fetch = LAZY, mappedBy = "town", targetEntity = Building.class)
    private Set<Building> buildings;

    @OneToMany(fetch = LAZY, mappedBy = "town", targetEntity = Person.class)
    private Set<Person> inhabitants;

    @OneToMany(fetch = LAZY, mappedBy = "town", targetEntity = TownProperty.class)
    private Set<TownProperty> properties;

    @Embedded
    private GeographicCoordinate geographicCoordinate;

    @Column
    private String name;

    @ManyToMany(fetch = LAZY, mappedBy = "towns")
    private Set<Person> inhabitantsMany = new HashSet<>();
}
