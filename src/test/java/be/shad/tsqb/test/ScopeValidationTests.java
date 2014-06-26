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

import java.util.Date;

import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.domain.House;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.exceptions.ValueNotInScopeException;
import be.shad.tsqb.query.TypeSafeSubQuery;

public class ScopeValidationTests extends TypeSafeQueryTest {

    @Test(expected=ValueNotInScopeException.class)
    public void testSubqueryProxyNotUsedInSelect() {
        TypeSafeSubQuery<Date> subquery = query.subquery(Date.class);
        Building building = subquery.from(Building.class);
        query.select(building.getConstructionDate());
    }

    @Test(expected=ValueNotInScopeException.class)
    public void testSubqueryProxyNotUsedInSibling() {
        TypeSafeSubQuery<Date> subquery1 = query.subquery(Date.class);
        TypeSafeSubQuery<Date> subquery2 = query.subquery(Date.class);
        Building building2 = subquery2.from(Building.class);
        subquery1.select(building2.getConstructionDate());
    }

    @Test(expected=ValueNotInScopeException.class)
    public void testSubqueryProxyNotUsedInOuterRestrictions() {
        House house = query.from(House.class);
        
        TypeSafeSubQuery<Date> subquery = query.subquery(Date.class);
        Building building = subquery.from(Building.class);
        
        // not proper use:, should select max in the subquery and use after(subquery):
        query.where(house.getConstructionDate()).after(query.hqlFunction().max(building.getConstructionDate()));
    }
    
    @Test(expected=ValueNotInScopeException.class)
    public void testSubSubqueryNotSelectableInRoot() {
        query.from(House.class);
        TypeSafeSubQuery<Date> subquery = query.subquery(Date.class);
        TypeSafeSubQuery<Date> subsubquery = subquery.subquery(Date.class);
        query.select(subsubquery.select());
    }

    @Test(expected=ValueNotInScopeException.class)
    public void testSubSubqueryNotSelectableInSelf() {
        query.from(House.class);
        TypeSafeSubQuery<Date> subquery = query.subquery(Date.class);
        TypeSafeSubQuery<Date> subsubquery = subquery.subquery(Date.class);
        subsubquery.select(subsubquery.select());
    }

    @Test(expected=ValueNotInScopeException.class)
    public void testSubSubqueryNotSelectableInChild() {
        query.from(House.class);
        TypeSafeSubQuery<Date> subquery = query.subquery(Date.class);
        TypeSafeSubQuery<Date> subsubquery = subquery.subquery(Date.class);
        subsubquery.select(subquery.select());
    }

    @Test
    public void testSubSubquerySelectableInParent() {
        query.from(Town.class);
        TypeSafeSubQuery<Date> subquery = query.subquery(Date.class);
        Building building = subquery.from(Building.class);
        subquery.select(subquery.hqlFunction().max(building.getConstructionDate()));
        query.select(subquery);
        
        validate("select (select max(hobj2.constructionDate) from Building hobj2) from Town hobj1");
    }

    /**
     * 
     */
    @Test(expected=ValueNotInScopeException.class)
    public void testWithLaterJoinedNotInScope() {
        Person parent = query.from(Person.class);
        Relation relation = query.join(parent.getChildRelations());
        Person child = query.join(relation.getChild());
        Relation relation2 = query.join(child.getChildRelations());
        Person grandChild = query.join(relation2.getChild());
        
        query.joinWith(child).where(child.getName()).eq(grandChild.getName());
    }

    @Test(expected=ValueNotInScopeException.class)
    public void testWithJoinedDifferentFromNotInScope() {
        Person parent = query.from(Person.class);
        Person child = query.from(Person.class);
        Relation relation = query.join(child.getChildRelations());
        
        query.joinWith(relation).where(parent.getId()).eq(relation.getId());
        validate("");
    }
    
    @Test
    public void testWithFilterByParam() {
        Person parent = query.from(Person.class);
        Relation relation = query.join(parent.getChildRelations());
        Person child = query.join(relation.getChild());
        query.joinWith(child).where(child.getName()).eq("Josh");
        validate(" from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3 with hobj3.name = :np1", "Josh");
    }

    @Test(expected=RuntimeException.class)
    public void testWithFilterByReferenceNotAllowed() {
        Person parent = query.from(Person.class);
        Relation relation = query.join(parent.getChildRelations());
        Person child = query.join(relation.getChild());
        query.joinWith(child).where(child.getName()).eq(parent.getName());
    }
    
}
