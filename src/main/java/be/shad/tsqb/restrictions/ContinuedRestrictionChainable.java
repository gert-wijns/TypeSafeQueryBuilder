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
 * Allows for continued restrictions to be usable to either continue with another restriction
 * or to have its grouped restrictions added.
 */
public interface ContinuedRestrictionChainable extends RestrictionChainable {

    /**
     * Exposes the restriction group so it can be used when this
     * continued restriction chainable is added to another restriction chainable.
     */
    RestrictionsGroup getRestrictionsGroup();
    
}
