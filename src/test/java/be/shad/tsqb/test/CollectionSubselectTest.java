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

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;
import org.junit.Test;

import be.shad.tsqb.domain.DomainObject;
import be.shad.tsqb.domain.GeographicCoordinate;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.dto.HasId;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.dto.PersonValue;
import be.shad.tsqb.dto.PersonValue.PersonValueBuilder;
import be.shad.tsqb.dto.TownDto;
import be.shad.tsqb.dto.TownValue;
import be.shad.tsqb.dto.TownValue.TownValueBuilder;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.selection.collection.IdentityFieldProvider;
import be.shad.tsqb.selection.collection.ResultIdentifierBinder;
import be.shad.tsqb.selection.parallel.SelectPair;
import be.shad.tsqb.selection.parallel.SelectTriplet;
import be.shad.tsqb.selection.parallel.SelectValue;
import be.shad.tsqb.selection.parallel.SelectionMerger1;

public class CollectionSubselectTest extends TypeSafeQueryTest {
    private final IdentityFieldProvider<DomainObject> identifierProvider =
            new IdentityFieldProvider<DomainObject>() {
        @Override
        protected Object getIdentifier(DomainObject resultProxy) {
            return resultProxy.getId();
        }
    };

    private final IdentityFieldProvider<HasId> hasIdIdentifierProvider =
            new IdentityFieldProvider<HasId>() {
        @Override
        protected Object getIdentifier(HasId resultProxy) {
            return resultProxy.getId();
        }
    };

    private TestDataCreator creator;
    private Set<String> defaultNames;

    @Override
    public void initialize() {
        super.initialize();
        creator = new TestDataCreator(getSessionFactory());
        defaultNames = new HashSet<>();
        defaultNames.add("JohnyTheKid");
        defaultNames.add("Josh");
        defaultNames.add("Albert");
    }

    /**
     *
     */
    @Test
    public void testLCollectionSubselect() {
        Town town = creator.createTestTownWithPeople(defaultNames);

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());

        Town selectTown = query.select(Town.class, identifierProvider);
        Person selectPerson = query.select(selectTown.getInhabitants(), Person.class, null);

        selectTown.setId(townProxy.getId());
        selectPerson.setName(inhabitant.getName());

        validate("select hobj1.id as id, hobj2.name as g1__name "
                + "from Town hobj1 join hobj1.inhabitants hobj2");

        Town townResult = getSingleQueryResults();
        assertEquals(town.getId(), townResult.getId());
        assertEquals(3, townResult.getInhabitants().size());

