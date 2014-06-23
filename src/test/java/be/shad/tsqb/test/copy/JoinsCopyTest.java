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
package be.shad.tsqb.test.copy;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.query.JoinType;

public class JoinsCopyTest extends TypeSafeQueryCopyTest {

    /**
     * Test joins are copied and the joins on the original 
     * are not influenced by the joins on the copy.
     */
    @Test
    public void testCopyJoin() {
        Person personProxy = query.from(Person.class);
        Relation relationProxy = query.join(personProxy.getChildRelations(), JoinType.Left);
        Person childProxy = query.join(relationProxy.getChild(), JoinType.Left);
        query.named().name(childProxy, "child");
        
        Person childProxyCopy = validateAndCopy("child", 
                " from Person hobj1 "
                + "left join hobj1.childRelations hobj2 "
                + "left join hobj2.child hobj3");
        
        copy.join(childProxyCopy.getProperties(), JoinType.Left);
        validateChangedCopy(
                " from Person hobj1 "
                + "left join hobj1.childRelations hobj2 "
                + "left join hobj2.child hobj3 "
                + "left join hobj3.properties hobj4");
        
        query.join(personProxy.getTown(), JoinType.Inner);
        validateChangedOriginal(
                " from Person hobj1 "
                + "left join hobj1.childRelations hobj2 "
                + "left join hobj2.child hobj3 "
                + "join hobj1.town hobj4");
    }
    
}
