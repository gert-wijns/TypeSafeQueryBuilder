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

import be.shad.tsqb.values.HqlQueryValueBuilder;

/**
 * Represents a restriction. 
 * <p>
 * All restrictions could be expressed using the {@link RestrictionImpl} so 
 * this interface currently only has one implementation.
 */
public interface Restriction extends RestrictionChainable, HqlQueryValueBuilder {
    
    /**
     * Available to check whether or not to add enclosing brackets.
     * A restriction group is wrapped in brackets if it is not
     * the same as the restriction group to which the restriction is added!
     * <p>
     * You probably don't need to care about this though unless
     * you create your own Restriction implementation.
     */
    RestrictionsGroup getRestrictionsGroup();
    
}
