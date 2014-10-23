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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.domain.House;
import be.shad.tsqb.domain.Product;
import be.shad.tsqb.domain.Style;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.PersonProperty;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.domain.properties.PlanningProperties;
import be.shad.tsqb.domain.usertype.TextWrappingObject;
import be.shad.tsqb.dto.FunctionsDto;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.dto.ProductDetailsDto;
import be.shad.tsqb.dto.StringToPlanningPropertiesTransformer;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.selection.SelectionValueTransformer;
import be.shad.tsqb.values.CaseTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValueFunctions;


public class SelectTests extends TypeSafeQueryTest {

    /**
     * Select from without a select statement
     */
    @Test
    public void selectEntity() {
        query.from(House.class);
        validate(" from House hobj1");
    }

    /**
     * Select a property of the from entity.
     */
    @Test
    public void selectProperty() {
        House house = query.from(House.class);

        House result = query.select(House.class);
        result.setFloors(house.getFloors());

        validate("select hobj1.floors as floors from House hobj1");
    }

    /**
     * Select an enum property of the from entity.
     */
    @Test
    public void selectEnumProperty() {
        House house = query.from(House.class);

        House result = query.select(House.class);
        result.setStyle(house.getStyle());

        validate("select hobj1.style as style from House hobj1");
    }

    /**
     * Select the entity and a property of the entity
     */
    @Test
    public void selectEntityAndPropertyOfEntity() {
        House house = query.from(House.class);

        @SuppressWarnings("unchecked")
        MutablePair<House, Integer> result = query.select(MutablePair.class);
        result.setLeft(house);
        result.setRight(house.getFloors());

        validate("select hobj1 as left, hobj1.floors as right from House hobj1");
    }

    /**
     * Select a joined entity
     */
    @Test
    public void selectJoinedEntity() {
        Town town = query.from(Town.class);
        Building building = query.join(town.getBuildings());

        query.select(building);
        validate("select hobj2 from Town hobj1 join hobj1.buildings hobj2");
    }

    /**
     * Noone would ever select this... but,
     * check if from 2 entities selects properly:
     */
    @Test
    public void selectDoubleJoinedEntityValue() {
        House house1 = query.from(House.class);
        House house2 = query.from(House.class);

        House select = query.select(House.class);
        select.setFloors(house1.getFloors());
        select.setStyle(house2.getStyle());

        validate("select hobj1.floors as floors, hobj2.style as style from House hobj1, House hobj2");
    }

    /**
     * Select a value of a joined entity
     */
    @Test
    public void selectJoinedEntityProperty() {
        Town town = query.from(Town.class);
        Building building = query.join(town.getBuildings());

        query.select(building.getId());

        validate("select hobj2.id from Town hobj1 join hobj1.buildings hobj2");
    }

    @Test
    public void selectSubQueryValue() {
        House house = query.from(House.class);

        TypeSafeSubQuery<String> nameSubQuery = query.subquery(String.class);
        House houseSub = nameSubQuery.from(House.class);
        nameSubQuery.where(house.getId()).eq(houseSub.getId());

        nameSubQuery.select(houseSub.getName());

        query.select(nameSubQuery);
        validate("select (select hobj2.name from House hobj2 where hobj1.id = hobj2.id) from House hobj1");
    }

    @Test
    public void selectSubQueryValueAndProperty() {
        House house = query.from(House.class);

        TypeSafeSubQuery<String> nameSubQuery = query.subquery(String.class);
        House houseSub = nameSubQuery.from(House.class);
        nameSubQuery.where(house.getId()).eq(houseSub.getId());

        nameSubQuery.select(houseSub.getName());

        @SuppressWarnings("unchecked")
        MutablePair<Integer, String> result = query.select(MutablePair.class);
        result.setLeft(house.getFloors());
        result.setRight(nameSubQuery.select());

        validate("select hobj1.floors as left, (select hobj2.name from House hobj2 where hobj1.id = hobj2.id) as right from House hobj1");
    }

    /**
     * Selecting into a primitive setter should not fail.
     */
    @Test
    public void selectPrimitiveSubQueryValue() {
        House house = query.from(House.class);

        TypeSafeSubQuery<Integer> nameSubQuery = query.subquery(Integer.class);
        House houseSub = nameSubQuery.from(House.class);
        nameSubQuery.where(house.getId()).eq(houseSub.getId());

        nameSubQuery.select(houseSub.getFloors());

        House houseResult = query.select(House.class);
        houseResult.setFloors(nameSubQuery.select());

        validate("select (select hobj2.floors from House hobj2 where hobj1.id = hobj2.id) as floors from House hobj1");
    }

    /**
     * Test distinct function
     */
    @Test
    public void selectDistinct() {
        House house = query.from(House.class);

        House houseResult = query.select(House.class);
        houseResult.setFloors(query.hqlFunction().distinct(house.getFloors()).select());

        validate("select distinct hobj1.floors as floors from House hobj1");
    }

