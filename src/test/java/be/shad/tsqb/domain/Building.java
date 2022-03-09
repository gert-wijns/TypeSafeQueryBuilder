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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import be.shad.tsqb.domain.usertype.Address;
import be.shad.tsqb.domain.usertype.TextWrappingObject;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy= InheritanceType.JOINED)
@Table(name = "Building")
@Getter
@Setter
public class Building extends DomainObject {
    private static final long serialVersionUID = -3953945063023583541L;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "TownId", nullable = false)
    private Town town;

    @Column
    private Style style;

    @Column
    private boolean occupied;

    @Column
    private Date constructionDate;

    @Type(type="AddressType")
    @Columns(columns = {
            @Column(name = "street"),
            @Column(name = "number")
    })
    private Address address;

    @Type(type="TextWrappingObjectUserType")
    @Column(name = "text")
    private TextWrappingObject text;
}
