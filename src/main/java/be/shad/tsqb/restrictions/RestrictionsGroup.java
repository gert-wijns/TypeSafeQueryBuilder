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

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.values.HqlQueryValueBuilder;


/**
 * Groups the Restriction and WhereRestrictions to be able to add a group
 * as a nested restriction group and to provide the where() methods to start
 * chaining.
 */
public interface RestrictionsGroup extends RestrictionsGroupBuilder, RestrictionChainable, HqlQueryValueBuilder {
    
    public enum RestrictionsGroupBracketsPolicy {
        Always,
        Never,
        WhenMoreThanOne;
    }
    
    /**
     * @return the query to which this restrictions group belongs.
     */
    TypeSafeQuery getQuery();

    /**
     * The RestrictionsGroup doesn't implement the Restriction itself
     * because this would add the RestrictionChainable methods to this interface.
     * <p>
     * To be able to separately build a group, and then add it to the
     * query, use this method to get this group as a Restriction.
     * <p>
     * Usually, the restriction group is built as a chainable and the
     * final return value will already be a restriction.
     */
    Restriction getRestrictions();

    /**
     * Change when this group should add brackets.
     * For the first group of the where clause, the value is set to Never.
     * The default value is WhenMoreThanOne.
     */
    void setBracketsPolicy(RestrictionsGroupBracketsPolicy bracketsPolicy);

}
