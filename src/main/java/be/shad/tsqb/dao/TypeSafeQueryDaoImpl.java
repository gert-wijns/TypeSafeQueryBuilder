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
package be.shad.tsqb.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import be.shad.tsqb.CollectionNamedParameter;
import be.shad.tsqb.NamedParameter;
import be.shad.tsqb.dao.result.QueryResult;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.helper.TypeSafeQueryHelperImpl;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.proxy.TypeSafeQuerySelectionProxy;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeRootQueryImpl;
import be.shad.tsqb.selection.ResultEntry;
import be.shad.tsqb.selection.ResultEntryKeySelector;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;

public class TypeSafeQueryDaoImpl implements TypeSafeQueryDao {
    private final SessionFactory sessionFactory;
    private final TypeSafeQueryHelper typeSafeQueryHelper;

    public TypeSafeQueryDaoImpl(SessionFactory sessionFactory,
            TypeSafeQueryHelper typeSafeQueryHelper) {
        this.typeSafeQueryHelper = typeSafeQueryHelper;
        this.sessionFactory = sessionFactory;
    }

    public TypeSafeQueryDaoImpl(SessionFactory sessionFactory) {
        this(sessionFactory, new TypeSafeQueryHelperImpl(sessionFactory));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeRootQuery createQuery() {
        return new TypeSafeRootQueryImpl(typeSafeQueryHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> QueryResult<T> doQuery(TypeSafeRootQuery query) {
        return doQuery(query, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T doQueryFirstResult(TypeSafeRootQuery query) {
        return doQueryFirstResult(query, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> doQueryResults(TypeSafeRootQuery query) {
        return doQueryResults(query, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K, V> Map<K, List<V>> doGroupByQueryResults(TypeSafeRootQuery query, K key) {
        return doGroupByQueryResults(query, key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <K, V> Map<K, List<V>> doGroupByQueryResults(TypeSafeRootQuery query, K key, HibernateQueryConfigurer configurer) {
        if (!(key instanceof TypeSafeQuerySelectionProxy)) {
            query.selectMapKey(ResultEntryKeySelector.class).setValue(key);
        }
        HqlQueryBuilderParams params = new HqlQueryBuilderParamsImpl();
        params.setBuildingMapKeyGroupQuery(true);
        List<ResultEntry> results = this.<ResultEntry>doQuery(
                query, configurer, params).getResults();
        Map<Object, List<Object>> map = new HashMap<>();
        for(ResultEntry result: results) {
            List<Object> values = map.get(result.getKey());
            if (values == null) {
                values = new ArrayList<>();
                map.put(result.getKey(), values);
            }
            values.add(result.getValue());
        }
        return (Map) map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K, V> Map<K, V> doMapByQueryResults(TypeSafeRootQuery query, K key) {
        return doMapByQueryResults(query, key, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> doMapByQueryResults(TypeSafeRootQuery query, K key, HibernateQueryConfigurer configurer) {
        if (!(key instanceof TypeSafeQuerySelectionProxy)) {
            query.selectMapKey(ResultEntryKeySelector.class).setValue(key);
        }
        HqlQueryBuilderParams params = new HqlQueryBuilderParamsImpl();
        params.setBuildingMapKeyGroupQuery(true);
        List<ResultEntry> results = this.<ResultEntry>doQuery(
                query, configurer, params).getResults();
        Map<Object, Object> map = new HashMap<>();
        for(ResultEntry result: results) {
            map.put(result.getKey(), result.getValue());
        }
        return (Map<K, V>) map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> QueryResult<T> doQuery(TypeSafeRootQuery tsqbQuery, HibernateQueryConfigurer configurer) {
        return doQuery(tsqbQuery, configurer, new HqlQueryBuilderParamsImpl());
    }
    
    /**
     * {@inheritDoc}
     */
    private <T> QueryResult<T> doQuery(TypeSafeRootQuery tsqbQuery, 
            HibernateQueryConfigurer configurer, HqlQueryBuilderParams params) {
        HqlQuery hqlQuery = tsqbQuery.toHqlQuery(params);

        Session currentSession = sessionFactory.getCurrentSession();
        Query<Object[]> query = currentSession.createQuery(hqlQuery.getHql(), Object[].class);
        int position = 0;
        CollectionNamedParameter chunkedParam = null;
        for(Object param: hqlQuery.getParams()) {
            if (param instanceof NamedParameter) {
                NamedParameter named = (NamedParameter) param;
                if (isChunkedParam(named)) {
                    if (chunkedParam != null) {
                        throw new IllegalStateException(String.format(
                                "More than one batched param [%s, %s] was used in query [%s].",
                                chunkedParam.getName(), named.getName(), query.getQueryString()));
                    }
                    // remember batched param to bind iterate and bind chunks later:
                    chunkedParam = (CollectionNamedParameter) named;
                } else {
                    typeSafeQueryHelper.bindNamedParameter(query, named);
                }
            } else {
                query.setParameter(position++, param);
            }
        }
        if (tsqbQuery.getFirstResult() >= 0) {
            query.setFirstResult(tsqbQuery.getFirstResult());
        }
        if (tsqbQuery.getMaxResults() > 0) {
            query.setMaxResults(tsqbQuery.getMaxResults());
        }

        List<T> results = null;
        if (configurer != null) {
            configurer.beforeQuery(currentSession);
            configurer.configureQuery(query);
            try {
                results = listAll(query, hqlQuery, chunkedParam);
            } finally {
                configurer.afterQuery(currentSession);
            }
        } else {
            results = listAll(query, hqlQuery, chunkedParam);
        }
        return new QueryResult<>(results);
    }

    /**
     * Check if the parameter specifies splitting by batchsize.
     * Check if the amount of params exceeds the batch size,
     * otherwise no splitting is required anyway.
     */
    private boolean isChunkedParam(NamedParameter named) {
        if (!(named instanceof CollectionNamedParameter)) {
            return false;
        }
        CollectionNamedParameter cp = (CollectionNamedParameter) named;
        return cp.hasBatchSize() && cp.getBatchSize() < cp.getValue().size();
    }

    /**
     * Lists the same query with an updated collection in the named param for the batched named param.
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> listAll(Query<Object[]> query, HqlQuery hqlQuery, CollectionNamedParameter chunkedParam) {
        List<Object[]> results;
        if (chunkedParam == null) {
        	results = query.getResultList();
        } else {
        	results = new LinkedList<>();
	        int p = chunkedParam.getBatchSize();
            List<Object> values = new ArrayList<>(p);
            Iterator<?> it = chunkedParam.getValue().iterator();
            while (it.hasNext()) {
                values.add(it.next());
                if (values.size() == p || !it.hasNext()) {
                    query.setParameterList(chunkedParam.getName(), values);
                    results.addAll(query.getResultList());
                    values.clear();
                }
            }
        }

        if (hqlQuery.getResultTransformer() != null) {
            return (List<T>) hqlQuery.getResultTransformer().transformList(results);
        } else {
            return (List<T>) results;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T doQueryFirstResult(TypeSafeRootQuery query, HibernateQueryConfigurer configurer) {
        query.setMaxResults(1);
        QueryResult<T> queryResult = doQuery(query, configurer);
        return queryResult.getFirstResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> doQueryResults(TypeSafeRootQuery query, HibernateQueryConfigurer configurer) {
        QueryResult<T> queryResult = doQuery(query, configurer);
        return queryResult.getResults();
    }
}