    /**
     * Test wrapped distinct function
     */
    @Test
    public void selectCountDistinct() {
        House house = query.from(House.class);

        TypeSafeValueFunctions fun = query.hqlFunction();
        @SuppressWarnings("unchecked")
        MutablePair<Long, Integer> dto = query.select(MutablePair.class);
        dto.setLeft(fun.countDistinct(house.getFloors()).select());
        dto.setRight(query.groupBy(house.getFloors()).select());

        validate("select count(distinct hobj1.floors) as left, hobj1.floors as right from House hobj1 group by hobj1.floors");
    }

    /**
     * Distinct subquery value selection
     */
    @Test
    public void selectDistinctSubquery() {
        House house = query.from(House.class);

        TypeSafeSubQuery<Long> subquery = query.subquery(Long.class);
        House subhouse = subquery.from(House.class);
        subquery.where(subhouse.getId()).lt(house.getId());
        subquery.select(subquery.hqlFunction().count());

        @SuppressWarnings("unchecked")
        MutableObject<Long> dto = query.select(MutableObject.class);
        dto.setValue(query.hqlFunction().distinct(subquery).select());

        validate("select distinct (select count(*) from House hobj2 where hobj2.id < hobj1.id) as value from House hobj1");
    }

    /**
     * Test distinct is selected after another selection was made
     * but shows up first in the hql select list.
     */
    @Test
    public void selectDistinctIsMovedToFrontOfProjections() {
        House house = query.from(House.class);

        @SuppressWarnings("unchecked")
        MutablePair<Long, Integer> dto = query.select(MutablePair.class);
        dto.setLeft(house.getId());
        dto.setRight(query.hqlFunction().distinct(house.getFloors()).select());

        validate("select distinct hobj1.floors as right, hobj1.id as left from House hobj1");
    }

    /**
     * Test the shorthand distinct call
     */
    @Test
    public void selectDistinctShorthand() {
        House house = query.from(House.class);

        House houseResult = query.select(House.class);
        houseResult.setFloors(query.distinct(house.getFloors()));

        validate("select distinct hobj1.floors as floors from House hobj1");
    }

    @Test
    public void selectDistinctWithoutDto() {
        House house = query.from(House.class);
        query.select(query.distinct(house.getFloors()));

        validate("select distinct hobj1.floors from House hobj1");
    }

    /**
     * Test count function
     */
    @Test
    @SuppressWarnings("unused")
    public void selectCount() {
        House house = query.from(House.class);

        FunctionsDto dto = query.select(FunctionsDto.class);
        dto.setTestCount(query.hqlFunction().count().select());

        validate("select count(*) as testCount from House hobj1");
    }

    @Test
    public void selectCaseWhenValue() throws ParseException {
        House house = query.from(House.class);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = "1500-02-08 12:00:00";
        Date date = df.parse(dateString);
        CaseTypeSafeValue<String> value = new CaseTypeSafeValue<String>(query, String.class);
        value.is("Test1").when(house.getFloors()).gt(40);
        value.is((String) null).when(house.getName()).startsWith("Castle");
        value.is("Old").when(house.getConstructionDate()).before(date);
        value.is(house.getName()).otherwise();

        @SuppressWarnings("unchecked")
        MutablePair<String, Object> pair = query.select(MutablePair.class);
        pair.setLeft(value.select());

        validate("select ("
                + "case when (hobj1.floors > 40) "
                + "then 'Test1' "
                + "when (hobj1.name like 'Castle%') "
                + "then null "
                + "when (hobj1.constructionDate < " + getHelper().toLiteral(date) + ") "
                + "then 'Old' "
                + "else hobj1.name end) as left "
                + "from House hobj1");
    }

    @Test
    public void selectMultiJoinedEntityValues() {
        Person person = query.from(Person.class);
        PersonProperty property1 = query.join(person.getProperties());
        PersonProperty property2 = query.join(person.getProperties(), JoinType.Inner, true);

        query.joinWith(property1).where(property1.getPropertyKey()).eq("FavoriteColor");
        query.joinWith(property2).where(property2.getPropertyKey()).eq("FavoriteDish");

        @SuppressWarnings("unchecked")
        MutableTriple<Long, String, Object> triple = query.select(MutableTriple.class);
        triple.setLeft(person.getId());
        triple.setMiddle(property1.getPropertyValue());
        triple.setRight(property2.getPropertyValue());

        validate("select hobj1.id as left, hobj2.propertyValue as middle, hobj3.propertyValue as right "
                + "from Person hobj1 "
                + "join hobj1.properties hobj2 with hobj2.propertyKey = :np1 "
                + "join hobj1.properties hobj3 with hobj3.propertyKey = :np2",
                "FavoriteColor", "FavoriteDish");
    }

