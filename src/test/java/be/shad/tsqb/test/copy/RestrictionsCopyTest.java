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

import static be.shad.tsqb.restrictions.predicate.RestrictionPredicate.IGNORE_NULL;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;


public class RestrictionsCopyTest extends TypeSafeQueryCopyTest {

    @Test
    public void testCopyRestrictions() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        query.where(personProxy.getName()).eq("Josh");
        
        Person personProxyCopy = validateAndCopy(PERSON_OBJ,
                " from Person hobj1 where hobj1.name = :np1", "Josh");
        
        copy.where(personProxyCopy.getAge()).gt(10);
        validateChangedCopy(" from Person hobj1 where hobj1.name = :np1 and hobj1.age > :np2", "Josh", 10);

        query.where(personProxy.getAge()).gt(12);
        validateChangedOriginal(" from Person hobj1 where hobj1.name = :np1 and hobj1.age > :np2", "Josh", 12);
    }

    @Test
    public void testCopyRestrictionsIgnore() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        query.where(personProxy.getName()).eq(null, IGNORE_NULL);
        
        Person personProxyCopy = validateAndCopy(PERSON_OBJ,
                " from Person hobj1");
        
        copy.where(personProxyCopy.getAge()).gt(10);
        validateChangedCopy(" from Person hobj1 where hobj1.age > :np1", 10);

        query.where(personProxy.getAge()).gt(12);
        validateChangedOriginal(" from Person hobj1 where hobj1.age > :np1", 12);
    }

}
