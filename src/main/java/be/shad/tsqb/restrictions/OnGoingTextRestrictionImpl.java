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

import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Restrictions for text. Text specific restrictions are added here.
 * 
 * @see OnGoingRestrictionImpl
 */
public class OnGoingTextRestrictionImpl 
        extends OnGoingRestrictionImpl<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> 
        implements OnGoingTextRestriction {
    
    private final static String WILDCARD = "%";
    private final static String EMPTY = "";
    private final static String LIKE = "like";

    public OnGoingTextRestrictionImpl(RestrictionImpl restriction, String argument) {
        super(restriction, argument);
    }
    
    public OnGoingTextRestrictionImpl(RestrictionImpl restriction, TypeSafeValue<String> argument) {
        super(restriction, argument);
    }
    
    @Override
    protected ContinuedOnGoingTextRestriction createContinuedOnGoingRestriction(
            RestrictionsGroupInternal group, TypeSafeValue<String> previousValue) {
        return new ContinuedOnGoingTextRestrictionImpl(group, previousValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction like(TypeSafeValue<String> value) {
        restriction.setOperator(LIKE);
        restriction.setRight(value);
        return createContinuedOnGoingRestriction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction contains(String value) {
        return like(toValue(WILDCARD, value, WILDCARD));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction startsWith(String value) {
        return like(toValue(EMPTY, value, WILDCARD));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction endsWith(String value) {
        return like(toValue(WILDCARD, value, EMPTY));
    }
    
    /**
     * Adds wildcards to the value in case it is a direct value.
     * Validates no wildcards are used otherwise.
     */
    private TypeSafeValue<String> toValue(String left, String value, String right) {
        if( value instanceof String ) {
            return toValue(left + value + right);
        }
        if( left.length() != 0 || right.length() != 0 ) {
            List<TypeSafeQueryProxyData> dequeued = restriction.getQuery().dequeueInvocations();
            if( !dequeued.isEmpty() ) {
                throw new UnsupportedOperationException("Like not supported for "
                        + "referenced value [" + dequeued.get(0) + "].");
            }
        }
        TypeSafeValue<String> toValue = toValue(value);
        return toValue;
    }

}
