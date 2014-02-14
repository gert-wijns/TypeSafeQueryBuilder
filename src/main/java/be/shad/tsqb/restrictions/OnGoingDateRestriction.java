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

/**
 * Restrictions for dates. Date specific restrictions are added here.
 * 
 * @see OnGoingRestriction
 */
public class OnGoingDateRestriction extends OnGoingRestriction<Date> {
    private final static String LESS_THAN_EQUAL = "<=";
    private final static String LESS_THAN = "<";
    private final static String GREATER_THAN = ">";
    private final static String GREATER_THAN_EQUAL = ">=";

    public OnGoingDateRestriction(RestrictionImpl restriction, Date argument) {
        super(restriction, argument);
    }

    public OnGoingDateRestriction(RestrictionImpl restriction, TypeSafeValue<Date> argument) {
        super(restriction, argument);
    }

    /**
     * Generates: left < (referencedValue or actualValue)
     */
    public Restriction before(Date value) {
        return before(toValue(value));
    }

    /**
     * Generates: left < dateRepresentative
     */
    public Restriction before(TypeSafeValue<Date> value) {
        restriction.setOperator(LESS_THAN);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left > (referencedValue or actualValue)
     */
    public Restriction after(Date value) {
        return after(toValue(value));
    }

    /**
     * Generates: left > dateRepresentative
     */
    public Restriction after(TypeSafeValue<Date> value) {
        restriction.setOperator(GREATER_THAN);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left <= (referencedValue or actualValue)
     */
    public Restriction notAfter(Date value) {
        return notAfter(toValue(value));
    }

    /**
     * Generates: left <= dateRepresentative
     */
    public Restriction notAfter(TypeSafeValue<Date> value) {
        restriction.setOperator(LESS_THAN_EQUAL);
        restriction.setRight(value);
        return restriction;
    }

    /**
     * Generates: left >= (referencedValue or actualValue)
     */
    public Restriction notBefore(Date value) {
        return notBefore(toValue(value));
    }

    /**
     * Generates: left >= dateRepresentative
     */
    public Restriction notBefore(TypeSafeValue<Date> value) {
        restriction.setOperator(GREATER_THAN_EQUAL);
        restriction.setRight(value);
        return restriction;
    }

}
