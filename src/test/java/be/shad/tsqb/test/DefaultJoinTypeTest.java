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

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.query.JoinType;

public class DefaultJoinTypeTest extends TypeSafeQueryTest {

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
    
}
