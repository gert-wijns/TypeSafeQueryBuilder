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

import org.junit.Test;

import be.shad.tsqb.domain.DomainObject;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.selection.collection.ResultIdentifierProvider;

public class CollectionSubselectTest extends TypeSafeQueryTest {
    private final ResultIdentifierProvider<DomainObject> identifierProvider = 
            new ResultIdentifierProvider<DomainObject>() {
        @Override
        public Object createIdentifier(DomainObject result) {
            return result.getId();
        }
    };

    @Test
    public void test() {
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
        
        validate("select hobj1.id as id, hobj2.id as g1__id from Town hobj1 join hobj1.inhabitants hobj2 where hobj2.name like :np1", "Jo%");

        assertEquals(1, doQueryResult.size());
        if (!(doQueryResult.get(0) instanceof Town)) {
            fail("Expected to find a town as result.");
        }
        Town townResult = (Town) doQueryResult.get(0);
        assertEquals(town.getId(), townResult.getId());
        assertEquals(2, townResult.getInhabitants().size());
    }
    
}
