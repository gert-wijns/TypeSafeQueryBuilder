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

import org.junit.Before;
import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.query.JoinType;

public class CustomAliasesTest extends TypeSafeQueryTest {
    private static final String PARENT_CUSTOM_ALIAS = "parentAlias";
    private static final String CHILD_CUSTOM_ALIAS = "childAlias";

    @Before
    public void setupQuery() {
        Person person = query.from(Person.class);
        Relation children = query.join(person.getChildRelations());
        query.setHqlAlias(person, PARENT_CUSTOM_ALIAS);
        query.setHqlAlias(children.getChild(), CHILD_CUSTOM_ALIAS);
    }

    /**
     * The child is registered using a custom alias, retrieve it from the query use it to complete the query
     */
    @Test
    public void testUseRegisteredAlias() {
        Person child = query.getByHqlAlias(CHILD_CUSTOM_ALIAS);
        query.where(child.getName()).startsWith("Josh");
        validate(" from Person parentAlias join parentAlias.childRelations hobj2 join hobj2.child childAlias where childAlias.name like :np1", "Josh%");
    }

    /**
     * Also registering the grandchildren with the same custom alias must throw an exception
     */
    @Test(expected=IllegalArgumentException.class)
    public void testRegisteringAliasAgainForDifferentPathThrowsException() {
        Person parent = query.getByHqlAlias(CHILD_CUSTOM_ALIAS);
        Relation children = query.join(parent.getChildRelations());
        query.setHqlAlias(children.getChild(), CHILD_CUSTOM_ALIAS);
    }

    /**
     * Also registering the grandchildren with the same custom alias must throw an exception
     */
    @Test
    public void testRegisteringAliasAgainForSamePathIsAllowed() {
        Person person = query.getByHqlAlias(PARENT_CUSTOM_ALIAS);
        Relation children = query.join(person.getChildRelations());
        query.setHqlAlias(person, PARENT_CUSTOM_ALIAS);
        query.setHqlAlias(children.getChild(), CHILD_CUSTOM_ALIAS);

        validate(" from Person parentAlias join parentAlias.childRelations hobj2 join hobj2.child childAlias");
    }

    /**
     * When explicitly creating an additional join for the same entity path,
     * the custom alias may also not be set to the same value.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testPathIsDifferentWhenExplicitExtraJoiningSameEntity() {
        Person person = query.getByHqlAlias(PARENT_CUSTOM_ALIAS);
        Relation children = query.join(person.getChildRelations());

        Person otherChild = query.join(children.getChild(), JoinType.Inner, true);
        query.setHqlAlias(otherChild, CHILD_CUSTOM_ALIAS);
    }

    /**
     * Passing null into the register method fails,
     * unless {@link #testRegisteringWithNullDoesntFailIfInvocationWasQueued()}.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testRegisteringWithNullFails() {
        query.setHqlAlias(null, "unusedAlias");
    }

    /**
     * parent.getChildRelations() returns null, but the invocation is used to know
     * that the user really meant the childrelations
     */
    @Test
    public void testRegisteringWithNullDoesntFailIfInvocationWasQueued() {
        Person parent = query.getByHqlAlias(CHILD_CUSTOM_ALIAS);
        query.setHqlAlias(parent.getChildRelations(), "someOtherChildRelations");

        validate(" from Person parentAlias join parentAlias.childRelations hobj2 join hobj2.child childAlias join childAlias.childRelations someOtherChildRelations");
    }

    /**
     * Passing something other than a proxy is not allowed.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testRegisteringWithNonProxyFails() {
        query.setHqlAlias(1D, "UnusedAlias");
    }

}
