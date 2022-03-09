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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import be.shad.tsqb.domain.DomainObject;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PersonProperty")
@Getter
@Setter
public class PersonProperty extends DomainObject {
    private static final long serialVersionUID = 3104165712544802569L;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "PersonId", nullable = false)
    private Person person;

    @Column(nullable = false)
    private String propertyKey;

    @Column
    private String propertyValue;

}
