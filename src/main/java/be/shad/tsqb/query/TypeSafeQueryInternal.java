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
package be.shad.tsqb.query;

import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.data.TypeSafeQueryProxyDataTree;
import be.shad.tsqb.grouping.TypeSafeQueryGroupBys;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.ordering.TypeSafeQueryOrderBys;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.values.TypeSafeValue;

public interface TypeSafeQueryInternal extends TypeSafeQuery {

    /**
     * @return the root query, may be the same instance if this query is the root query.
     */
    TypeSafeRootQueryInternal getRootQuery();
    
    /**
     * @return the parent in case this is a subquery, returns self otherwise.
     */
    TypeSafeQueryInternal getParentQuery();
    
    /**
     * Clears the queue and returns all pending invocations.
     */
    List<TypeSafeQueryProxyData> dequeueInvocations();
    
    /**
     * Same as dequeueInvocations, but immediately validates
     * that there are no or only one pending invocations.
     */
    TypeSafeQueryProxyData dequeueInvocation();

    /**
     * Calls dequeueInvocation().
     * If there was exactly one, then this invocations data is used as referenced value.
     * If there was no invocation, the value is used as a direct value.
     * 
     * @throws IllegalStateException if more than one invocation was on the queue.
     */
    <VAL> TypeSafeValue<VAL> toValue(VAL val);

    /**
     * Enqueues an invocation. The queue tracks all invocations made on the entity proxies 
     * created for this query or one of its subqueries.
     */
    void invocationWasMade(TypeSafeQueryProxyData data);
    
    /**
     * Generates a new entity alias.
     */
    String createEntityAlias();
    
    /**
     * Generates a new named param name.
     */
    String createNamedParam();
    
    /**
     * Checks if the data is available in the query or one of its parents.
     * And before <code>join<code> in case <code>join</code> is not null.
     */
    boolean isInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join);
    
    /**
     * Validates if the data is available in the scope of a query (+join)
     */
    void validateInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join);
    
    /**
     * Validates if the type safe value and any of its nested values is in scope
     * of the query + join if the join is not null. 
     */
    void validateInScope(TypeSafeValue<?> value, TypeSafeQueryProxyData join);
    
    /**
     * @return the known restrictions for this query.
     */
    RestrictionsGroup getRestrictions();
    
    /**
     * @return the known order bys for this query.
     */
    TypeSafeQueryOrderBys getOrderBys();
    
    /**
     * @return the known group bys for this query.
     */
    TypeSafeQueryGroupBys getGroupBys();
    
    /**
     * Data tree, contains all proxy data related to this query.
     * The joins are constructed using this tree.
     */
    TypeSafeQueryProxyDataTree getDataTree();

    /**
     * Convenience method to provide the helper where the internal query was provided.
     */
    TypeSafeQueryHelper getHelper();
    
}
