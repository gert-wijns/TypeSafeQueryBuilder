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

import be.shad.tsqb.NamedParameter;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.values.HqlQueryValueImpl;

public class CustomHqlRestrictionTest extends TypeSafeQueryTest {
    private static final String PARENT_CUSTOM_ALIAS = "parentAlias";

    /**
     * 
     */
    @Test
    public void testInjectCustomHqlRestriction() {
        Person person = query.from(Person.class);
        query.setHqlAlias(person, PARENT_CUSTOM_ALIAS);
        query.where().and(new HqlQueryValueImpl("parentAlias.id = :parentAliasId", new NamedParameter("parentAliasId", 1L)));
        validate(" from Person parentAlias where parentAlias.id = :parentAliasId", 1L);
    }

    /**
     * 
     */
    @Test
    public void testInjectMultipleCustomHqlRestriction() {
        Person person = query.from(Person.class);
        query.setHqlAlias(person, PARENT_CUSTOM_ALIAS);
        query.where().and(new HqlQueryValueImpl("parentAlias.id = :parentAliasId", new NamedParameter("parentAliasId", 1L)));
        query.where().and(new HqlQueryValueImpl("parentAlias.name like :parentAliasName", new NamedParameter("parentAliasName", "Josh%")));
        validate(" from Person parentAlias where parentAlias.id = :parentAliasId "
                + "and parentAlias.name like :parentAliasName", 1L, "Josh%");
    }

}