    @Test
    public void selectCompositeTypeValues() {
        Building building = query.from(Building.class);

        @SuppressWarnings("unchecked")
        MutablePair<String, Style> result = query.select(MutablePair.class);
        result.setLeft(building.getAddress().getNumber());
        result.setRight(building.getStyle());

        validate("select hobj1.address.number as left, hobj1.style as right from Building hobj1");
    }

    @Test
    public void selectUserTypeValue() {
        Building building = query.from(Building.class);

        @SuppressWarnings("unchecked")
        MutablePair<TextWrappingObject, Style> result = query.select(MutablePair.class);
        result.setLeft(building.getText());
        result.setRight(building.getStyle());

        validate("select hobj1.text as left, hobj1.style as right from Building hobj1");
    }

    @Test
    public void selectComponentTypeValues() {
        Town town = query.from(Town.class);

        @SuppressWarnings("unchecked")
        MutablePair<Double, Double> result = query.select(MutablePair.class);
        result.setLeft(town.getGeographicCoordinate().getLongitude());
        result.setRight(town.getGeographicCoordinate().getLattitude());

        validate("select hobj1.geographicCoordinate.longitude as left, hobj1.geographicCoordinate.lattitude as right from Town hobj1");
    }

    @Test
    public void selectNestedComponentTypeValues() {
        Product product = query.from(Product.class);

        @SuppressWarnings("unchecked")
        MutableTriple<String, String, Boolean> triple = query.select(MutableTriple.class);
        triple.setLeft(product.getName());
        triple.setMiddle(product.getProperties().getPlanning().getAlgorithm());
        triple.setRight(product.getProperties().getSales().isSalesAllowed());

        validate("select hobj1.name as left, hobj1.properties.planning.algorithm as middle, hobj1.properties.sales.salesAllowed as right from Product hobj1");
    }

    @Test
    public void selectNestedSelectionPropertyDto() {
        Product product = query.from(Product.class);

        Product productDto = query.select(Product.class);
        productDto.setName(product.getName());
        productDto.getProperties().getPlanning().setAlgorithm(product.getName());

        validate("select hobj1.name as name, hobj1.name as properties_planning_algorithm from Product hobj1");
    }

    /**
     * Use a transformer to select a value into a selection dto which doesn't comply with the original
     * and is converted in code instead of in the query.
     */
    @Test
    public void selectSimpleTransformedValue() {
        Product product = query.from(Product.class);

        Product productDto = query.select(Product.class);
        productDto.setName(product.getName());
        productDto.getProperties().setPlanning(query.select(PlanningProperties.class,
                product.getName(), new StringToPlanningPropertiesTransformer()));

        validate("select hobj1.name as name, hobj1.name as properties_planning from Product hobj1");
    }

    /**
     * Select encoded string property into a date field.
     */
    @Test
    public void selectDateTransformedValue() {
        Product product = query.from(Product.class);

        ProductDetailsDto dto = query.select(ProductDetailsDto.class);
        dto.setId(product.getId());
        dto.setValidUntilDate(query.select(Date.class,
                product.getManyProperties().getProperty1(),
                new SelectionValueTransformer<String, Date>() {
            @Override
            public Date convert(String a) {
                try {
                    return DateFormat.getDateInstance().parse(a);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }));

        validate("select hobj1.id as id, hobj1.manyProperties.property1 as validUntilDate from Product hobj1");
    }

    /**
     * Select an object and use some of the values of this object to transform into another dto.
     * <p>
     * Using a proxy as select value to get values from it should be avoided. Only do this
     * if there is no other option! (value on a dto without a default constructor for example)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void selectProxyTransformedValue() {
        Person person = query.from(Person.class);
        Relation relation = query.join(person.getChildRelations());

        MutablePair<Long, ImmutablePair<Long, Integer>> select = query.select(MutablePair.class);
        select.setLeft(person.getId());
        select.setRight(query.select(ImmutablePair.class, relation.getChild(),
            new SelectionValueTransformer<Person, ImmutablePair>() {
                @Override
                public ImmutablePair<Long, Integer> convert(Person a) {
                    return new ImmutablePair<Long, Integer>(a.getId(), a.getAge());
                }
        }));

        validate("select hobj1.id as left, hobj3 as right from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3");
    }

    @Test
    public void selectProxyClassTwiceShouldntFail() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();
        creator.createTestPerson(town, "Anonymous");

        Person person = query.from(Person.class);

        PersonDto personDto = query.select(PersonDto.class);
        personDto.setPersonAge(person.getAge());

        // second select proxy, can be a new instance, it's only a
        // bridge to add the projections after all
        personDto = query.select(PersonDto.class);
        personDto.setThePersonsName(person.getName());

        validate("select hobj1.age as personAge, hobj1.name as thePersonsName from Person hobj1");
    }
}
