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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import be.shad.tsqb.domain.DomainObject;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.dto.HasId;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.dto.TownDto;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.selection.collection.IdentityFieldProvider;

/**
 * @author Gert
 *
 */
public class BatchFetchedResultsTest extends TypeSafeQueryTest {

    /**
     * Test the expected amount of results are fetched when specifying the batch size
     */
    @Test
    public void testBatchedInQuery() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();


        int n = 10;
        List<Long> ids = new ArrayList<>(n);
        for (long i=0; i < n; i++) {
            Person savedPerson = creator.createTestPerson(town, "P" + i);
            ids.add(savedPerson.getId());
        }
        for (long i=0; i < n; i++) {
            creator.createTestPerson(town, "P" + i);
        }

        Person person = query.from(Person.class);
        query.where(person.getId()).in(ids, 2);

        validate("from Person hobj1 where hobj1.id in (:np1)", ids);
        assertEquals(n, doQueryResult.size());
    }

    @Test(expected=IllegalStateException.class)
    public void testMultipleBatchedInQueryNotAllowed() {
        Person person = query.from(Person.class);
        int n = 10;
        List<Long> ids = new ArrayList<>(n);
        for (long i=0; i < n; i++) {
            ids.add(i);
        }
        query.where(person.getId()).in(ids, 2).or(person.getSpouse().getId()).in(ids, 2);
        validate("will fail because of multiple batched params");
    }

    /**
     * If the batched values were transformed 1 at a time, then
     * the result would not contain just 1 item.
     */
    @Test
    public void testGroupedItemsAreStillGroupedWhenBatchSplitResults() {
        IdentityFieldProvider<HasId> hasIdIdentifierProvider =
                new IdentityFieldProvider<HasId>() {
            @Override
            protected Object getIdentifier(HasId resultProxy) {
                return resultProxy.getId();
            }
        };
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();
        Person johny = creator.createTestPerson(town, "Johny");
        Person angie = creator.createTestPerson(town, "Angie");
        Person josh = creator.createTestPerson(town, "Josh");
        Person alberta = creator.createTestPerson(town, "Alberta");

        Person becky = creator.createTestPerson(town, "Becky");
        Person fred = creator.createTestPerson(town, "Fred");
        Set<String> childNames = new HashSet<>();
        childNames.add(becky.getName());;
        childNames.add(fred.getName());

        // johny has 2 kids, angie and alberta have 1, josh has none
        creator.addChildRelation(johny, becky);
        creator.addChildRelation(angie, becky);
        creator.addChildRelation(johny, fred);
        creator.addChildRelation(alberta, fred);

        Town townProxy = query.from(Town.class);
        Person inhabitant = query.join(townProxy.getInhabitants());
        Relation child = query.join(inhabitant.getChildRelations(), JoinType.Left);

        query.where(child.getChild().getId()).isNull().or(child.getChild().getName()).in(childNames, 1);

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
                + "left join hobj3.child hobj4 "
                + "where hobj4.id is null or hobj4.name in (:np1)", childNames);

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
        assertEquals(resultIds.get(johny.getId()), toIds(becky, fred));
        assertEquals(resultIds.get(angie.getId()), toIds(becky));
        assertEquals(resultIds.get(josh.getId()), Collections.emptySet());
        assertEquals(resultIds.get(alberta.getId()), toIds(fred));
        assertEquals(resultIds.get(becky.getId()), Collections.emptySet());
        assertEquals(resultIds.get(fred.getId()), Collections.emptySet());
    }

    private Set<Long> toIds(DomainObject... dtos) {
        Set<Long> ids = new HashSet<>();
        for(DomainObject dto: dtos) {
            ids.add(dto.getId());
        }
        return ids;
    }
}
