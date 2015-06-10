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

import java.util.Collection;
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

    private final static String EMPTY = "";
    private final static String WILDCARD = "%";

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
        for(String value: values) {
            valuesSet.add(value);
        }
        return valuesSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction notIn(String[] values) {
        return notIn(toCollection(values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction notIn(String[] values, RestrictionPredicate predicate) {
        return notIn(toCollection(values), predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction in(String[] values) {
        return in(toCollection(values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction in(String[] values, RestrictionPredicate predicate) {
        return in(toCollection(values), predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction like(TypeSafeValue<String> value) {
        return addRestrictionAndContinue(startValue, LIKE, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction like(String value) {
        return like(toValue(value, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction contains(String value) {
        return contains(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction startsWith(String value) {
        return startsWith(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction endsWith(String value) {
        return endsWith(value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleNamedParameterBinder<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> contains() {
        DirectTypeSafeValue<String> value = createDirectValue(WILDCARD, WILDCARD);
        return createNamedParameterBinder(value, like(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleNamedParameterBinder<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> endsWith() {
        DirectTypeSafeValue<String> value = createDirectValue(WILDCARD, EMPTY);
        return createNamedParameterBinder(value, like(value));
    }

    /**
     * {@inheritDoc}
     */
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
        if (value instanceof String) {
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
        TypeSafeValue<String> toValue = toValue(value, predicate);
        return toValue;
    }

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
    public ContinuedOnGoingTextRestriction like(String value, RestrictionPredicate predicate) {
        return like(toValue(value, predicate));
    }

}
