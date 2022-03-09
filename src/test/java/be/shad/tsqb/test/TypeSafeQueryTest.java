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

import static be.shad.tsqb.values.HqlQueryValueImpl.hql;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

import be.shad.tsqb.NamedParameter;
import be.shad.tsqb.dao.HibernateQueryConfigurer;
import be.shad.tsqb.dao.TypeSafeQueryDao;
import be.shad.tsqb.dao.TypeSafeQueryDaoImpl;
import be.shad.tsqb.helper.TypeSafeQueryHelperImpl;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.values.HqlQueryValue;

public abstract class TypeSafeQueryTest {

    protected final Logger logger = LogManager.getLogger(getClass());
    @Rule public final TestName name = new TestName();

    private static SessionFactory sessionFactory;
    protected static TypeSafeQueryDao typeSafeQueryDao;
    private static TypeSafeQueryHelperImpl helper;
    protected TypeSafeRootQuery query;
    protected List<?> doQueryResult;

    /**
     * Initialize the sessionFactory and helper once.
     * The helper has an override to generate shorter entity names
     * for readability (and it also works in hibernate...)
     */
    @BeforeClass
    public static void initializeClass() {
        if (sessionFactory == null) {
            Configuration config = new Configuration();
            config.configure("be/shad/tsqb/tests/hibernate.cfg.xml");
            sessionFactory = config.buildSessionFactory();
            Supplier<Session> sessionSup = () -> sessionFactory.getCurrentSession();
            helper = new TypeSafeQueryHelperImpl(sessionSup, (MetamodelImplementor) sessionFactory.getMetamodel()) {
                // trim package for readability:
                @Override
                public String getEntityName(Class<?> entityClass) {
                    String entityName = super.getEntityName(entityClass);
                    return entityName.substring(entityName.lastIndexOf(".")+1);
                }
            };
            typeSafeQueryDao = new TypeSafeQueryDaoImpl(sessionSup, helper);
        }
    }

    /**
     * Create a new query to test and begin the transaction
     */
    @Before
    public void initialize() {
        query = typeSafeQueryDao.createQuery();
        ((TypeSafeRootQueryInternal) query).getProjections().setIncludeAliases(true);
        sessionFactory.getCurrentSession().beginTransaction();
    }

    /**
     * Rollback anything which was added to the memory db
     */
    @After
    public void teardown() {
        sessionFactory.getCurrentSession().getTransaction().rollback();
    }

    public TypeSafeQueryHelperImpl getHelper() {
        return helper;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected TypeSafeRootQuery createQuery() {
        return typeSafeQueryDao.createQuery();
    }

    /**
     * Creates a query using a session calls list, hibernate should
     * not complain if the query is syntactically correct.
     * <p>
     * The hql query is returned.
     */
    protected HqlQuery doQuery(TypeSafeRootQuery typeSafeQuery, HibernateQueryConfigurer configurer) {
        HqlQuery hqlQuery = typeSafeQuery.toHqlQuery();

        doQueryResult = typeSafeQueryDao.doQueryResults(typeSafeQuery, configurer);
        logger.debug("{}:\n{}\n--- params: {}\n", name.getMethodName(),
                hqlQuery.getHql(), hqlQuery.getParams());

        // return for additional checks:
        return hqlQuery;
    }

    protected HqlQuery doQuery(TypeSafeRootQuery typeSafeQuery) {
        return doQuery(typeSafeQuery, null);
    }

    protected <T> List<T>  validate(HqlQueryValue expected) {
        return validate(expected.getHql(), expected.getParams());
    }

    protected <T> List<T>  validate(String hql, Object... params) {
        return validate(query, hql(hql, params));
    }

    protected <T> List<T>  validate(TypeSafeRootQuery query, HqlQueryValue expected) {
        return validate(doQuery(query), expected);
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> validate(HqlQueryValue actual, HqlQueryValue expected) {
        List<Object> actualParams = new LinkedList<>();
        if (actual.getParams() != null) {
            for(Object queryParam: actual.getParams()) {
                if (queryParam instanceof NamedParameter) {
                    actualParams.add(((NamedParameter) queryParam).getValue());
                } else {
                    actualParams.add(queryParam);
                }
            }
        }

        String expectedHql = String.format("\nExpected:\n%s\n--- params: %s\n", expected.getHql().trim(), expected.getParams());
        String result = String.format("\nResult:\n%s\n--- params: %s\n", actual.getHql().trim(), actual.getParams());

        assertEquals(expectedHql + result, expected.getHql().trim(), actual.getHql().trim());
        assertEquals(expectedHql + result, expected.getParams().size(), actualParams.size());
        Iterator<Object> expectedIt = expected.getParams().iterator();
        Iterator<Object> actualIt = actualParams.iterator();
        while(expectedIt.hasNext() && actualIt.hasNext()) {
            Object expectedParam = expectedIt.next();
            Object actualParam = actualIt.next();
            if (actualParam instanceof HibernateProxy) {
                actualParam = ((HibernateProxy) actualParam).getHibernateLazyInitializer().getIdentifier();
            }
            if (expectedParam instanceof Collection) {
                // don't care if the collection has a different order,
                // as long as the elements are the same:
                assertTrue(expectedHql + result, actualParam instanceof Collection);
                assertEquals(expectedHql + result,
                        new HashSet<>((Collection<?>) expectedParam),
                        new HashSet<>((Collection<?>) actualParam));
            } else {
                assertEquals(expectedHql + result, expectedParam, actualParam);
            }
        }
        return (List<T>) doQueryResult;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getSingleQueryResults() {
        Assert.assertEquals(1, doQueryResult.size());
        return (T) doQueryResult.get(0);
    }
}
