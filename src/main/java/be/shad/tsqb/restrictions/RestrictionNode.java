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
 * A node in the chain of restrictions.
 * The type can be null in case it is the first restriction in the chain.
 * <p>
 * This class is most or only for internal use and is only used in the RestrictionsGroup.
 */
public class RestrictionNode {
    private final Restriction restriction;
    private final RestrictionNodeType type;

    public RestrictionNode(Restriction restriction, RestrictionNodeType type) {
        this.restriction = restriction;
        this.type = type;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public RestrictionNodeType getType() {
        return type;
    }

}
