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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import be.shad.tsqb.helper.TypeSafeQueryHelperImpl;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.param.QueryParameter;
import be.shad.tsqb.query.TypeSafeRootQuery;

public class TypeSafeQueryTest {
    
    protected final Logger logger = LogManager.getLogger(getClass());
    @Rule public TestName name = new TestName();
    
    private SessionFactory sessionFactory;
    private TypeSafeQueryHelperImpl helper;
    protected TypeSafeRootQuery query;
    protected List<?> doQueryResult;

    /**
     * Initialize the sessionFactory and helper.
     * The helper has an override to generate shorter entity names 
     * for readability (and it also works in hibernate...)
     */
    @Before
    public void initialize() {
        Configuration config = new Configuration();
        config.configure("be/shad/tsqb/tests/hibernate.cfg.xml");
        sessionFactory = config.buildSessionFactory();
        helper = new TypeSafeQueryHelperImpl(sessionFactory) {
            // trim package for readability:
            @Override
            public String getEntityName(Class<?> entityClass) {
                String entityName = super.getEntityName(entityClass);
                return entityName.substring(entityName.lastIndexOf(".")+1);
            }
        };
        query = helper.createQuery();
        sessionFactory.getCurrentSession().beginTransaction();
    }
    
    @After
    public void teardown() {
        sessionFactory.getCurrentSession().getTransaction().rollback();
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected TypeSafeRootQuery createQuery() {
        return helper.createQuery();
    }
    
    /**
     * Creates a query using a session calls list, hibernate should 
     * not complain if the query is syntactically correct.
     * <p>
     * The hql query is returned.
     */
    protected HqlQuery doQuery(TypeSafeRootQuery typeSafeQuery) {
        HqlQuery hqlQuery = typeSafeQuery.toHqlQuery();
        
        // Create a hibernate query object using the generated hql+params:
        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery.getHql());
        for(QueryParameter param: hqlQuery.getParams()) {
            if (param.getValue() instanceof Collection) {
                query.setParameterList(param.getName(), (Collection<?>) param.getValue());
            } else if(param.getValue() instanceof Object[]) {
                query.setParameterList(param.getName(), (Object[]) param.getValue());
            } else {
                query.setParameter(param.getName(), param.getValue());
            }
        }
        
        logger.debug(String.format("%s:\n%s\n--- params: %s\n", name.getMethodName(), 
                hqlQuery.getHql(), Arrays.toString(hqlQuery.getParams())));
        
        // call the list, this is the moment of truth:
        query.setResultTransformer(hqlQuery.getResultTransformer());
        doQueryResult = query.list();
        
        // return for additional checks:
        return hqlQuery;
    }
    
    protected void validate(String hql, Object... params) {
        HqlQuery hqlQuery = doQuery(query);

        String expected = String.format("\nExpected:\n%s\n--- params: %s\n", hql, Arrays.toString(params));
        String result = String.format("\nResult:\n%s\n--- params: %s\n", hqlQuery.getHql(), Arrays.toString(hqlQuery.getParams()));
        
        assertTrue(expected + result, hqlQuery.getHql().equals(hql));
        if( params == null || params.length == 0 ){
            assertTrue(expected + result, hqlQuery.getParams().length == 0);
        } else {
            List<Object> actualParams = new LinkedList<>();
            List<QueryParameter> queryParams = new ArrayList<>();
            for(QueryParameter queryParam: hqlQuery.getParams()) {
                queryParams.add(queryParam);
            }
            Collections.sort(queryParams, new Comparator<QueryParameter>() {
                @Override
                public int compare(QueryParameter o1, QueryParameter o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            for(QueryParameter queryParam: queryParams) {
                actualParams.add(queryParam.getValue());
            }
            assertTrue(expected + result, actualParams.equals(Arrays.asList(params)));
        }
    }

}
