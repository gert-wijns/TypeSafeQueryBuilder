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

public class OnGoingObjectRestrictionImpl<VAL>
        extends OnGoingRestrictionImpl<VAL, ContinuedOnGoingObjectRestriction<VAL>, OnGoingObjectRestriction<VAL>>
        implements OnGoingObjectRestriction<VAL>, ContinuedOnGoingObjectRestriction<VAL> {

    public OnGoingObjectRestrictionImpl(RestrictionsGroupInternal group, RestrictionNodeType restrictionNodeType, TypeSafeValue<VAL> argument) {
        super(group, restrictionNodeType, argument);
    }

    public OnGoingObjectRestrictionImpl(RestrictionsGroupInternal group, RestrictionNodeType restrictionNodeType, VAL argument) {
        super(group, restrictionNodeType, argument);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Class<VAL> getSupportedValueClass() {
        return startValue != null ? startValue.getValueClass(): (Class) Object.class;
    }

    @Override
    protected ContinuedOnGoingObjectRestriction<VAL> createContinuedOnGoingRestriction(RestrictionNodeType restrictionNodeType, TypeSafeValue<VAL> startValue) {
        return new OnGoingObjectRestrictionImpl<VAL>(group, restrictionNodeType, startValue);
    }

    @Override
    protected OnGoingObjectRestriction<VAL> createOriginalOnGoingRestriction(RestrictionNodeType restrictionNodeType, TypeSafeValue<VAL> startValue) {
        return createContinuedOnGoingRestriction(restrictionNodeType, startValue);
    }
}
