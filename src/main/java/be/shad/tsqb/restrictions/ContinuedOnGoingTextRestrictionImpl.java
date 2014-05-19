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

public class ContinuedOnGoingTextRestrictionImpl 
        extends ContinuedOnGoingRestrictionImpl<String, ContinuedOnGoingTextRestriction, OnGoingTextRestriction> 
        implements ContinuedOnGoingTextRestriction {

    public ContinuedOnGoingTextRestrictionImpl(RestrictionsGroupInternal group, TypeSafeValue<String> previousValue) {
        super(group, previousValue);
    }

    @Override
    protected OnGoingTextRestriction createOnGoingRestriction(RestrictionImpl restriction, TypeSafeValue<String> value) {
        return new OnGoingTextRestrictionImpl(restriction, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction endsWith(String value) {
        return and().endsWith(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction startsWith(String value) {
        return and().startsWith(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction contains(String value) {
        return and().contains(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContinuedOnGoingTextRestriction like(TypeSafeValue<String> value) {
        return and().like(value);
    }

}
