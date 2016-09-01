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
package be.shad.tsqb.test.wiki;

import static be.shad.tsqb.restrictions.predicate.RestrictionPredicate.IGNORE_NULL_OR_EMPTY;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import be.shad.tsqb.domain.Product;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.dto.MapsDto;
import be.shad.tsqb.dto.PersonDto;
import be.shad.tsqb.dto.TownDto;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.selection.collection.IdentityFieldProvider;
import be.shad.tsqb.selection.parallel.SelectPair;
import be.shad.tsqb.selection.parallel.SelectionMerger2;
import be.shad.tsqb.selector.PersonSelector;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.TypeSafeValueFunctions;

@SuppressWarnings("unused")
public class WikiTests extends TypeSafeQueryTest {

    @Test
    public void testFrom() {
        Person person = query.from(Person.class);

        validate("from Person hobj1");
    }

    @Test
    public void testSelect() {
        Person person = query.from(Person.class);
        PersonDto personDto = query.select(PersonDto.class); // proxy instance of dto class
        personDto.setPersonAge(person.getAge());

        validate("select hobj1.age as personAge from Person hobj1");
    }

    @Test
    public void testSelectMap() {
        Person person = query.from(Person.class);
        // select creates proxy instance of dto class
        @SuppressWarnings("unchecked")
        Map<String, Object> map = query.select(Map.class);
        // binds person name to the personName key in the resulting map
        map.put("personName", person.getName());
        validate("select hobj1.name as personName from Person hobj1");
    }

    @Test
    public void testSelectNestedMap() {
        Person person = query.from(Person.class);
        // select creates proxy instance of dto class
        MapsDto maps = query.select(MapsDto.class);
        Map<String, Object> map = maps.getGenericMap();
        // binds person name to the personName key in the resulting map
        map.put("personName", person.getName());
        validate("select hobj1.name as genericMap_personName from Person hobj1");
    }

    @Test
    public void testWhere() {
        Person person = query.from(Person.class);
        query.where(person.getAge()).gt(50);

        validate("from Person hobj1 where hobj1.age > :np1", 50);
    }

    @Test
    public void testJoin() {
        Person parent = query.from(Person.class);

        // join and obtain a proxy of a collection element
        Relation childRelation = query.join(parent.getChildRelations());
        // join implicitly, returns a proxy of the getter type
        Person child = childRelation.getChild();

        validate("from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3");
    }

    @Test
    public void testOrderBy() {
        Person person = query.from(Person.class);
        query.orderBy().desc(person.getName()).
                         asc(person.getAge());

        validate("from Person hobj1 order by hobj1.name desc, hobj1.age");
    }

    @Test
    public void testGroupBy() {
        Person person = query.from(Person.class);
        PersonDto personDto = query.select(PersonDto.class); // proxy instance of dto class
        personDto.setPersonAge(person.getAge());
        query.groupBy(person.getAge());

        validate("select hobj1.age as personAge from Person hobj1 group by hobj1.age");
    }

    /**
     * Call 'from' multiple times to create a query
     * with multiple from clause elements.
     * <p>
     * Can be used when manual 'join' is required
     * because no property path is available.
     */
    @Test
    public void testMultipleFrom() {
        Person person = query.from(Person.class);
        Town town = query.from(Town.class);

        query.where(person.getTown().getId()).eq(town.getId());

        validate("from Person hobj1, Town hobj2 where hobj1.town.id = hobj2.id");
    }

    /**
     *
     */
    @Test
    public void testFromWithCustomHqlAlias() {
        Person person = query.from(Person.class);
        Town town = person.getTown();

        query.setHqlAlias(person, "p");
        query.setHqlAlias(town, "t");

        validate("from Person p join p.town t");
    }

    /**
     *
     */
    @Test
    public void testSelectSingleValue() {
        Person person = query.from(Person.class);
        query.select(person);

        validate("select hobj1 from Person hobj1");
    }

    /**
     *
     */
    @Test
    public void testSelectObjectArrayList() {
        Person person = query.from(Person.class);
        query.select(person.getId());
        query.select(person.getName());

        validate("select hobj1.id, hobj1.name from Person hobj1");
    }


    /**
     *
     */
    @Test
    public void testSelectHqlFunctionValues() {
        Person person = query.from(Person.class);

        TypeSafeValueFunctions fun = query.hqlFunction();
        PersonDto dto = query.select(PersonDto.class);
        dto.setPersonAge(fun.avg(person.getAge()).select());
    }

