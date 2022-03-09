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

import java.util.List;

import be.shad.tsqb.dao.result.QueryResult;
import be.shad.tsqb.query.TypeSafeDeleteQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeUpdateQuery;

public interface TypeSafeQueryDao {

    /**
     * Creates a fresh query instance. This is the starting point to create a new query.
     */
    TypeSafeRootQuery createQuery();

    /**
     * Creates a new delete query instance.
     */
    TypeSafeDeleteQuery createDeleteQuery();

    /**
     * Delegates to {@link #doDeleteQuery(TypeSafeDeleteQuery, HibernateQueryConfigurer)}
     */
    int doDeleteQuery(TypeSafeDeleteQuery query);

    /**
     * Executes the given delete query.
     * @return the amount of affected rows.
     */
    int doDeleteQuery(TypeSafeDeleteQuery query, HibernateQueryConfigurer configurer);

    /**
     * Creates a new update query instance.
     */
    TypeSafeUpdateQuery createUpdateQuery();

    /**
     * Delegeates to {@link #doUpdateQuery(TypeSafeUpdateQuery, HibernateQueryConfigurer)}
     * @return the amount of affeced rows
     */
    int doUpdateQuery(TypeSafeUpdateQuery query);

    /**
     * Executes the given update query.
     * @return the amount of affected rows.
     */
    int doUpdateQuery(TypeSafeUpdateQuery query, HibernateQueryConfigurer configurer);

    /**
     * Runs the query with replaced selection selecting only count(*) and then returns the first result.
     * This should not be used when using group by.
     * @return the amount of records which would be available without limiting the amount of results.
     */
    long doCount(TypeSafeRootQuery query);

    /**
     * Delegates to {@link #doQuery(TypeSafeRootQuery, HibernateQueryConfigurer)} without configurer.
     */
    <T> QueryResult<T> doQuery(TypeSafeRootQuery query);

    /**
     * Delegates to {@link #doQueryFirstResult(TypeSafeRootQuery, HibernateQueryConfigurer)} without configurer.
     */
    <T> T doQueryFirstResult(TypeSafeRootQuery query);

    /**
     * Delegates to {@link #doQueryResults(TypeSafeRootQuery, HibernateQueryConfigurer)} without configurer.
     */
    <T> List<T> doQueryResults(TypeSafeRootQuery query);

    /**
     * Transforms the query to a HqlQuery, creates a hibernate query object for
     * the current session and sets the start/max results.
     * <p>
     * The values are transformed using the transformer which was created
     * when the query was transformed and are wrapped in the query result.
     */
    <T> QueryResult<T> doQuery(TypeSafeRootQuery query, HibernateQueryConfigurer configurer);

    /**
     * Delegates to {@link #doQuery(TypeSafeRootQuery)} and returns the first result.
     */
    <T> T doQueryFirstResult(TypeSafeRootQuery query, HibernateQueryConfigurer configurer);

    /**
     * Delegates to {@link #doQuery(TypeSafeRootQuery)} and returns the results.
     */
    <T> List<T> doQueryResults(TypeSafeRootQuery query, HibernateQueryConfigurer configurer);
}
