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

import static be.shad.tsqb.joins.JoinParams.defaultJoin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

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
import be.shad.tsqb.dto.CustomMap;
import be.shad.tsqb.dto.FunctionsDto;
import be.shad.tsqb.dto.MapsDto;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.dto.ProductDetailsDto;
import be.shad.tsqb.dto.StringToPlanningPropertiesTransformer;
import be.shad.tsqb.dto.TownDetailsDto;
import be.shad.tsqb.dto.ValueDto;
import be.shad.tsqb.dto.ValueSubDto;
import be.shad.tsqb.exceptions.SelectException;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.restrictions.ContinuedOnGoingNumberRestriction;
import be.shad.tsqb.restrictions.RestrictionsGroupFactory;
import be.shad.tsqb.selection.parallel.MapSelectionMerger;
import be.shad.tsqb.values.CaseTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;
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
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();

        House house = query.from(House.class);

        House result = query.select(House.class);
        result.setFloors(house.getFloors());

        validate("select hobj1.floors as floors from House hobj1");
        House actual = getSingleQueryResults();
        assertEquals(houseData.getFloors(), actual.getFloors());
    }

    /**
     * Select a restriction.
     */
    @Test
    public void selectBuilderRestriction() {
        House house = query.from(House.class);

        RestrictionsGroupFactory rb = query.getGroupedRestrictionsBuilder();
        query.selectValue(rb.where(house.getFloors()).gt(1));
        validate("select case when(hobj1.floors > 1) then true else false end from House hobj1");
    }

    @Test(expected=SelectException.class)
    public void selectQueryRestrictionShouldFailCauseItsUnlikelyThisWasThePurpose() {
        House house = query.from(House.class);
        query.selectValue(query.where(house.getFloors()).gt(1));
    }

    @Test(expected=SelectException.class)
    public void selectRestrictionWithMultiplePartsFailWhenUsingWhere() {
        House house = query.from(House.class);
        query.selectValue(query.where(house.getFloors()).gt(1).lt(10));
    }

    @Test(expected=SelectException.class)
    public void selectRestrictionWithMultiplePartsFailWhenAddedSubRestrictionGroup() {
        House house = query.from(House.class);

        RestrictionsGroupFactory rb = query.getGroupedRestrictionsBuilder();
        ContinuedOnGoingNumberRestriction check = rb.where(house.getFloors()).gt(1);
        query.where(check.getRestriction());
        query.selectValue(check);
    }

    /**
     * When the sub restriction is created first and selected before appending to the
     * query where it bypasses the check whether it's already in the where clause
     * (this way such a restriction can actually be reused when you know what you're doing).
     */
    public void selectRestrictionWithMultiplePartsDoesntFailWhenAddedSubRestrictionGroupInCorrectOrder() {
        House house = query.from(House.class);

        RestrictionsGroupFactory rb = query.getGroupedRestrictionsBuilder();
        ContinuedOnGoingNumberRestriction check = rb.where(house.getFloors()).gt(1);
        query.selectValue(check);
        query.where(check.getRestriction());
        validate("select case when(hobj1.floors > 1) then true else false end from House hobj1 where hobj1.floors > :np1", 1L);
    }

    /**
     * Select an enum property of the from entity.
     */
    @Test
    public void selectEnumProperty() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();

        House house = query.from(House.class);

        House result = query.select(House.class);
        result.setStyle(house.getStyle());

        validate("select hobj1.style as style from House hobj1");
        assertEquals(houseData.getStyle(), ((House) this.doQueryResult.get(0)).getStyle());
    }

    /**
     * Select the entity and a property of the entity
     */
    @Test
    public void selectEntityAndPropertyOfEntity() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();

        House house = query.from(House.class);

        @SuppressWarnings("unchecked")
        MutablePair<House, Integer> result = query.select(MutablePair.class);
        result.setLeft(house);
        result.setRight(house.getFloors());

        validate("select hobj1 as left, hobj1.floors as right from House hobj1");
        MutablePair<House, Integer> actual = getSingleQueryResults();
        assertEquals(houseData.getFloors(), actual.getRight().intValue());

    }

    /**
     * Select a joined entity
     */
    @Test
    public void selectJoinedEntity() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();

        Town town = query.from(Town.class);
        Building building = query.join(town.getBuildings());

        query.selectValue(building);
        validate("select hobj2 from Town hobj1 join hobj1.buildings hobj2");
        Building actual = getSingleQueryResults();
        assertEquals(houseData.getId(), actual.getId());
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
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();

        Town town = query.from(Town.class);
        Building building = query.join(town.getBuildings());

        query.selectValue(building.getId());

        validate("select hobj2.id from Town hobj1 join hobj1.buildings hobj2");
        Long actual = getSingleQueryResults();
        assertEquals(houseData.getId(), actual);
    }

    @Test
    public void selectSubQueryValue() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();

        House house = query.from(House.class);

        TypeSafeSubQuery<String> nameSubQuery = query.subquery(String.class);
        House houseSub = nameSubQuery.from(House.class);
        nameSubQuery.where(house.getId()).eq(houseSub.getId());

        nameSubQuery.select(houseSub.getName());

        query.selectValue(nameSubQuery);
        validate("select (select hobj2.name from House hobj2 where hobj1.id = hobj2.id) from House hobj1");
        String actual = getSingleQueryResults();
        assertEquals(houseData.getName(), actual);
    }

    @Test
    public void selectSubQueryValueAndProperty() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();

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
        MutablePair<Integer, String> actual = getSingleQueryResults();
        assertEquals(houseData.getFloors(), actual.getLeft().intValue());
        assertEquals(houseData.getName(), actual.getRight());
    }

    /**
     * Selecting into a primitive setter should not fail.
     */
    @Test
    public void selectPrimitiveSubQueryValue() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();

        House house = query.from(House.class);

        TypeSafeSubQuery<Integer> nameSubQuery = query.subquery(Integer.class);
        House houseSub = nameSubQuery.from(House.class);
        nameSubQuery.where(house.getId()).eq(houseSub.getId());

        nameSubQuery.select(houseSub.getFloors());

        House houseResult = query.select(House.class);
        houseResult.setFloors(nameSubQuery.select());

        validate("select (select hobj2.floors from House hobj2 where hobj1.id = hobj2.id) as floors from House hobj1");
        House actual = getSingleQueryResults();
        assertEquals(houseData.getFloors(), actual.getFloors());
    }

    /**
     * Selecting both into a dto and without a dto doesn't can't be combined
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSelectIntoDtoAfterSelectWithoutDtoThrowsException() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestPerson(creator.createTestTown(), "Josh");

        Person person = query.from(Person.class);
        query.selectValue(person.getAge());
        PersonDto dtoPx = query.select(PersonDto.class);
        dtoPx.setId(person.getId());

        validate("select hobj1.age, hobj1.id as id from Person hobj1");
    }

    /**
     * Selecting both without a dto and into a dto doesn't can't be combined
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSelectWithoutDtoAfterSelectIntoDtoThrowsException() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestPerson(creator.createTestTown(), "Josh");

        Person person = query.from(Person.class);
        PersonDto dtoPx = query.select(PersonDto.class);
        dtoPx.setId(person.getId());
        query.selectValue(person.getAge());

        validate("select hobj1.id as id, hobj1.age from Person hobj1");
    }

    /**
     * Test distinct function
     */
    @Test
    public void selectDistinct() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();
        House house = query.from(House.class);

        House houseResult = query.select(House.class);
        houseResult.setFloors(query.hqlFunction().distinct(house.getFloors()).select());

        validate("select distinct hobj1.floors as floors from House hobj1");
        House actual = getSingleQueryResults();
        assertEquals(houseData.getFloors(), actual.getFloors());
    }

    /**
     * Test wrapped distinct function
     */
    @Test
    public void selectCountDistinct() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();
        House house = query.from(House.class);

        TypeSafeValueFunctions fun = query.hqlFunction();
        @SuppressWarnings("unchecked")
        MutablePair<Long, Integer> dto = query.select(MutablePair.class);
        dto.setLeft(fun.countDistinct(house.getFloors()).select());
        dto.setRight(query.groupBy(house.getFloors()).select());

        validate("select count(distinct hobj1.floors) as left, hobj1.floors as right from House hobj1 group by hobj1.floors");
        MutablePair<Long, Integer> actual = getSingleQueryResults();
        assertEquals(1, actual.getLeft().intValue());
        assertEquals(houseData.getFloors(), actual.getRight().intValue());

    }

    /**
     * Distinct subquery value selection
     */
    @Test
    public void selectDistinctSubquery() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();
        House house = query.from(House.class);

        TypeSafeSubQuery<Long> subquery = query.subquery(Long.class);
        House subhouse = subquery.from(House.class);
        subquery.where(subhouse.getId()).lt(house.getId());
        subquery.select(subquery.hqlFunction().count());

        @SuppressWarnings("unchecked")
        MutableObject<Long> dto = query.select(MutableObject.class);
        dto.setValue(query.hqlFunction().distinct(subquery).select());

        validate("select distinct (select count(*) from House hobj2 where hobj2.id < hobj1.id) as value from House hobj1");
        MutableObject<Long> actual = getSingleQueryResults();
        assertEquals(0, actual.getValue().intValue());
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
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();
        House house = query.from(House.class);
        query.selectValue(query.distinct(house.getFloors()));

        validate("select distinct hobj1.floors from House hobj1");
        Integer actual = getSingleQueryResults();
        assertEquals(houseData.getFloors(), actual.intValue());
    }

    /**
     * Test count function
     */
    @Test
    @SuppressWarnings("unused")
    public void selectCount() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createRandomHouse();
        creator.createRandomHouse();
        House house = query.from(House.class);

        FunctionsDto dto = query.select(FunctionsDto.class);
        dto.setTestCount(query.hqlFunction().count().select());

        validate("select count(*) as testCount from House hobj1");
        FunctionsDto actual = getSingleQueryResults();
        assertEquals(2, actual.getTestCount().longValue());
    }

    @Test
    public void selectCaseWhenValue() throws ParseException {
        House house = query.from(House.class);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = "1500-02-08 12:00:00";
        Date date = df.parse(dateString);
        CaseTypeSafeValue<String> value = new CaseTypeSafeValue<>(query, String.class);
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
    
    /**
     * Test different types of maps can be filled with
     * values using put. Also tests sorting by one of the
     * selected map values.
     */
    @Test
    public void selectMapsDtoTestWithAliases() {
        selectMapsDtoTest(true);
    }

    /**
     * Validate the query with results works when the aliases
     * are not included in the select statement
     */
    @Test
    public void selectMapsDtoTestWithoutAliases() {
        selectMapsDtoTest(false);
    }

    private void selectMapsDtoTest(boolean aliases) {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestPerson(creator.createTestTown(), "Josh");
        ((TypeSafeRootQueryInternal) query).getProjections().setIncludeAliases(aliases);

        Person person = query.from(Person.class);
        MapsDto dtoPx = query.select(MapsDto.class);
        Map<String, Object> genericMapPx = dtoPx.getNestedMaps().getGenericMap();
        CustomMap<String,Object> customMapPx = dtoPx.getNestedMaps().getCustomMap();
        SortedMap<String, Object> sortedMapPx = dtoPx.getSortedMap();
        genericMapPx.put("person.value", person.getName());
        customMapPx.put("person.object", person);
        sortedMapPx.put("person.transformed", query.select(String.class, person.getName(),
                (name) -> "###" + name + "###"));

        // subselect map
        Map<String, Object> merge1 = query.selectMergeValues(dtoPx,
                new MapSelectionMerger<MapsDto, String, Object>() {
            @Override
            public void mergeMapIntoResult(MapsDto partialResult, Map<String, Object> map) {
                partialResult.getSortedMap().put("thepersonid", map.get("person.id"));
            }
        });
        merge1.put("person.id", person.getId());
        if (aliases) {
            validate("select hobj1.name as g2__person_value, "
                    + "hobj1 as g3__person_object, "
                    + "hobj1.name as g4__person_transformed, "
                    + "hobj1.id as g5__person_id "
                    + "from Person hobj1");
        } else {
            validate("select hobj1.name, hobj1, hobj1.name, hobj1.id from Person hobj1");
        }
        assertTrue(MapsDto.class.isAssignableFrom(doQueryResult.get(0).getClass()));

        MapsDto result = (MapsDto) doQueryResult.get(0);
        Map<String, Object> genericMap = result.getNestedMaps().getGenericMap();
        CustomMap<String,Object> customMap = result.getNestedMaps().getCustomMap();
        SortedMap<String, Object> sortedMap = result.getSortedMap();
        assertEquals("Josh", genericMap.get("person.value"));
        assertEquals("###Josh###", sortedMap.get("person.transformed"));
        assertTrue(Person.class.isAssignableFrom(customMap.get("person.object").getClass()));
        assertNotNull(sortedMap.get("thepersonid"));
    }

    @Test
    public void selectMapDtoTest() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestPerson(creator.createTestTown(), "Josh");

        Person person = query.from(Person.class);
        @SuppressWarnings("unchecked")
        Map<String, String> mapPx = query.select(Map.class);
        mapPx.put("person.value", person.getName());

        validate("select hobj1.name as person_value from Person hobj1");
        assertTrue(Map.class.isAssignableFrom(doQueryResult.get(0).getClass()));

        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) doQueryResult.get(0);
        assertEquals("Josh", result.get("person.value"));
    }

    @Test
    public void orderByMapValueTest() {
        Person person = query.from(Person.class);
        @SuppressWarnings("unchecked")
        Map<String, String> mapPx = query.select(Map.class);
        mapPx.put("person.value", person.getName());
        query.orderBy().desc(mapPx.get("person.value"));

        validate("select hobj1.name as person_value from Person hobj1 order by hobj1.name desc");
    }

    @Test
    public void selectMultiJoinedEntityValues() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Person personData = creator.createTestPerson(creator.createTestTown(), "Bob");
        String favoriteColor = creator.createPersonProperty(personData, "FavoriteColor").getPropertyValue();
        String favoriteDish = creator.createPersonProperty(personData, "FavoriteDish").getPropertyValue();
        creator.createPersonProperty(personData, "FavoriteDance");

        Person person = query.from(Person.class);
        PersonProperty property1 = query.join(person.getProperties());
        PersonProperty property2 = query.join(person.getProperties(), defaultJoin().createAdditionalJoin().build());

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
        MutableTriple<Long, String, Object> actual = getSingleQueryResults();
        assertEquals(personData.getId(), actual.getLeft());
        assertEquals(favoriteColor, actual.getMiddle());
        assertEquals(favoriteDish, actual.getRight());
    }

    @Test
    public void selectCompositeTypeValues() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();

        Building building = query.from(Building.class);

        @SuppressWarnings("unchecked")
        MutablePair<String, Style> result = query.select(MutablePair.class);
        result.setLeft(building.getAddress().getNumber());
        result.setRight(building.getStyle());

        validate("select hobj1.address.number as left, hobj1.style as right from Building hobj1");
        MutablePair<String, Style> actual = getSingleQueryResults();
        assertEquals(houseData.getAddress().getNumber(), actual.getLeft());
        assertEquals(houseData.getStyle(), actual.getRight());
    }

    @Test
    public void selectUserTypeValue() {
        House houseData = new TestDataCreator(getSessionFactory()).createRandomHouse();

        Building building = query.from(Building.class);

        @SuppressWarnings("unchecked")
        MutablePair<TextWrappingObject, Style> result = query.select(MutablePair.class);
        result.setLeft(building.getText());
        result.setRight(building.getStyle());

        validate("select hobj1.text as left, hobj1.style as right from Building hobj1");
        MutablePair<TextWrappingObject, Style> actual = getSingleQueryResults();
        assertEquals(houseData.getText(), actual.getLeft());
        assertEquals(houseData.getStyle(), actual.getRight());
    }

    @Test
    public void selectToValueOfTransformSelectedValue() {
        Town town = query.from(Town.class);
        TypeSafeValue<String> toValue = query.toValue(query.select(
                String.class, town.getId(), Object::toString));
        TownDetailsDto details = query.select(TownDetailsDto.class);
        details.setName(toValue.select());
    }

    @Test
    public void selectToValueOfTransformSelectedValueObject() {
        new TestDataCreator(getSessionFactory()).createTestTown();

        Town town = query.from(Town.class);
        TypeSafeValue<String> toValue = query.toValue(query
                .select(String.class, town, Town::getName));
        TownDetailsDto details = query.select(TownDetailsDto.class);
        details.setName(toValue.select());
        validate("select hobj1 as name from Town hobj1");

        TownDetailsDto result = getSingleQueryResults();
        assertEquals("TestTown", result.getName());
    }

    @Test
    public void selectToValueOfTransformSelectedValueObject2() {
        new TestDataCreator(getSessionFactory()).createTestTown();

        Town town = query.from(Town.class);
        CaseTypeSafeValue<Long> townCaseTypeSafeValue = query.caseWhenValue(Long.class);
        townCaseTypeSafeValue.is(town.getId()).when(town.getName()).eq("Abc");
        townCaseTypeSafeValue.is(15L).otherwise();

        TypeSafeValue<String> toValue = query.toValue(query.select(String.class,
                townCaseTypeSafeValue.select(), Object::toString));
        TownDetailsDto details = query.select(TownDetailsDto.class);
        details.setName(toValue.select());
        validate("select (case when (hobj1.name = 'Abc') then hobj1.id else 15 end) as name from Town hobj1");

        TownDetailsDto result = getSingleQueryResults();
        assertEquals("15", result.getName());
    }

    @Test
    public void selectComponentTypeValues() {
        Town townData = new TestDataCreator(getSessionFactory()).createTestTown();
        Town town = query.from(Town.class);

        @SuppressWarnings("unchecked")
        MutablePair<Double, Double> result = query.select(MutablePair.class);
        result.setLeft(town.getGeographicCoordinate().getLongitude());
        result.setRight(town.getGeographicCoordinate().getLattitude());

        validate("select hobj1.geographicCoordinate.longitude as left, hobj1.geographicCoordinate.lattitude as right from Town hobj1");
        MutablePair<Double, Double> actual = getSingleQueryResults();
        assertEquals(townData.getGeographicCoordinate().getLongitude(), actual.getLeft(), 0);
        assertEquals(townData.getGeographicCoordinate().getLattitude(), actual.getRight(), 0);
    }

    @Test
    public void selectNestedComponentTypeValues() {
        Product productData = new TestDataCreator(getSessionFactory()).createRandomProduct();
        Product product = query.from(Product.class);

        @SuppressWarnings("unchecked")
        MutableTriple<String, String, Boolean> triple = query.select(MutableTriple.class);
        triple.setLeft(product.getName());
        triple.setMiddle(product.getProductProperties().getPlanning().getAlgorithm());
        triple.setRight(product.getProductProperties().getSales().isSalesAllowed());

        validate("select hobj1.name as left, hobj1.productProperties.planning.algorithm as middle, hobj1.productProperties.sales.salesAllowed as right from Product hobj1");
        MutableTriple<String, String, Boolean> actual = getSingleQueryResults();
        assertEquals(productData.getName(), actual.getLeft());
        assertEquals(productData.getProductProperties().getPlanning().getAlgorithm(), actual.getMiddle());
        assertEquals(productData.getProductProperties().getSales().isSalesAllowed(), actual.getRight());

    }

    @Test
    public void selectNestedSelectionPropertyDto() {
        Product productData = new TestDataCreator(getSessionFactory()).createRandomProduct();
        Product product = query.from(Product.class);

        Product productDto = query.select(Product.class);
        productDto.setName(product.getName());
        productDto.getProductProperties().getPlanning().setAlgorithm(product.getName());

        validate("select hobj1.name as name, hobj1.name as g2__algorithm from Product hobj1");

        Product actual = getSingleQueryResults();
        assertEquals(productData.getName(), actual.getName());
        assertEquals(productData.getName(), actual.getProductProperties().getPlanning().getAlgorithm());
    }

    /**
     * Use a transformer to select a value into a selection dto which doesn't comply with the original
     * and is converted in code instead of in the query.
     */
    @Test
    public void selectSimpleTransformedValue() {
        Product productData = new TestDataCreator(getSessionFactory()).createRandomProduct();
        Product product = query.from(Product.class);

        Product productDto = query.select(Product.class);
        productDto.setName(product.getName());
        productDto.getProductProperties().setPlanning(query.select(PlanningProperties.class,
                product.getName(), new StringToPlanningPropertiesTransformer()));

        validate("select hobj1.name as name, hobj1.name as g1__planning from Product hobj1");
        Product actual = getSingleQueryResults();
        assertEquals(productData.getName(), actual.getName());
        assertEquals(productData.getName(), actual.getProductProperties().getPlanning().getAlgorithm());
    }

    /**
     * Select encoded string property into a date field.
     */
    @Test
    public void selectDateTransformedValue() throws ParseException {
        Product productData = new TestDataCreator(getSessionFactory()).createRandomProduct();
        productData.getManyProperties().setProperty1(DateFormat.getDateInstance().format(new Date()));
        getSessionFactory().getCurrentSession().save(productData);

        Product product = query.from(Product.class);

        ProductDetailsDto dto = query.select(ProductDetailsDto.class);
        dto.setId(product.getId());
        dto.setValidUntilDate(query.select(Date.class,
                product.getManyProperties().getProperty1(),
                (a) -> {
                    try {
                        return DateFormat.getDateInstance().parse(a);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
        }));

        validate("select hobj1.id as id, hobj1.manyProperties.property1 as validUntilDate from Product hobj1");
        ProductDetailsDto actual = getSingleQueryResults();
        assertEquals(productData.getId(), actual.getId());
        assertEquals(DateFormat.getDateInstance().parse(productData.getManyProperties().getProperty1()),
                actual.getValidUntilDate());
    }

    /**
     * Select an object and use some of the values of this object to transform into another dto.
     * <p>
     * Using a proxy as select value to get values from it should be avoided. Only do this
     * if there is no other option! (value on a dto without a default constructor for example)
     */
    @SuppressWarnings({ "unchecked" })
    @Test
    public void selectProxyTransformedValue() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town townData = creator.createTestTown();
        Person personAData = creator.createTestPerson(townData, "A");
        Person personBData = creator.createTestPerson(townData, "B");
        creator.createRelation(personAData, personBData);

        Person person = query.from(Person.class);
        Relation relation = query.join(person.getChildRelations());

        MutablePair<Long, ImmutablePair<Long, Integer>> select = query.select(MutablePair.class);
        select.setLeft(person.getId());
        select.setRight(query.select(ImmutablePair.class, relation.getChild(),
                (a) -> new ImmutablePair<>(a.getId(), a.getAge())));

        validate("select hobj1.id as left, hobj3 as right from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3");
        MutablePair<Long, ImmutablePair<Long, Integer>> actual = getSingleQueryResults();
        assertEquals(personAData.getId(), actual.getLeft());
        assertEquals(personBData.getId(), actual.getRight().getLeft());
        assertEquals(personBData.getAge(), actual.getRight().getRight().intValue());

    }

    @Test
    public void selectProxyClassTwiceShouldntFail() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();
        Person personData = creator.createTestPerson(town, "Anonymous");

        Person person = query.from(Person.class);

        PersonDto personDto = query.select(PersonDto.class);
        personDto.setPersonAge(person.getAge());

        // second select proxy, can be a new instance, it's only a
        // bridge to add the projections after all
        personDto = query.select(PersonDto.class);
        personDto.setThePersonsName(person.getName());

        validate("select hobj1.age as personAge, hobj1.name as thePersonsName from Person hobj1");

        PersonDto actual = getSingleQueryResults();
        assertEquals(personData.getAge(), actual.getPersonAge());
        assertEquals(personData.getName(), actual.getThePersonsName());
    }

    @Test
    public void selectBuilderBasic() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();
        Person testPerson = creator.createTestPerson(town, "Anonymous");

        Person person = query.from(Person.class);

        ValueDto.ValueDtoBuilder builder = query.select(ValueDto::builder);
        builder.id(person.getId());

        ValueSubDto.ValueSubDtoBuilder<?,?> subValueBuilder = query.selectMergeValues(builder,
                ValueSubDto.ValueSubDtoBuilder.class,
                (partialResult, parallelDto) -> partialResult.value(parallelDto.build()));
        subValueBuilder
                .id(person.getTown().getId())
                .quantity(5d);

        validate("select hobj1.id as id, hobj1.town.id as g1__id, 5.0 as g1__quantity from Person hobj1");

        ValueDto actual = getSingleQueryResults();

        assertEquals(testPerson.getId(), actual.getId());
        assertEquals(town.getId(), actual.getValue().getId());
        assertEquals(5d, actual.getValue().getQuantity(), 0d);
    }

    @Test
    public void selectBuilderAdvanced() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();
        Person testPerson = creator.createTestPerson(town, "Anonymous");

        Person person = query.from(Person.class);

        ValueDto.ValueDtoBuilder builder = query.select(ValueDto::builder);
        ValueSubDto.ValueSubDtoBuilder<?, ?> subBuilder = query.subBuilder(ValueSubDto::builder);

        builder.id(person.getId());
        builder.value(subBuilder
                    .id(person.getTown().getId())
                    .quantity(5d)
                .build());

        validate("select hobj1.id as id, hobj1.town.id as g1__id, 5.0 as g1__quantity from Person hobj1");

        @SuppressWarnings("unchecked")
        List<ValueDto> valueDtos = (List<ValueDto>) this.doQueryResult;

        assertEquals(testPerson.getId(), valueDtos.get(0).getId());
        assertEquals(town.getId().longValue(), valueDtos.get(0).getValue().getId().longValue());
        assertEquals(5d, valueDtos.get(0).getValue().getQuantity(), 0d);
    }

    @Test
    public void selectBuilderAdvanced2() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();
        Person testPerson = creator.createTestPerson(town, "Anonymous");

        Person person = query.from(Person.class);

        ValueDto.ValueDtoBuilder builder = query.select(ValueDto::builder);
        ValueSubDto.ValueSubDtoBuilder<?, ?> subBuilder = query.subBuilder(ValueSubDto::builder);
        ValueSubDto.ValueSubDtoBuilder<?, ?> subSubBuilder = query.subBuilder(ValueSubDto::builder);
        //ValueSubDto.ValueSubDtoBuilder<?,?> subBuilder = query.subSelect(builder, ValueSubDto.ValueSubDtoBuilder.class);

        builder.value(subBuilder
                .subDto(subSubBuilder
                        .id(person.getTown().getId())
                        .quantity(5d)
                        .build())
                .build());

        validate("select hobj1.town.id as g2__id, 5.0 as g2__quantity from Person hobj1");

        @SuppressWarnings("unchecked")
        List<ValueDto> valueDtos = (List<ValueDto>) this.doQueryResult;

        assertEquals(town.getId().longValue(), valueDtos.get(0).getValue().getSubDto().getId().longValue());
        assertEquals(5d, valueDtos.get(0).getValue().getSubDto().getQuantity(), 0d);
    }
}
