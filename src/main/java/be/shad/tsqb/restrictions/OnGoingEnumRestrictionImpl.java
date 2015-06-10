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

/**
 * Restrictions for enums, enum specific restrictions can be added here.
 *
 * @see OnGoingRestrictionImpl
 */
public class OnGoingEnumRestrictionImpl<E extends Enum<E>>
        extends OnGoingRestrictionImpl<E, ContinuedOnGoingEnumRestriction<E>, OnGoingEnumRestriction<E>>
        implements OnGoingEnumRestriction<E>, ContinuedOnGoingEnumRestriction<E> {

    public OnGoingEnumRestrictionImpl(RestrictionsGroupInternal group,
            RestrictionNodeType restrictionNodeType, E argument) {
        super(group, restrictionNodeType, argument);
    }

    public OnGoingEnumRestrictionImpl(RestrictionsGroupInternal group,
            RestrictionNodeType restrictionNodeType, TypeSafeValue<E> argument) {
        super(group, restrictionNodeType, argument);
    }

    @Override
    protected OnGoingEnumRestrictionImpl<E> createContinuedOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<E> previousValue) {
        return new OnGoingEnumRestrictionImpl<E>(group, restrictionNodeType, previousValue);
    }

    @Override
    protected OnGoingEnumRestriction<E> createOriginalOnGoingRestriction(
            RestrictionNodeType restrictionNodeType, TypeSafeValue<E> previousValue) {
        return createContinuedOnGoingRestriction(restrictionNodeType, previousValue);
    }

    @Override
    protected Class<E> getSupportedValueClass() {
        return startValue.getValueClass();
    }

}
