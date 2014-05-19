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

import java.util.Date;

import be.shad.tsqb.values.TypeSafeValue;

public class ContinuedOnGoingDateRestrictionImpl 
        extends ContinuedOnGoingRestrictionImpl<Date, ContinuedOnGoingDateRestriction, OnGoingDateRestriction> 
        implements ContinuedOnGoingDateRestriction {

    public ContinuedOnGoingDateRestrictionImpl(RestrictionsGroupInternal group, TypeSafeValue<Date> previousValue) {
        super(group, previousValue);
    }

    @Override
    protected OnGoingDateRestriction createOnGoingRestriction(RestrictionImpl restriction, TypeSafeValue<Date> value) {
        return new OnGoingDateRestrictionImpl(restriction, value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notBefore(TypeSafeValue<Date> value) {
        return and().notBefore(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notBefore(Date value) {
        return and().notBefore(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notAfter(TypeSafeValue<Date> value) {
        return and().notAfter(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction notAfter(Date value) {
        return and().notAfter(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction after(TypeSafeValue<Date> value) {
        return and().after(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction after(Date value) {
        return and().after(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction before(TypeSafeValue<Date> value) {
        return and().before(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingDateRestriction before(Date value) {
        return and().before(value);
    }

}
