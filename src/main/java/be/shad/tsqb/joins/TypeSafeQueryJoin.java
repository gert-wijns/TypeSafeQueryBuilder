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
package be.shad.tsqb.joins;

import java.util.Date;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.restrictions.OnGoingBooleanRestriction;
import be.shad.tsqb.restrictions.OnGoingDateRestriction;
import be.shad.tsqb.restrictions.OnGoingEnumRestriction;
import be.shad.tsqb.restrictions.OnGoingNumberRestriction;
import be.shad.tsqb.restrictions.OnGoingTextRestriction;
import be.shad.tsqb.restrictions.Restriction;
import be.shad.tsqb.restrictions.RestrictionChainable;
import be.shad.tsqb.restrictions.RestrictionsGroupImpl;
import be.shad.tsqb.restrictions.RestrictionsGroupInternal;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Links a proxy data with its restrictions.
 */
public class TypeSafeQueryJoin<T> {
    private final TypeSafeQueryProxyData data;
    private final RestrictionsGroupInternal restrictions;

    public TypeSafeQueryJoin(TypeSafeQueryInternal query, TypeSafeQueryProxyData data) {
        this.restrictions = new RestrictionsGroupImpl(query, data);
        this.data = data;
    }
    
    @SuppressWarnings("unchecked")
    public T getProxy() {
        return (T) data.getProxy();
    }
    
    public TypeSafeQueryProxyData getData() {
        return data;
    }
    
    public Restriction getRestrictions() {
        return restrictions.getRestrictions();
    }
    
    /**
     * Kickoff for the restriction chainable.
     * <p>
     * It is probably preferrable to use one of the other with(...)
     * methods, but the result would be the same as calling with().and(...).
     */
    public RestrictionChainable with() {
        return restrictions;
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingNumberRestriction with(Number value) {
        return with().and(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingTextRestriction with(String value) {
        return with().and(value);
    }
    
    /**
     * Delegate to restrictions.
     */
    public OnGoingBooleanRestriction with(Boolean value) {
        return with().and(value);
    }
    
    /**
     * Delegate to restrictions.
     */
    public OnGoingDateRestriction with(Date value) {
        return with().and(value);
    }
    
    /**
     * Delegate to restrictions.
     */
    public <E extends Enum<E>> OnGoingEnumRestriction<E> with(E value) {
        return with().and(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingNumberRestriction withNumber(TypeSafeValue<Number> value) {
        return with().andNumber(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingTextRestriction withString(TypeSafeValue<String> value) {
        return with().andString(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingDateRestriction withDate(TypeSafeValue<Date> value) {
        return with().andDate(value);
    }

    /**
     * Delegate to restrictions.
     */
    public OnGoingBooleanRestriction withBoolean(TypeSafeValue<Boolean> value) {
        return with().andBoolean(value);
    }

    /**
     * Delegate to restrictions.
     */
    public <E extends Enum<E>> OnGoingEnumRestriction<E> withEnum(TypeSafeValue<E> value) {
        return with().andEnum(value);
    }
    
}
