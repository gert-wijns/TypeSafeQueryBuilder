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

import be.shad.tsqb.restrictions.RestrictionsGroupFactory;
import be.shad.tsqb.restrictions.WhereRestrictions;
import be.shad.tsqb.values.CaseTypeSafeValue;
import be.shad.tsqb.values.CustomTypeSafeValue;
import be.shad.tsqb.values.HqlQueryValueBuilder;
import be.shad.tsqb.values.TypeSafeValue;
import be.shad.tsqb.values.TypeSafeValueFunctions;
import be.shad.tsqb.values.arithmetic.ArithmeticTypeSafeValueFactory;

public interface TypeSafeBaseQuery extends WhereRestrictions, HqlQueryValueBuilder {

    /**
     * Delegates to {@link #from(Class, String)} with name = null.
     */
    <T> T from(Class<T> fromClass);

    /**
     * Creates a proxy for the given fromClass.
     * <p>
     * Multiple calls are not allowed for update/delete queries.
     * <p>
     * Multiple calls are allowed to create from clauses with multiple entities for selection queries.
     * This may be useful when the queries have no direct relation in hibernate,
     * but the relation can be expressed in the restrictions afterwards.
     *
     * @param name when name is not null, the created proxy will be named using the given name.
     *        (Remark: this is not an hql alias! It is a tag by which the proxy can be retrieved from the query)
     */
    <T> T from(Class<T> fromClass, String name);

    /**
     * Creates a subquery which will select a value of the <code>resultClass</code>.
     */
    <T> TypeSafeSubQuery<T> subquery(Class<T> resultClass);

    /**
     * Build a value using a function. Use this method to create TypeSafeValue objects fluently.
     */
    TypeSafeValueFunctions hqlFunction();

    /**
     * @return instance which can be used to build an arithmetic value
     */
    ArithmeticTypeSafeValueFactory getArithmeticsBuilder();

    /**
     * @return instance which can be used to build restriction groups
     */
    RestrictionsGroupFactory getGroupedRestrictionsBuilder();

    /**
     * Provide methods related to named objects.
     */
    TypeSafeNameds named();

    /**
     * @return a custom value, the hql will be injected into the query where the value is used.
     */
    <VAL> CustomTypeSafeValue<VAL> customValue(Class<VAL> valueClass, String hql, Object... params);

    /**
     * @return a case value which must be built using the is(result).when(conditions).
     */
    <VAL> CaseTypeSafeValue<VAL> caseWhenValue(Class<VAL> valueClass);

    /**
     * Dequeues pending invocations:
     * <ul>
     * <li>If a pending invocation exists, returns a value representing this invocation.</li>
     * <li>If no pending invocation exists, returns a direct value.</li>
     * <li>IllegalStateException when more than one pending invocation.</li>
     * </ul>
     * In general, pending invocations are added when methods of proxied entities or typesare called,
     * except when the method returns another proxy.
     * <p>
     * Using the query restrictions clause building will automatically use the toValue method behind the scenes.
     * The use of this method in custom code is probably a rare thing and is not really encouraged,
     * but there may be cases when this can be useful.
     * <p>
     * An example when this can be used externally is when the value is when grouping by a custom hibernate Type.
     *
     * @throws IllegalStateException when more than one invocation is pending
     */
    <VAL> TypeSafeValue<VAL> toValue(VAL val);

    /**
     * @return a formatted string representation of the resulting hql
     */
    String toFormattedString();

}
