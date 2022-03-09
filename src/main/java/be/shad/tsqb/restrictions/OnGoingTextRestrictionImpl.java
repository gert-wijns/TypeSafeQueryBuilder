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
package be.shad.tsqb.restrictions;

import static be.shad.tsqb.restrictions.RestrictionOperator.LIKE;
import static be.shad.tsqb.restrictions.RestrictionOperator.NOT_LIKE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.restrictions.named.SingleNamedParameterBinder;
import be.shad.tsqb.restrictions.predicate.RestrictionPredicate;
import be.shad.tsqb.values.DirectTypeSafeStringValue;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for text. Text specific restrictions are added here.
 *
 * @see OnGoingRestrictionImpl
 */
public class OnGoingTextRestrictionImpl
        extends OnGoingRestrictionImpl<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction>
        implements OnGoingTextRestriction, ContinuedOnGoingTextRestriction {

    private static final String EMPTY = "";
    private static final String WILDCARD = "%";

    public OnGoingTextRestrictionImpl(RestrictionsGroupInternal group,
            RestrictionNodeType restrictionNodeType, String argument) {
        super(group, restrictionNodeType, argument);
    }

    public OnGoingTextRestrictionImpl(RestrictionsGroupInternal group,
            RestrictionNodeType restrictionNodeType, TypeSafeValue<String> argument) {
        super(group, restrictionNodeType, argument);
    }

    @Override
    protected OnGoingTextRestrictionImpl createContinuedOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<String> startValue) {
        return new OnGoingTextRestrictionImpl(group, restrictionNodeType, startValue);
    }

    @Override
    protected OnGoingTextRestriction createOriginalOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<String> startValue) {
        return createContinuedOnGoingRestriction(restrictionNodeType, startValue);
    }

    @Override
    protected Class<String> getSupportedValueClass() {
        return String.class;
    }

    /**
     * @return null if values is null and a set containing the values otherwise
     */
    private Collection<String> toCollection(String[] values) {
        if (values == null) {
            return null;
        }
        Set<String> valuesSet = new HashSet<>(values.length);
        Collections.addAll(valuesSet, values);
        return valuesSet;
    }

    @Override
    public ContinuedOnGoingTextRestriction notIn(String[] values) {
        return notIn(toCollection(values));
    }

    @Override
    public ContinuedOnGoingTextRestriction notIn(String[] values, RestrictionPredicate predicate) {
        return notIn(toCollection(values), predicate);
    }

    @Override
    public ContinuedOnGoingTextRestriction in(String[] values) {
        return in(toCollection(values));
    }

    @Override
    public ContinuedOnGoingTextRestriction in(String[] values, RestrictionPredicate predicate) {
        return in(toCollection(values), predicate);
    }

    @Override
    public ContinuedOnGoingTextRestriction like(TypeSafeValue<String> value) {
        return addRestrictionAndContinue(startValue, LIKE, value);
    }

    @Override
    public ContinuedOnGoingTextRestriction like(String value) {
        return like(toValue(value, null));
    }

    @Override
    public ContinuedOnGoingTextRestriction notLike(TypeSafeValue<String> value) {
        return addRestrictionAndContinue(startValue, NOT_LIKE, value);
    }

    @Override
    public ContinuedOnGoingTextRestriction notLike(String value) {
        return notLike(toValue(value, null));
    }

    @Override
    public ContinuedOnGoingTextRestriction contains(String value) {
        return contains(value, null);
    }

    @Override
    public ContinuedOnGoingTextRestriction startsWith(String value) {
        return startsWith(value, null);
    }

    @Override
    public ContinuedOnGoingTextRestriction endsWith(String value) {
        return endsWith(value, null);
    }

    @Override
    public SingleNamedParameterBinder<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> contains() {
        DirectTypeSafeValue<String> value = createDirectValue(WILDCARD, WILDCARD);
        return createNamedParameterBinder(value, like(value));
    }

    @Override
    public SingleNamedParameterBinder<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> endsWith() {
        DirectTypeSafeValue<String> value = createDirectValue(WILDCARD, EMPTY);
        return createNamedParameterBinder(value, like(value));
    }

    @Override
    public SingleNamedParameterBinder<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> startsWith() {
        DirectTypeSafeValue<String> value = createDirectValue(EMPTY, WILDCARD);
        return createNamedParameterBinder(value, like(value));
    }

    /**
     * Adds wildcards to the value in case it is a direct value.
     * Validates no wildcards are used otherwise.
     */
    private TypeSafeValue<String> toValue(String prefix, String value, String postfix, RestrictionPredicate predicate) {
        if (value != null) {
            DirectTypeSafeStringValue toValue = (DirectTypeSafeStringValue) toValue(value, predicate);
            return setWildCards(prefix, toValue, postfix);
        }
        if (!prefix.isEmpty() || !postfix.isEmpty()) {
            List<TypeSafeQueryProxyData> dequeued = group.getQuery().dequeueInvocations();
            if (!dequeued.isEmpty()) {
                throw new UnsupportedOperationException("Like not supported for "
                        + "referenced value [" + dequeued.get(0) + "].");
            }
        }
        return toValue(null, predicate);
    }

    @Override
    protected DirectTypeSafeStringValue createDummyDirectValue() {
        return new DirectTypeSafeStringValue(group.getQuery());
    }

    /**
     * Creates an empty value and sets the prefix and postfix.
     */
    private DirectTypeSafeValue<String> createDirectValue(String prefix, String postfix) {
        DirectTypeSafeStringValue directValue = createDummyDirectValue();
        return setWildCards(prefix, directValue, postfix);
    }

    /**
     * Sets the prefix and postfix of the string parameter.
     */
    private DirectTypeSafeValue<String> setWildCards(String prefix,
            DirectTypeSafeStringValue parameter, String postfix) {
        parameter.setPrefix(prefix);
        parameter.setPostfix(postfix);
        return parameter;
    }

    @Override
    public ContinuedOnGoingTextRestriction endsWith(String value, RestrictionPredicate predicate) {
        return like(toValue(WILDCARD, value, EMPTY, predicate));
    }

    @Override
    public ContinuedOnGoingTextRestriction startsWith(String value, RestrictionPredicate predicate) {
        return like(toValue(EMPTY, value, WILDCARD, predicate));
    }

    @Override
    public ContinuedOnGoingTextRestriction contains(String value, RestrictionPredicate predicate) {
        return like(toValue(WILDCARD, value, WILDCARD, predicate));
    }

    @Override
    public ContinuedOnGoingTextRestriction notLike(String value, RestrictionPredicate predicate) {
        return notLike(toValue(value, predicate));
    }

    @Override
    public ContinuedOnGoingTextRestriction like(String value, RestrictionPredicate predicate) {
        return like(toValue(value, predicate));
    }

    @Override
    public ContinuedOnGoingTextRestriction lt(String value) {
        return lt(toValue(value, null));
    }

    @Override
    public ContinuedOnGoingTextRestriction lt(TypeSafeValue<String> value) {
        return addRestrictionAndContinue(startValue, RestrictionOperator.LESS_THAN, value);
    }

    @Override
    public ContinuedOnGoingTextRestriction gt(String value) {
        return gt(toValue(value, null));
    }

    @Override
    public ContinuedOnGoingTextRestriction gt(TypeSafeValue<String> value) {
        return addRestrictionAndContinue(startValue, RestrictionOperator.GREATER_THAN, value);
    }

    @Override
    public ContinuedOnGoingTextRestriction lte(String value) {
        return lte(toValue(value, null));
    }

    @Override
    public ContinuedOnGoingTextRestriction lte(TypeSafeValue<String> value) {
        return addRestrictionAndContinue(startValue, RestrictionOperator.LESS_THAN_EQUAL, value);
    }

    @Override
    public ContinuedOnGoingTextRestriction gte(String value) {
        return gte(toValue(value, null));
    }

    @Override
    public ContinuedOnGoingTextRestriction gte(TypeSafeValue<String> value) {
        return addRestrictionAndContinue(startValue, RestrictionOperator.GREATER_THAN_EQUAL, value);
    }

}
