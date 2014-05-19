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

import java.util.Collection;

import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.TypeSafeValue;


public abstract class ContinuedOnGoingRestrictionImpl<VAL, CONTINUED extends ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL>, 
            ORIGINAL extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>>
        extends RestrictionChainableDelegatingImpl implements Restriction, ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL> {
    private final TypeSafeValue<VAL> previousValue;
    private RestrictionImpl continuedRestriction;

    public ContinuedOnGoingRestrictionImpl(RestrictionsGroupInternal group, 
            TypeSafeValue<VAL> previousValue) {
        super(group);
        this.previousValue = previousValue;
    }
    
    /**
     * Creats the actual instance of the subtype of this continued restriction.
     */
    protected abstract ORIGINAL createOnGoingRestriction(RestrictionImpl restriction, TypeSafeValue<VAL> value);

    /**
     * {@inheritDoc}
     */
    @Override
    public final ORIGINAL and() {
        continuedRestriction = createAnd();
        return createOnGoingRestriction(continuedRestriction, previousValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ORIGINAL or() {
        continuedRestriction = createOr();
        return createOnGoingRestriction(continuedRestriction, previousValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED isNull() {
        return and().isNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED isNotNull() {
        return and().isNotNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED in(TypeSafeValue<T> value) {
        return and().in(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED in(Collection<T> values) {
        return and().in(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED notIn(TypeSafeValue<T> value) {
        return and().notIn(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends VAL> CONTINUED notIn(Collection<T> values) {
        return and().notIn(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED eq(TypeSafeValue<VAL> value) {
        return and().eq(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED eq(VAL value) {
        return and().eq(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED not(TypeSafeValue<VAL> value) {
        return and().not(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CONTINUED not(VAL value) {
        return and().not(value);
    }
    
    /**
     * Add the continued restriction if continued
     */
    @Override
    public HqlQueryValue toHqlQueryValue() {
        if (continuedRestriction != null) {
            return continuedRestriction.toHqlQueryValue();
        } else {
            // return empty value, wasnt continued.
            return new HqlQueryValueImpl();
        }
    }

}
