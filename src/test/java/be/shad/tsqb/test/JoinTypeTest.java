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

import org.junit.Test;

import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.query.TypeSafeQueryJoin;

public class JoinTypeTest extends TypeSafeQueryTest {

    @Test
    public void testJoinTypeNoneWhenOnlyIdentifierUsed() {
        Person parent = query.from(Person.class);
        Relation relation = query.join(parent.getChildRelations());
        
        query.select(relation.getChild().getId());
        
        validate("select hobj2.child.id from Person hobj1 join hobj1.childRelations hobj2");
    }
    
    /**
     * Test if jointype (unless set explicitly) is defaulted so when a parent
     * join is "Left", the child join is also left.
     */
    @Test
    public void testJoinTypeLeftWhenParentEffectiveJoinTypeIsLeft() {
        Person parent = query.from(Person.class);
        Relation relation = query.join(parent.getChildRelations(), JoinType.Left);
        
        query.select(relation.getChild().getAge());
        
        validate("select hobj3.age from Person hobj1 left join hobj1.childRelations hobj2 left join hobj2.child hobj3");
    }

    /**
     * Test if jointype (unless set explicitly) is defaulted so when a parent
     * join is "Left", the child join is also left.
     */
    @Test
    public void testJoinTypeLeftWhenParentEffectiveJoinTypeIsLeftEvenWhenOnlyIdSelected() {
        Person parent = query.from(Person.class);
        Relation relation = query.join(parent.getChildRelations(), JoinType.Left);
        
        query.select(relation.getChild().getId());
        
        validate("select hobj3.id from Person hobj1 left join hobj1.childRelations hobj2 left join hobj2.child hobj3");
    }
    
    @Test
    public void testFetchOuterEntityFetchesParentEntityByDefault() {
        Person parent = query.from(Person.class);
        query.join(parent.getTown().getProperties(), JoinType.Fetch);

        validate("from Person hobj1 join fetch hobj1.town hobj2 join fetch hobj2.properties hobj3");
    }

    @Test
    public void testLeftFetchOuterEntityFetchesParentEntityByDefault() {
        Person parent = query.from(Person.class);
        query.join(parent.getTown().getProperties(), JoinType.LeftFetch);

        validate("from Person hobj1 join fetch hobj1.town hobj2 left join fetch hobj2.properties hobj3");
    }

    @Test
    public void testLeftFetchOuterEntityFetchesParentEntityByDefaultMore() {
        Person person = query.from(Person.class);
        query.join(person.getSpouse().getSpouse().getTown(), JoinType.LeftFetch);

        validate("from Person hobj1 join fetch hobj1.spouse hobj2 join fetch hobj2.spouse hobj3 left join fetch hobj3.town hobj4");
    }

    @Test
    public void testMultiJoinAppliedToEachProxy() {
        Person parent = query.from(Person.class);
        query.join(JoinType.LeftFetch).join(parent.getTown().getProperties());

        validate("from Person hobj1 left join fetch hobj1.town hobj2 left join fetch hobj2.properties hobj3");
    }

    @Test
    public void testMultiJoinAppliedAndOverridesToEachProxy() {
        Person person = query.from(Person.class);
        Person s1 = person.getSpouse();
        s1.getSpouse(); // adds join

        validate("from Person hobj1 join hobj1.spouse hobj2 join hobj2.spouse hobj3");
        
        // override jointype of second spouse and set jointype for next spouses
        query.join(JoinType.LeftFetch).join(s1.getSpouse().getSpouse().getSpouse());
        
        // original spouse is fetched because next spouses specify fetching
        validate("from Person hobj1 "
                + "join fetch hobj1.spouse hobj2 "
                + "left join fetch hobj2.spouse hobj3 "
                + "left join fetch hobj3.spouse hobj4 "
                + "left join fetch hobj4.spouse hobj5");
    }

    @Test(expected=IllegalStateException.class)
    public void testExceptionWhenJoinAfterDanglingMultiJoin() {
        Person person = query.from(Person.class);
        query.join(JoinType.LeftFetch);
        query.join(person.getSpouse());
    }
    
    @Test(expected=IllegalStateException.class)
    public void testExceptionWhenReusingJoinJoinType() {
        Person person = query.from(Person.class);
        TypeSafeQueryJoin join = query.join(JoinType.LeftFetch);
        Person person2 = join.join(person.getSpouse().getSpouse());
        join.join(person2.getSpouse());
    }

    @Test(expected=IllegalStateException.class)
    public void testExceptionWhenRestrictionAfterDanglingMultiJoin() {
        Person person = query.from(Person.class);
        query.join(JoinType.LeftFetch);

        // spouse.getId doesn't return an entity, this means any
        // pending multi joins cannot be relevant anymore.
        query.where(person.getSpouse().getId()).eq(1L);
    }
    
    @Test(expected=IllegalStateException.class)
    public void testExceptionWhenRestrictionAfterDanglingMultiJoinForUserType() {
        Town town = query.from(Town.class);
        query.join(JoinType.LeftFetch);
        
        // getGeographicCoordinate doesn't return an entity, this means any
        // pending multi joins cannot be relevant anymore.
        query.where(town.getGeographicCoordinate().getLattitude()).eq(1d);
    }

    @Test(expected=IllegalStateException.class)
    public void testExceptionWhenJoinTypeAfterDanglingJoinTypeJoin() {
        query.from(Town.class);
        query.join(JoinType.LeftFetch);
        query.join(JoinType.Fetch);
    }
}
