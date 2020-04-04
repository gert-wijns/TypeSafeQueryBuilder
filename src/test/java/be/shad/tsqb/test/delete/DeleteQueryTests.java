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
package be.shad.tsqb.test.delete;

import be.shad.tsqb.domain.EmbeddedId;
import be.shad.tsqb.domain.ObjectWithEmbeddedId;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.query.TypeSafeDeleteQuery;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.TypeSafeValue;

import org.junit.Test;

public class DeleteQueryTests extends TypeSafeQueryTest {

    @Test
    public void testDeleteAll() {
        TypeSafeDeleteQuery deleteQuery = typeSafeQueryDao.createDeleteQuery();
        Town townPx = deleteQuery.from(Town.class);
        validateDelete(deleteQuery, "delete from Town");
    }

    @Test
    public void testDeleteNameLike() {
        TypeSafeDeleteQuery deleteQuery = typeSafeQueryDao.createDeleteQuery();
        Town townPx = deleteQuery.from(Town.class);
        deleteQuery.where(townPx.getName()).startsWith("Bad");

        validateDelete(deleteQuery,
                "delete from Town where name like :np1",
                "Bad%");
    }

    @Test
    public void testDeleteIdInSubQuery() {
        TypeSafeDeleteQuery deleteQuery = typeSafeQueryDao.createDeleteQuery();
        Town townPx = deleteQuery.from(Town.class);

        TypeSafeSubQuery<Long> idsSQ = deleteQuery.subquery(Long.class);
        Town fromSqPx = idsSQ.from(Town.class);
        idsSQ.where(fromSqPx.getName()).startsWith("Bad");
        idsSQ.select(fromSqPx.getId());

        deleteQuery.where(townPx.getId()).in(idsSQ);

        validateDelete(deleteQuery,"delete from Town where id in (" +
                "select hobj1.id from Town hobj1 where hobj1.name like :np1)",
                "Bad%");
    }

    @Test
    public void testDeleteExistsSubQuery() {
        TypeSafeDeleteQuery deleteQuery = typeSafeQueryDao.createDeleteQuery();
        Town townPx = deleteQuery.from(Town.class);

        TypeSafeSubQuery<Long> idsSQ = deleteQuery.subquery(Long.class);
        Town fromSqPx = idsSQ.from(Town.class);
        idsSQ.where(fromSqPx.getId()).lt(townPx.getId());
        idsSQ.select(fromSqPx.getId());

        deleteQuery.whereExists(idsSQ);

        validateDelete(deleteQuery, "delete from Town " +
                "where exists (select hobj1.id from Town hobj1 where hobj1.id < id)");
    }

    @Test
    public void testDeleteByEmbeddedId() {
        TypeSafeDeleteQuery deleteQuery = typeSafeQueryDao.createDeleteQuery();
        ObjectWithEmbeddedId entity = deleteQuery.from(ObjectWithEmbeddedId.class);
        EmbeddedId id = new EmbeddedId(5L);
        deleteQuery.where(query.toValue(entity.getId())).eq(id);

        validateDelete(deleteQuery, "delete from ObjectWithEmbeddedId where id = :np1", id);
    }

    @Test
    public void testDeleteByEmbeddedId2() {
        TypeSafeDeleteQuery deleteQuery = typeSafeQueryDao.createDeleteQuery();
        ObjectWithEmbeddedId entity = deleteQuery.from(ObjectWithEmbeddedId.class);
        deleteQuery.where(entity.getId().getId()).eq(5L);

        validateDelete(deleteQuery, "delete from ObjectWithEmbeddedId where id.id = :np1", 5L);
    }

    @Test
    public void testDeleteByEmbeddedChildId() {
        TypeSafeDeleteQuery deleteQuery = typeSafeQueryDao.createDeleteQuery();
        ObjectWithEmbeddedId entity = deleteQuery.from(ObjectWithEmbeddedId.class);
        EmbeddedId id = new EmbeddedId(5L);
        deleteQuery.where(query.toValue(entity.getChild().getId())).eq(id);

        validateDelete(deleteQuery, "delete from ObjectWithEmbeddedId where child.id = :np1", id);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDeleteByEmbeddedChildIdException() {
        TypeSafeDeleteQuery deleteQuery = typeSafeQueryDao.createDeleteQuery();
        ObjectWithEmbeddedId entity = deleteQuery.from(ObjectWithEmbeddedId.class);
        deleteQuery.where(query.toValue(entity.getChild().getId().getId())).eq(5L);

        validateDelete(deleteQuery, "delete from ObjectWithEmbeddedId where child.id.id = :np1", 5L);
    }

    private int validateDelete(TypeSafeDeleteQuery deleteQuery, String expectedHql, Object... expectedParams) {
        HqlQueryValue hqlDeleteQuery = deleteQuery.toHqlQueryValue(new HqlQueryBuilderParamsImpl());
        validate(hqlDeleteQuery, HqlQueryValueImpl.hql(expectedHql, expectedParams));
        return typeSafeQueryDao.doDeleteQuery(deleteQuery);
    }
}
