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
package be.shad.tsqb.test;

import java.util.Date;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.domain.Product;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.ordering.OrderByProjection;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.selection.parallel.SelectValue;

public class OrderingTest extends TypeSafeQueryTest {

    @Test
    public void testOrderByDesc() {
        Building building = query.from(Building.class);
        query.orderBy().desc(building.getConstructionDate());

        validate(" from Building hobj1 order by hobj1.constructionDate desc");
    }

    @Test
    public void testOrderByAsc() {
        Building building = query.from(Building.class);
        query.orderBy().asc(building.getConstructionDate());

        validate(" from Building hobj1 order by hobj1.constructionDate");
    }

    @Test
    public void testOrderByMultiple() {
        Building building = query.from(Building.class);
        query.orderBy().asc(building.getConstructionDate());
        query.orderBy().desc(building.getStyle());

        validate(" from Building hobj1 order by hobj1.constructionDate, hobj1.style desc");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOrderByAlias() {
        Building building = query.from(Building.class);

        MutablePair<Long, Date> select = query.select(MutablePair.class);
        select.setLeft(building.getId());
        select.setRight(building.getConstructionDate());

        query.orderBy().by(new OrderByProjection(query, "right", true));

        validate("select hobj1.id as left, hobj1.constructionDate as right from Building hobj1 order by hobj1.constructionDate desc");
    }

    @Test
    public void testOrderBySelectionDtoField() {
        Person person = query.from(Person.class);

        PersonDto dto = query.select(PersonDto.class);
        dto.setThePersonsName(person.getName());

        query.orderBy().desc(dto.getThePersonsName());

        validate("select hobj1.name as thePersonsName from Person hobj1 order by hobj1.name desc");
    }

    @Test
    public void testOrderByNestedSelectionDtoField() {
        Product product = query.from(Product.class);

        Product dto = query.select(Product.class);
        dto.getManyProperties().setProperty1(product.getName());

        query.orderBy().desc(dto.getManyProperties().getProperty1());

        validate("select hobj1.name as manyProperties_property1 from Product hobj1 order by hobj1.name desc");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOrderByAliasWithSubQuery() {
        Building building = query.from(Building.class);
        TypeSafeSubQuery<Long> maxId = query.subquery(Long.class);
        Building buildingMaxId = maxId.from(Building.class);
        maxId.select(maxId.hqlFunction().max(buildingMaxId.getId()));

        MutablePair<Long, Date> select = query.select(MutablePair.class);
        select.setLeft(maxId.select());
        select.setRight(building.getConstructionDate());

        query.orderBy().by(new OrderByProjection(query, "left", true));

        validate("select (select max(hobj2.id) from Building hobj2) as left, hobj1.constructionDate as right from Building hobj1 order by 1 desc");
    }

    @Test
    public void testOrderByIgnoreCase() {
        Product product = query.from(Product.class);
        Product dto = query.select(Product.class);
        dto.getManyProperties().setProperty1(product.getName());
        query.orderBy().descIgnoreCase(dto.getManyProperties().getProperty1());

        validate("select hobj1.name as manyProperties_property1 from Product hobj1 order by upper(hobj1.name) desc");
    }

    @Test(expected=IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testOrderByWrappedProjectionSubQueryFails() {
        Person person = query.from(Person.class);
        TypeSafeSubQuery<String> nameSQ = query.subquery(String.class);
        Person personSub = nameSQ.from(Person.class);
        nameSQ.where(personSub.getId()).eq(person.getId());
        nameSQ.select(personSub.getName());

        SelectValue<String> select = query.select(SelectValue.class);
        select.setValue(nameSQ.select());

        query.orderBy().descIgnoreCase(select.getValue());
        query.toHqlQuery();
    }
}
