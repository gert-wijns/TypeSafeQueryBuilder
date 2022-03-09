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
package be.shad.tsqb.test.update;

import org.junit.Test;

import be.shad.tsqb.domain.EmbeddedId;
import be.shad.tsqb.domain.ObjectWithEmbeddedId;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.query.TypeSafeUpdateQuery;
import be.shad.tsqb.test.TestDataCreator;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.CaseTypeSafeValue;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.TypeSafeValue;

public class UpdateQueryTests extends TypeSafeQueryTest {

    @Test
    public void testUpdateAllTownNames() {
        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        Town townPx = query.from(Town.class);
        townPx.setName(query.hqlFunction().concat(townPx.getName()).append("Good").select());
        validateUpdate(query, "update Town set name = concat(name, :np1)", "Good");
    }

    @Test
    public void testUpdateSomeTownNames() {
        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        Town townPx = query.from(Town.class);
        townPx.setName(query.hqlFunction().concat(townPx.getName()).append("Good").select());
        query.where(townPx.getName()).notLike("%Good");
        validateUpdate(query, "update Town set name = concat(name, :np1) where name not like :np2", "Good", "%Good");
    }

    @Test
    public void testUpdateAllTownNameWithSQ() {
        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        Town townPx = query.from(Town.class);
        TypeSafeSubQuery<String> subquery = query.subquery(String.class);
        Person personPx = subquery.from(Person.class);
        subquery.where(personPx.getTown().getId()).eq(townPx.getId());
        subquery.select(subquery.hqlFunction().max(personPx.getName()));

        townPx.setName(subquery.select());
        validateUpdate(query, "update Town set name = (select max(hobj1.name) from Person hobj1 where hobj1.town.id = id)");
    }

    @Test
    public void testUpdateForeignKey() {
        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        Town townPx = query.from(Town.class);
        townPx.setName(query.hqlFunction().concat(townPx.getName()).append("Good").select());
//        GeographicCoordinate coord = new GeographicCoordinate();
//        coord.setLattitude(5);
//        coord.setLongitude(2);
        townPx.getGeographicCoordinate().setLongitude(5);
        validateUpdate(query, "update Town " +
                "set name = concat(name, :np1), geographicCoordinate.longitude = :np2",
                "Good", 5.0);
    }

    @Test
    public void testUpdateEmbeddedId() {
        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        ObjectWithEmbeddedId objPx = query.from(ObjectWithEmbeddedId.class);
        EmbeddedId id = new EmbeddedId(1L);
        query.where(objPx.getChild().getId().getId()).eq(50L);

        objPx.setChild(query.asReference(ObjectWithEmbeddedId.class, id).select());
        validateUpdate(query, "update ObjectWithEmbeddedId " +
                "set child.id = :np1 where child.id.id = :np2",
                id, 50L);
    }

    @Test
    public void testUpdateEmbeddedIdNull() {
        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        ObjectWithEmbeddedId objPx = query.from(ObjectWithEmbeddedId.class);
        query.where(objPx.getChild().getId().getId()).eq(50L);

        objPx.setChild(query.nullValue());
        validateUpdate(query, "update ObjectWithEmbeddedId " +
                        "set child.id = NULL where child.id.id = :np1",
                 50L);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValidateIdClass() {
        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        ObjectWithEmbeddedId objPx = query.from(ObjectWithEmbeddedId.class);
        objPx.setChild(query.asReference(ObjectWithEmbeddedId.class, 5L).select());
    }

    @Test
    public void testAssingReferencedObject() {
        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        Person personPx = query.from(Person.class);
        personPx.setTown(query.asReference(Town.class, 1L).select());
        validateUpdate(query, "update Person set town.id = :np1", 1L);
    }

    @Test
    public void testAssingHibernateProxy() {
        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        Person personPx = query.from(Person.class);
        personPx.setTown(getSessionFactory().getCurrentSession().load(Town.class, 1L));
        validateUpdate(query, "update Person set town.id = :np1", 1L);
    }

    @Test
    public void testAssingActualObject() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();

        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        Person personPx = query.from(Person.class);
        personPx.setTown(getSessionFactory().getCurrentSession().get(Town.class, town.getId()));
        validateUpdate(query, "update Person set town.id = :np1", town.getId());
    }

    @Test
    public void testAssingId() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        creator.createTestTown();

        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        Person personPx = query.from(Person.class);
        personPx.getTown().setId(1L);
        validateUpdate(query, "update Person set town.id = :np1", 1L);
    }

    @Test
    public void testAssingCase() {
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();

        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        Person personPx = query.from(Person.class);

        Town loadedTown = getSessionFactory().getCurrentSession().load(Town.class, 10L);
        Town sessionTown = getSessionFactory().getCurrentSession().get(Town.class, town.getId());
        TypeSafeValue<Town> refTown = query.asReference(Town.class, 200L);

        CaseTypeSafeValue<Town> townValue = query.caseWhenValue(Town.class);
        townValue.is(loadedTown).when(personPx.getAge()).gt(50d);
        townValue.is(sessionTown).when(personPx.getAge()).gt(30d);
        townValue.is(refTown).otherwise();

        personPx.setTown(townValue.select());
        validateUpdate(query, "update Person set town.id = (" +
                "case when (age > 50.0) then 10 " +
                "when (age > 30.0) then " + town.getId() + " " +
                "else 200 end)");
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnAttemptedAutomaticJoin() {
        TypeSafeUpdateQuery query = typeSafeQueryDao.createUpdateQuery();
        Person personPx = query.from(Person.class);

        // normally Town.getName() would generate an automatic join to Town
        // but this is not allowed during an update query.
        personPx.getTown().getName();
    }

    private int validateUpdate(TypeSafeUpdateQuery updateQuery, String expectedHql, Object... expectedParams) {
        HqlQueryValue hqlUpdateQuery = updateQuery.toHqlQueryValue(new HqlQueryBuilderParamsImpl());
        validate(hqlUpdateQuery, HqlQueryValueImpl.hql(expectedHql, expectedParams));
        return typeSafeQueryDao.doUpdateQuery(updateQuery);
    }
}
