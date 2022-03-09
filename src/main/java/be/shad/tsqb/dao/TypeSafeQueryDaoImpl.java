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

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.hibernate.Session;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.query.Query;

import be.shad.tsqb.CollectionNamedParameter;
import be.shad.tsqb.NamedParameter;
import be.shad.tsqb.dao.result.QueryResult;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.helper.TypeSafeQueryHelperImpl;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeBaseQuery;
import be.shad.tsqb.query.TypeSafeDeleteQuery;
import be.shad.tsqb.query.TypeSafeDeleteQueryImpl;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeRootQueryImpl;
import be.shad.tsqb.query.TypeSafeUpdateQuery;
import be.shad.tsqb.query.TypeSafeUpdateQueryImpl;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;
import be.shad.tsqb.values.HqlQueryValue;

public class TypeSafeQueryDaoImpl implements TypeSafeQueryDao {
    private final Supplier<Session> sessionSup;
    private final TypeSafeQueryHelper typeSafeQueryHelper;

    public TypeSafeQueryDaoImpl(Supplier<Session> sessionSup,
            TypeSafeQueryHelper typeSafeQueryHelper) {
        this.typeSafeQueryHelper = typeSafeQueryHelper;
        this.sessionSup = sessionSup;
    }

    public TypeSafeQueryDaoImpl(Supplier<Session> sessionSup, MetamodelImplementor metaModel) {
        this(sessionSup, new TypeSafeQueryHelperImpl(sessionSup, metaModel));
    }

    @Override
    public TypeSafeDeleteQuery createDeleteQuery() {
        return new TypeSafeDeleteQueryImpl(typeSafeQueryHelper);
    }

    @Override
    public int doDeleteQuery(TypeSafeDeleteQuery query) {
        return doDeleteQuery(query, null);
    }

    @Override
    public int doDeleteQuery(TypeSafeDeleteQuery query, HibernateQueryConfigurer configurer) {
        return (Integer) doQuery(query, configurer, new HqlQueryBuilderParamsImpl()).getFirstResult();
    }

    @Override
    public TypeSafeUpdateQuery createUpdateQuery() {
        return new TypeSafeUpdateQueryImpl(typeSafeQueryHelper);
    }

    @Override
    public int doUpdateQuery(TypeSafeUpdateQuery query) {
        return doUpdateQuery(query, null);
    }

    @Override
    public int doUpdateQuery(TypeSafeUpdateQuery query, HibernateQueryConfigurer configurer) {
        return (Integer) doQuery(query, configurer, new HqlQueryBuilderParamsImpl()).getFirstResult();
    }

    @Override
    public TypeSafeRootQuery createQuery() {
        return new TypeSafeRootQueryImpl(typeSafeQueryHelper);
    }

    @Override
    public long doCount(TypeSafeRootQuery query) {
        HqlQueryBuilderParamsImpl params = new HqlQueryBuilderParamsImpl();
        params.setSelectingCount(true);
        QueryResult<Long> result = doQuery(query, null, params);
        return result.getFirstResult();
    }

    @Override
    public <T> QueryResult<T> doQuery(TypeSafeRootQuery query) {
        return doQuery(query, null);
    }

    @Override
    public <T> T doQueryFirstResult(TypeSafeRootQuery query) {
        return doQueryFirstResult(query, null);
    }

    @Override
    public <T> List<T> doQueryResults(TypeSafeRootQuery query) {
        return doQueryResults(query, null);
    }

    @Override
    public <T> QueryResult<T> doQuery(TypeSafeRootQuery tsqbQuery, HibernateQueryConfigurer configurer) {
        return doQuery(tsqbQuery, configurer, new HqlQueryBuilderParamsImpl());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> QueryResult<T> doQuery(TypeSafeBaseQuery tsqbQuery,
                                       HibernateQueryConfigurer configurer,
                                       HqlQueryBuilderParams params) {
        HqlQueryValue hqlQuery = tsqbQuery.toHqlQueryValue(params);

        Session currentSession = sessionSup.get();
        Query<Object[]> query = currentSession.createQuery(hqlQuery.getHql());
        CollectionNamedParameter chunkedParam = applyParams(query, hqlQuery.getParams());
        if (hqlQuery instanceof HqlQuery && !params.isSelectingCount()) {
            TypeSafeRootQuery rootQuery = (TypeSafeRootQuery) tsqbQuery;
            if (rootQuery.getFirstResult() >= 0) {
                query.setFirstResult(rootQuery.getFirstResult());
            }
            if (rootQuery.getMaxResults() > 0) {
                query.setMaxResults(rootQuery.getMaxResults());
            }
        }

        List<T> results;
        if (configurer != null) {
            configurer.beforeQuery(currentSession);
            configurer.configureQuery(query);
            try {
                if (hqlQuery instanceof HqlQuery) {
                    results = listAll(query, (HqlQuery) hqlQuery, chunkedParam);
                } else {
                    results = (List) Collections.singletonList(executeAll(query, chunkedParam));
                }
            } finally {
                configurer.afterQuery(currentSession);
            }
        } else {
            if (hqlQuery instanceof HqlQuery) {
                results = listAll(query, (HqlQuery) hqlQuery, chunkedParam);
            } else {
                results = (List) Collections.singletonList(executeAll(query, chunkedParam));
            }
        }
        return new QueryResult<>(results);
    }

    private CollectionNamedParameter applyParams(Query<Object[]> query, Collection<Object> params) {
        int position = 0;
        CollectionNamedParameter chunkedParam = null;
        for(Object param: params) {
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
        return chunkedParam;
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
            results = forEachChunk(query, chunkedParam, query::getResultList).stream()
                    .flatMap(Collection::stream)
                    .collect(toList());
        }

        if (hqlQuery.getResultTransformer() != null) {
            return hqlQuery.getResultTransformer().transformList(results);
        } else {
            return (List<T>) results;
        }
    }

    private int executeAll(Query<Object[]> query, CollectionNamedParameter chunkedParam) {
        if (chunkedParam == null) {
            return query.executeUpdate();
        }
        return forEachChunk(query, chunkedParam, query::executeUpdate)
                .stream().reduce(0, Integer::sum);
    }

    private <T> List<T> forEachChunk(Query<Object[]> query, CollectionNamedParameter chunkedParam, Supplier<T> fn) {
        List<T> chunkResults = new LinkedList<>();
        int p = chunkedParam.getBatchSize();
        List<Object> values = new ArrayList<>(p);
        Iterator<?> it = chunkedParam.getValue().iterator();
        while (it.hasNext()) {
            values.add(it.next());
            if (values.size() == p || !it.hasNext()) {
                query.setParameterList(chunkedParam.getName(), values);
                chunkResults.add(fn.get());
                values.clear();
            }
        }
        return chunkResults;
    }

    @Override
    public <T> T doQueryFirstResult(TypeSafeRootQuery query, HibernateQueryConfigurer configurer) {
        query.setMaxResults(1);
        QueryResult<T> queryResult = doQuery(query, configurer);
        return queryResult.getFirstResult();
    }

    @Override
    public <T> List<T> doQueryResults(TypeSafeRootQuery query, HibernateQueryConfigurer configurer) {
        QueryResult<T> queryResult = doQuery(query, configurer);
        return queryResult.getResults();
    }
}