        for(Person p: townResult.getInhabitants()) {
            assertTrue(defaultNames.contains(p.getName()));
        }
    }

    @Test
    public void testLeftJoinedCollectionSubselect() {
        Town townWithPeople = creator.createTestTownWithPeople(defaultNames);
        Town townWithoutPeople = creator.createTestTown();

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants(), JoinType.Left);

        Town selectTown = query.select(Town.class, identifierProvider);
        Person selectPerson = query.select(selectTown.getInhabitants(), Person.class, null);

        query.orderBy().asc(townProxy.getId());

        selectTown.setId(townProxy.getId());
        selectPerson.setName(inhabitant.getName());

        validate("select hobj1.id as id, hobj2.name as g1__name "
                + "from Town hobj1 left join hobj1.inhabitants hobj2 "
                + "order by hobj1.id");

        assertEquals(2, doQueryResult.size());
        if (!(doQueryResult.get(0) instanceof Town)) {
            fail("Expected to find a town as result.");
        }
        Town townWithPeopleResult = (Town) doQueryResult.get(0);
        Town townWithoutPeopleResult = (Town) doQueryResult.get(1);
        assertEquals(townWithPeopleResult.getId(), townWithPeople.getId());
        assertEquals(townWithoutPeopleResult.getId(), townWithoutPeople.getId());
        assertEquals(3, townWithPeopleResult.getInhabitants().size());
        assertNotNull(townWithoutPeopleResult.getInhabitants());
        assertEquals(0, townWithoutPeopleResult.getInhabitants().size());

        for(Person p: townWithPeopleResult.getInhabitants()) {
            assertTrue(defaultNames.contains(p.getName()));
        }
    }

    @Test
    public void testSubSelectIdentity() {
        Town town = creator.createTestTownWithPeople(Arrays.asList("AA1", "AA2", "AA3", "BA1", "BA2", "BA3"));
        Town town2 = creator.createTestTownWithPeople(Arrays.asList("AB1", "AB2", "AB3", "BB1", "BB2", "BB3"));

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants(), JoinType.Left);
        query.where(inhabitant.getName()).startsWith("A");
        query.orderBy().asc(townProxy.getId());

        TownValueBuilder selectTown = query.select(TownValue::builder);
        PersonValueBuilder selectPerson = query.subListBuilder(PersonValue::builder, selectTown::inhabitants);
        GeographicCoordinate selectCoordinates = query.subBuilder(GeographicCoordinate::new);

        selectTown.geographicCoordinate(query.groupSelectBy(selectCoordinates));
        selectCoordinates.setLattitude(townProxy.getGeographicCoordinate().getLattitude());
        selectCoordinates.setLongitude(townProxy.getGeographicCoordinate().getLongitude());
        selectPerson.thePersonsName(inhabitant.getName());

        validate("select hobj1.geographicCoordinate.lattitude as g2__lattitude, "
                + "hobj1.geographicCoordinate.longitude as g2__longitude, "
                + "hobj2.name as g1__thePersonsName "
                + "from Town hobj1 left join hobj1.inhabitants hobj2 "
                + "where hobj2.name like :np1 "
                + "order by hobj1.id", "A%");

        TownValue townResult = getSingleQueryResults();
        Assert.assertEquals(Arrays.asList("AA1", "AA2", "AA3", "AB1", "AB2", "AB3"),
                townResult.getInhabitants().stream()
                        .map(PersonValue::getThePersonsName)
                        .sorted()
                        .collect(toList()));
    }

    @Test
    public void testSubSelectIdentityInPair() {
        Town town = creator.createTestTownWithPeople(Arrays.asList("AA1", "AA2", "AA3", "BA1", "BA2", "BA3"));
        Town town2 = creator.createTestTownWithPeople(Arrays.asList("AB1", "AB2", "AB3", "BB1", "BB2", "BB3"));

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants(), JoinType.Left);
        query.where(inhabitant.getName()).startsWith("A");
        query.orderBy().asc(townProxy.getId());

        @SuppressWarnings("unchecked")
        SelectPair<GeographicCoordinate, List<PersonValue>> selectPx = query.select(SelectPair.class);
        PersonValueBuilder selectPerson = query.subListBuilder(PersonValue::builder, selectPx::setSecond);
        GeographicCoordinate selectCoordinates = query.subBuilder(GeographicCoordinate::new);

        selectPx.setFirst(query.groupSelectBy(selectCoordinates));
        selectCoordinates.setLattitude(townProxy.getGeographicCoordinate().getLattitude());
        selectCoordinates.setLongitude(townProxy.getGeographicCoordinate().getLongitude());
        selectPerson.thePersonsName(inhabitant.getName());

        validate("select hobj1.geographicCoordinate.lattitude as g2__lattitude, "
                + "hobj1.geographicCoordinate.longitude as g2__longitude, "
                + "hobj2.name as g1__thePersonsName "
                + "from Town hobj1 left join hobj1.inhabitants hobj2 "
                + "where hobj2.name like :np1 "
                + "order by hobj1.id", "A%");

        SelectPair<GeographicCoordinate, List<PersonValue>> townResult = getSingleQueryResults();
        Assert.assertEquals(Arrays.asList("AA1", "AA2", "AA3", "AB1", "AB2", "AB3"),
                townResult.getSecond().stream()
                        .map(PersonValue::getThePersonsName)
                        .sorted()
                        .collect(toList()));

    }

    /**
     * Test possibility to use merge value on collection value.
     */
    @Test
    public void testCollectionSubselectWithValueMerger() {
        Town town = creator.createTestTownWithPeople(defaultNames);

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());
        query.where(inhabitant.getName()).startsWith("Jo");

        Town selectTown = query.select(Town.class, identifierProvider);
        Person selectPerson = query.select(selectTown.getInhabitants(), Person.class, null);

        selectTown.setId(townProxy.getId());
        selectPerson.setAge(inhabitant.getAge());

        final MutableInt counter = new MutableInt(0);
        SelectValue<Long> mergeValue = query.selectMergeValues(selectPerson,
                new SelectionMerger1<Person, Long>() {
            @Override
            public void mergeValueIntoResult(Person partialResult, Long value) {
                partialResult.setId(value);
                counter.increment();
            }
        });
        mergeValue.setValue(inhabitant.getId());

        validate("select hobj1.id as id, hobj2.age as g1__age, hobj2.id as g2__value "
                + "from Town hobj1 join hobj1.inhabitants hobj2 where hobj2.name like :np1", "Jo%");

        assertEquals(1, doQueryResult.size());
        if (!(doQueryResult.get(0) instanceof Town)) {
            fail("Expected to find a town as result.");
        }
        Town townResult = (Town) doQueryResult.get(0);
        assertEquals(town.getId(), townResult.getId());
        assertEquals(2, counter.getValue().intValue());
    }

    /**
     * Test possibility to use merge value on collection value.
     */
    @Test
    public void testCollectionSubselectWithValueObject() {
        Town town = creator.createTestTownWithPeople(defaultNames);

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());
        query.where(inhabitant.getName()).startsWith("Jo");

        TownValueBuilder selectTown = query.select(TownValue::builder);
        PersonValueBuilder selectPerson = query.subSetBuilder(PersonValue::builder, selectTown::inhabitants);

        selectTown.id(query.groupSelectBy(townProxy.getId()));
        selectPerson.personAge(inhabitant.getAge());

        validate("select hobj1.id as id, hobj2.age as g1__personAge "
                + "from Town hobj1 join hobj1.inhabitants hobj2 where hobj2.name like :np1", "Jo%");

        assertEquals(1, doQueryResult.size());
         if (!(doQueryResult.get(0) instanceof TownValue)) {
            fail("Expected to find a town as result.");
        }
        TownValue townResult = (TownValue) doQueryResult.get(0);
        assertEquals(town.getId(), townResult.getId());
    }

    @Test
    public void testCollectionSubselectWithEmbeddedValue() {
        Town town = creator.createTestTownWithPeople(defaultNames);

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());
        query.where(inhabitant.getName()).startsWith("Jo");

        Town selectTown = query.select(Town.class, identifierProvider);
        Person selectPerson = query.select(selectTown.getInhabitants(), Person.class, null);

        selectTown.setId(townProxy.getId());
        selectPerson.setId(inhabitant.getId());
        selectTown.getGeographicCoordinate().setLattitude(townProxy.getGeographicCoordinate().getLattitude());

        validate("select hobj1.id as id, hobj2.id as g1__id, hobj1.geographicCoordinate.lattitude as g2__lattitude "
                + "from Town hobj1 join hobj1.inhabitants hobj2 where hobj2.name like :np1", "Jo%");

        assertEquals(1, doQueryResult.size());
        if (!(doQueryResult.get(0) instanceof Town)) {
            fail("Expected to find a town as result.");
        }
        Town townResult = (Town) doQueryResult.get(0);
        assertEquals(town.getId(), townResult.getId());
        assertEquals(2, townResult.getInhabitants().size());
    }

    @Test
    public void testNotSelectingIdentityShouldStillYieldFormattedString() {
        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());
        Town selectTown = query.select(Town.class, identifierProvider);
        Person selectPerson = query.select(selectTown.getInhabitants(), Person.class, null);
        selectTown.setName(townProxy.getName());
        selectPerson.setId(inhabitant.getId());
        assertEquals("\n    select"
        		+ "\n        hobj1.name as name,"
                + "\n        hobj2.id as g1__id "
                + "\n    from"
                + "\n        Town hobj1 "
                + "\n    join"
                + "\n        hobj1.inhabitants hobj2"
                + "\n --- with: []", query.toFormattedString().replace("\r", ""));
    }

    @Test(expected=IllegalStateException.class)
    public void testNotSelectingIdentityShouldYieldException() {
        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());
        Town selectTown = query.select(Town.class, identifierProvider);
        Person selectPerson = query.select(selectTown.getInhabitants(), Person.class, null);
        selectTown.setName(townProxy.getName());
        selectPerson.setId(inhabitant.getId());
        validate("Should throw illegal state because some parent values but not the binding value was not selected.");
    }

    @Test(expected=IllegalStateException.class)
    public void testNotSelectingAnyParentFieldShouldYieldException() {
        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());
        Town selectTown = query.select(Town.class, identifierProvider);
        Person selectPerson = query.select(selectTown.getInhabitants(), Person.class, null);
        selectPerson.setId(inhabitant.getId());
        validate("Should throw illegal state because no parent values were selected.");
    }

    @Test
    public void testNestedCollectionsSelection() {
        Town town = creator.createTestTown();
        Person johny = creator.createTestPerson(town, "Johny");
        Person angie = creator.createTestPerson(town, "Angie");
        Person josh = creator.createTestPerson(town, "Josh");
        Person alberta = creator.createTestPerson(town, "Alberta");

        Person becky = creator.createTestPerson(town, "Becky");
        Person fred = creator.createTestPerson(town, "Fred");

        // johny has 2 kids, angie and alberta have 1, josh has none
        creator.addChildRelation(johny, becky);
        creator.addChildRelation(angie, becky);
        creator.addChildRelation(johny, fred);
        creator.addChildRelation(alberta, fred);

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());
        Relation child = query.join(inhabitant.getChildRelations(), JoinType.Left);

        TownDto selectTown = query.select(TownDto.class, hasIdIdentifierProvider);
        PersonDto selectParent = query.select(selectTown.getInhabitants(),
                PersonDto.class, hasIdIdentifierProvider);
        PersonDto selectChild = query.select(selectParent.getChildren(),
                PersonDto.class, hasIdIdentifierProvider);

        selectTown.setId(townProxy.getId());
        selectParent.setId(inhabitant.getId());
        selectParent.setThePersonsName(inhabitant.getName());
        selectChild.setId(child.getChild().getId());
        selectChild.setThePersonsName(child.getChild().getName());

        validate("select hobj1.id as id, hobj2.id as g1__id, hobj2.name as g1__thePersonsName, "
                + "hobj4.id as g2__id, hobj4.name as g2__thePersonsName "
                + "from Town hobj1 "
                + "join hobj1.inhabitants hobj2 "
                + "left join hobj2.childRelations hobj3 "
                + "left join hobj3.child hobj4");

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<TownDto> results = (List) doQueryResult;
        assertEquals(1, results.size());
        TownDto townResult = results.get(0);
        assertEquals(6, townResult.getInhabitants().size());

        Map<Long, Set<Long>> resultIds = new HashMap<>();
        for(PersonDto dto: townResult.getInhabitants()) {
            Set<Long> childIds = new HashSet<>();
            if (dto.getChildren() != null) {
                for(PersonDto dtoChild: dto.getChildren()) {
                    childIds.add(dtoChild.getId());
                }
            }
            resultIds.put(dto.getId(), childIds);
        }
        assertEquals(toIds(becky, fred), resultIds.get(johny.getId()));
        assertEquals(toIds(becky), resultIds.get(angie.getId()));
        assertEquals(emptySet(), resultIds.get(josh.getId()));
        assertEquals(toIds(fred), resultIds.get(alberta.getId()));
        assertEquals(emptySet(), resultIds.get(becky.getId()));
        assertEquals(emptySet(), resultIds.get(fred.getId()));
    }

    @Test
    public void testNestedCollectionsSelectionWithValues() {
        Town town = creator.createTestTown();
        Person johny = creator.createTestPerson(town, "Johny");
        Person angie = creator.createTestPerson(town, "Angie");
        Person josh = creator.createTestPerson(town, "Josh");
        Person alberta = creator.createTestPerson(town, "Alberta");

        Person becky = creator.createTestPerson(town, "Becky");
        Person fred = creator.createTestPerson(town, "Fred");

        // johny has 2 kids, angie and alberta have 1, josh has none
        creator.addChildRelation(johny, becky);
        creator.addChildRelation(angie, becky);
        creator.addChildRelation(johny, fred);
        creator.addChildRelation(alberta, fred);

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());
        Relation child = query.join(inhabitant.getChildRelations(), JoinType.Left);

        TownValue.TownValueBuilder selectTown = query.select(TownValue::builder);
        PersonValue.PersonValueBuilder selectParent = query.subSetBuilder(PersonValue::builder, selectTown::inhabitants);
        PersonValue.PersonValueBuilder selectChild = query.subListBuilder(PersonValue::builder, selectParent::children);

        selectTown.id(query.groupSelectBy(townProxy.getId()));
        selectParent.id(query.groupSelectBy(inhabitant.getId()));
        selectParent.thePersonsName(inhabitant.getName());
        selectChild.id(child.getChild().getId());
        selectChild.thePersonsName(child.getChild().getName());

        validate("select hobj1.id as id, hobj2.id as g1__id, hobj2.name as g1__thePersonsName, "
                + "hobj4.id as g2__id, hobj4.name as g2__thePersonsName "
                + "from Town hobj1 "
                + "join hobj1.inhabitants hobj2 "
                + "left join hobj2.childRelations hobj3 "
                + "left join hobj3.child hobj4");

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<TownValue> results = (List) doQueryResult;
        assertEquals(1, results.size());
        TownValue townResult = results.get(0);
        assertEquals(6, townResult.getInhabitants().size());

        Map<Long, Set<Long>> resultIds = new HashMap<>();
        for(PersonValue dto: townResult.getInhabitants()) {
            Set<Long> childIds = new HashSet<>();
            if (dto.getChildren() != null) {
                for(PersonValue dtoChild: dto.getChildren()) {
                    childIds.add(dtoChild.getId());
                }
            }
            resultIds.put(dto.getId(), childIds);
        }
        assertEquals(toIds(becky, fred), resultIds.get(johny.getId()));
        assertEquals(toIds(becky), resultIds.get(angie.getId()));
        assertEquals(emptySet(), resultIds.get(josh.getId()));
        assertEquals(toIds(fred), resultIds.get(alberta.getId()));
        assertEquals(emptySet(), resultIds.get(becky.getId()));
        assertEquals(emptySet(), resultIds.get(fred.getId()));
    }

    private Set<Long> toIds(DomainObject... dtos) {
        Set<Long> ids = new HashSet<>();
        for(DomainObject dto: dtos) {
            ids.add(dto.getId());
        }
        return ids;
    }

    /**
     * Test binding multiple values
     */
    @Test
    public void testGroupSelectionByMultiField() {
        Town townA = creator.createTestTown();
        Town townB = creator.createTestTown();
        Person johnyTheKidA = createPersonWithAge(creator, townA, "JohnyTheKid", 10);
        Person joshA =        createPersonWithAge(creator, townA, "Josh", 30);
        Person bertaA =       createPersonWithAge(creator, townA, "Berta", 30);
        Person albertA =      createPersonWithAge(creator, townA, "Albert", 50);
        Person johnyTheKidB = createPersonWithAge(creator, townB, "JohnyTheKid", 10);
        Person joshB =        createPersonWithAge(creator, townB, "Josh", 10);
        Person bertaB =       createPersonWithAge(creator, townB, "Berta", 50);
        Person albertB =      createPersonWithAge(creator, townB, "Albert", 50);

        // we will be grouping by townId and personAge
        ResultIdentifierBinder<SelectTriplet<Long, Integer, Collection<PersonDto>>> identityBinder =
                (binding, triplet) -> {
                    binding.bind(triplet.getFirst());
                    binding.bind(triplet.getSecond());
                };

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());

        @SuppressWarnings("unchecked")
        SelectTriplet<Long, Integer, Collection<PersonDto>> selectTriplet = query.select(SelectTriplet.class, identityBinder);
        selectTriplet.setFirst(townProxy.getId());
        selectTriplet.setSecond(inhabitant.getAge());

        PersonDto selectPerson = query.select(selectTriplet.getThird(), PersonDto.class, null);
        selectPerson.setId(inhabitant.getId());
        selectPerson.setPersonAge(inhabitant.getAge());
        selectPerson.setThePersonsName(inhabitant.getName());

        validate("select hobj1.id as first, hobj2.age as second, hobj2.id as g1__id, "
                + "hobj2.age as g1__personAge, hobj2.name as g1__thePersonsName "
                + "from Town hobj1 join hobj1.inhabitants hobj2");

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<SelectTriplet<Long, Integer, Collection<PersonDto>>> results = (List) doQueryResult;
        assertEquals(5, doQueryResult.size());
        if (!(doQueryResult.get(0) instanceof SelectTriplet)) {
            fail("Expected to find a SelectTriplet as result.");
        }
        checkValue(results.get(0), townA, johnyTheKidA);
        checkValue(results.get(1), townA, joshA, bertaA);
        checkValue(results.get(2), townA, albertA);
        checkValue(results.get(3), townB, johnyTheKidB, joshB);
        checkValue(results.get(4), townB, bertaB, albertB);
    }

    private void checkValue(SelectTriplet<Long, Integer, Collection<PersonDto>> selectTriplet, Town town, Person... people) {
        if (!town.getId().equals(selectTriplet.getFirst())) {
            fail("Town not correct." + selectTriplet);
        }
        Set<Long> expected = new HashSet<>();
        for(Person person: people) {
            expected.add(person.getId());
        }
        Set<Long> result = new HashSet<>();
        for(PersonDto person: selectTriplet.getThird()) {
            result.add(person.getId());
        }
        assertEquals(expected, result);
    }

    private Person createPersonWithAge(TestDataCreator creator, Town town, String name, int age) {
        Person testPerson = creator.createTestPerson(town, name);
        testPerson.setAge(age);
        getSessionFactory().getCurrentSession().save(testPerson);
        return testPerson;
    }

}
