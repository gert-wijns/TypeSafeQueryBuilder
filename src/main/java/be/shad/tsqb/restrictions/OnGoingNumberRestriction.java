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
 * Restrictions for numbers. Number specific restrictions are added here.
 * 
 * @see OnGoingRestriction
 */
public class OnGoingNumberRestriction extends OnGoingRestriction<Number> {
    private final static String LESS_THAN_EQUAL = "<=";
    private final static String LESS_THAN = "<";
    private final static String GREATER_THAN = ">";
    private final static String GREATER_THAN_EQUAL = ">=";

    public OnGoingNumberRestriction(RestrictionImpl restriction, Number argument) {
        super(restriction, argument);
    }
    
    @SuppressWarnings("unchecked")
    public OnGoingNumberRestriction(RestrictionImpl restriction, TypeSafeValue<? extends Number> argument) {
        super(restriction, (TypeSafeValue<Number>) argument);
    }

    /**
     * Generates: left < (referencedValue or actualValue)
     */
    public Restriction lt(Number value) {
        return lt(toValue(value));
    }

    /**
     * Generates: left < numberRepresentative
     */
    public Restriction lt(TypeSafeValue<Number> value) {
        restriction.setOperator(LESS_THAN);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left > (referencedValue or actualValue)
     */
    public Restriction gt(Number value) {
        return gt(toValue(value));
    }

    /**
     * Generates: left > numberRepresentative
     */
    public Restriction gt(TypeSafeValue<Number> value) {
        restriction.setOperator(GREATER_THAN);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    public Restriction lte(Number value) {
        return lte(toValue(value));
    }

    /**
     * Generates: left <= numberRepresentative
     */
    public Restriction lte(TypeSafeValue<Number> value) {
        restriction.setOperator(LESS_THAN_EQUAL);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left >= (referencedValue or actualValue)
     */
    public Restriction gte(Number value) {
        return gte(toValue(value));
    }

    /**
     * Generates: left >= numberRepresentative
     */
    public Restriction gte(TypeSafeValue<Number> value) {
        restriction.setOperator(GREATER_THAN_EQUAL);
        restriction.setRight(value);
        return restriction;
    }

}
