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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import be.shad.tsqb.domain.DomainObject;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.selection.collection.IdentityFieldProvider;
import be.shad.tsqb.selection.collection.ResultIdentifierBinder;
import be.shad.tsqb.selection.collection.ResultIdentifierBinding;
import be.shad.tsqb.selection.parallel.SelectTriplet;

public class CollectionSubselectTest extends TypeSafeQueryTest {
    private final IdentityFieldProvider<DomainObject> identifierProvider = 
            new IdentityFieldProvider<DomainObject>() {
        @Override
        protected Object getIdentifier(DomainObject resultProxy) {
            return resultProxy.getId();
        }
    };
    
    @Test
    public void testCollectionSubselect() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();
        creator.createTestPerson(town, "JohnyTheKid");
        creator.createTestPerson(town, "Josh");
        creator.createTestPerson(town, "Albert");
        
        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());
        query.where(inhabitant.getName()).startsWith("Jo");

        Town selectTown = query.select(Town.class, identifierProvider);
        Person selectPerson = query.select(selectTown.getInhabitants(), Person.class, null);
        
        selectTown.setId(townProxy.getId());
        selectPerson.setId(inhabitant.getId());
        selectTown.getGeographicCoordinate().setLattitude(townProxy.getGeographicCoordinate().getLattitude());
        
        validate("select hobj1.id as id, hobj2.id as g1__id, hobj1.geographicCoordinate.lattitude as geographicCoordinate_lattitude "
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
    public void testGroupSelectionByMultiFieldWithCompositeField() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
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
                new ResultIdentifierBinder<SelectTriplet<Long, Integer, Collection<PersonDto>>>()  {
            @Override
            public void bind(ResultIdentifierBinding binding, SelectTriplet<Long, Integer, Collection<PersonDto>> triplet) {
                binding.bind(triplet.getFirst());
                binding.bind(triplet.getSecond());
            }
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
        assertEquals(5, results.size());
        if (!(results.get(0) instanceof SelectTriplet)) {
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
