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

public class FromsCopyTest extends TypeSafeQueryCopyTest {

    /**
     * Test from on the original is copied and extending 
     * the query doesn't extend the original query.
     */
    @Test
    public void testCopyFrom() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        
        Person copiedPersonProxy = validateAndCopy(PERSON_OBJ,
                " from Person hobj1");
        
        // add something to the copy query and validate the change
        copy.where(copiedPersonProxy.getName()).eq("Test");
        validateChangedCopy(" from Person hobj1 where hobj1.name = :np1", "Test");
    }

    
}
