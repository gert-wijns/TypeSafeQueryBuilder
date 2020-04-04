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

/**
 * Enum containing all allowed operators in the default implemented restriction.
 */
public enum RestrictionOperator {
    EQUAL("="),
    IN("in"),
    NOT_IN("not in"),
    NOT_EQUAL("<>"),
    LIKE("like"),
    NOT_LIKE("not like"),
    IS_NULL("is null"),
    IS_NOT_NULL("is not null"),
    EXISTS("exists"),
    NOT_EXISTS("not exists"),
    LESS_THAN_EQUAL("<="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    GREATER_THAN_EQUAL(">=");

    private final String operator;

    RestrictionOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
