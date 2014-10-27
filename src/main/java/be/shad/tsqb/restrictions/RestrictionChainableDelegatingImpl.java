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

import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.values.HqlQueryValue;

/**
 * An implementation where all methods are delegates to a group.
 */
public abstract class RestrictionChainableDelegatingImpl extends RestrictionChainableImpl {

    protected final RestrictionsGroupInternal group;
    
    public RestrictionChainableDelegatingImpl(RestrictionsGroupInternal group) {
        this.group = group;
    }

    /**
     * Copy constructor
     */
    protected RestrictionChainableDelegatingImpl(CopyContext context, RestrictionChainableDelegatingImpl original) {
        this.group = context.get(original.group);
    }

    public RestrictionsGroupInternal getRestrictionsGroup() {
        return group;
    }
    
    @Override
    public Restriction getRestriction() {
        return group;
    }

    public RestrictionsGroupInternal getGroup() {
        return group;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable and(HqlQueryValue restriction) {
        return group.and(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable or(HqlQueryValue restriction) {
        return group.or(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable and(Restriction restriction) {
        return group.and(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionAndChainable and(RestrictionsGroup group) {
        return this.group.and(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable or(Restriction restriction) {
        return group.or(restriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionAndChainable or(RestrictionsGroup group) {
        return this.group.or(group);
    }
    
}