    @Test
    public void testSubselectMergeValues() {
        Person person = query.from(Person.class);
        PersonDto dto = query.select(PersonDto.class);
        dto.setId(person.getId());

        SelectPair<Integer, String> subset = query.selectMergeValues(
                dto, new SelectionMerger2<PersonDto, Integer, String>() {
            public void mergeValuesIntoResult(PersonDto partialResult,
                    Integer personAge, String personName) {
                // do custom result decoration with fetched values
            }
        });
        subset.setFirst(person.getAge());
        subset.setSecond(person.getName());

        validate("select hobj1.id as id, hobj1.age as g1__first, hobj1.name as g1__second from Person hobj1");
    }

    /**
     * Select towns with all of their inhabitants starting their name with a G.
     */
    @Test
    public void testCollectionSubselect() {
        Town town = query.from(Town.class);
        Person inhabitant = query.join(town.getInhabitants());

        // only people with a name starting with 'G'
        query.where(inhabitant.getName()).startsWith("G");

        // select a townDto, and provide the identity field
        TownDto townDto = query.select(TownDto.class,
                new IdentityFieldProvider<TownDto>() {
            @Override
            protected Object getIdentifier(TownDto resultProxy) {
                return resultProxy.getId();
            }
        });
        // set some townDto fields
        bindTownDto(townDto, town);

        // create a personDto to select inhabitants into
        // the town dto's inhabitants
        PersonDto personDto = query.select(
                townDto.getInhabitants(),
                PersonDto.class, null);
        // set some personDto fields
        bindPersonDto(personDto, inhabitant);

        validate("select hobj1.id as id, "
                + "hobj2.id as g1__id, "
                + "hobj2.age as g1__personAge, "
                + "hobj2.name as g1__thePersonsName "
                + "from Town hobj1 "
                + "join hobj1.inhabitants hobj2 "
                + "where hobj2.name like :np1", "G%");
    }

    @Test
    public void selectNestedComponentTypeValues() {
        Product product = query.from(Product.class);
        query.select(product.getProperties().getPlanning().getAlgorithm());

        validate("select hobj1.properties.planning.algorithm from Product hobj1");
    }

    @Test
    public void selectNestedSelectionPropertyDto() {
        Product product = query.from(Product.class);

        Product productDto = query.select(Product.class);
        productDto.setName(product.getName());
        productDto.getProperties().getPlanning().setAlgorithm(product.getName());

        validate("select hobj1.name as name, hobj1.name as properties_planning_algorithm from Product hobj1");
    }

    @Test
    public void testQueryAgainWithAlteredRestriction() {
        Person person = query.from(Person.class);

        OnGoingTextRestriction nameRes = query.where(person.getName());
        nameRes.contains("a");
        validate("from Person hobj1 where hobj1.name like :np1", "%a%");

        nameRes.eq("Eve");
        validate("from Person hobj1 where hobj1.name = :np1", "Eve");
    }

    @Test
    public void testRestrictionPredicate() {
        PersonSelector selector = new PersonSelector();
        selector.setMinimumAge(18);
        selector.setMaximumAge(null);
        selector.setNames(Collections.<String>emptySet());

        Person person = query.from(Person.class);
        query.setDefaultRestrictionPredicate(IGNORE_NULL_OR_EMPTY);
        query.where(person.getAge()).
            gt(selector.getMinimumAge()).
            lt(selector.getMaximumAge());
        query.where(person.getName()).in(selector.getNames());

        validate("from Person hobj1 where hobj1.age > :np1", 18);
    }

    @Test
    public void testDefaultJoinWithNonIdentifier() {
        Person person = query.from(Person.class);
        query.where(person.getTown().getName()).startsWith("New ");

        validate("from Person hobj1 join hobj1.town hobj2 where hobj2.name like :np1", "New %");
    }

    @Test
    public void testDefaultJoinWithIdentifier() {
        Person person = query.from(Person.class);
        query.where(person.getTown().getId()).eq(1L);

        validate("from Person hobj1 where hobj1.town.id = :np1", 1L);
    }

    @Test
    public void testExplicitNoneJoinType() {
        Person person = query.from(Person.class);
        Town town = query.join(person.getTown(), JoinType.None);
        query.where(town.getName()).startsWith("New ");

        validate("from Person hobj1 where hobj1.town.name like :np1", "New %");
    }

    private void bindPersonDto(PersonDto personDto, Person person) {
        personDto.setId(person.getId());
        personDto.setPersonAge(person.getAge());
        personDto.setThePersonsName(person.getName());
    }

    private void bindTownDto(TownDto townDto, Town town) {
        townDto.setId(town.getId());
    }
}
