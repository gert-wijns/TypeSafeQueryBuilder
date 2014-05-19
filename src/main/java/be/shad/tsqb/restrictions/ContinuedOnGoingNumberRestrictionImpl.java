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

import be.shad.tsqb.values.TypeSafeValue;

public class ContinuedOnGoingNumberRestrictionImpl 
        extends ContinuedOnGoingRestrictionImpl<Number, ContinuedOnGoingNumberRestriction, OnGoingNumberRestriction> 
        implements ContinuedOnGoingNumberRestriction {

    public ContinuedOnGoingNumberRestrictionImpl(RestrictionsGroupInternal group, TypeSafeValue<Number> previousValue) {
        super(group, previousValue);
    }

    @Override
    protected OnGoingNumberRestriction createOnGoingRestriction(RestrictionImpl restriction, TypeSafeValue<Number> value) {
        return new OnGoingNumberRestrictionImpl(restriction, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction gte(TypeSafeValue<Number> value) {
        return and().gte(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction gte(Number value) {
        return and().gte(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction lte(TypeSafeValue<Number> value) {
        return and().lte(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction lte(Number value) {
        return and().lte(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction gt(TypeSafeValue<Number> value) {
        return and().gt(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction gt(Number value) {
        return and().gt(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction lt(TypeSafeValue<Number> value) {
        return and().lt(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingNumberRestriction lt(Number value) {
        return and().lt(value);
    }
    
}
