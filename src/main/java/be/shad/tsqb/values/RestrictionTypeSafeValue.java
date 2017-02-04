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
package be.shad.tsqb.values;

import be.shad.tsqb.exceptions.SelectException;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.restrictions.Restriction;

/**
 * Turns a restriction into a type safe value (boolean) so that it can be
 * queued as selection into the query.
 */
public class RestrictionTypeSafeValue extends TypeSafeValueImpl<Boolean> {

    private Restriction restriction;

    public RestrictionTypeSafeValue(CopyContext context, RestrictionTypeSafeValue original) {
        super(context, original);
        this.restriction = context.get(original.restriction);
    }

    public RestrictionTypeSafeValue(TypeSafeQuery query, Restriction restriction) {
        super(query, Boolean.class);
        this.restriction = restriction;
        if (((TypeSafeQueryInternal) query).containsRestriction(restriction)) {
            throw new SelectException("Preventing to accidently use query.where(...) as a restriction to select a boolean value."
                    + "The result would be the restriction is added both to the where clause AND the select clause."
                    + "Build restrictions to 'select' using query.getGroupedRestrictionBuilder().where(...) "
                    + "instead (those restrictions aren't automatically attached to the query).");
        }
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        boolean requiresLiterals = params.setRequiresLiterals(true);
        HqlQueryValue hqlQueryValue = restriction.toHqlQueryValue(params);
        params.setRequiresLiterals(requiresLiterals);
        // wrap in a case when so the person who builds the query doesn't have to!
        return HqlQueryValueImpl.hql("case when(" + hqlQueryValue.getHql() +
                ") then true else false end", hqlQueryValue.getParams());
    }

    /**
     */
    @Override
    public Copyable copy(CopyContext context) {
        return new RestrictionTypeSafeValue(context, this);
    }

}
