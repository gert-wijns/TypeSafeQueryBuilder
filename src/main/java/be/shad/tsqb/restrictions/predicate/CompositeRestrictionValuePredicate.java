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
package be.shad.tsqb.restrictions.predicate;

import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.values.TypeSafeValue;

public class CompositeRestrictionValuePredicate implements RestrictionPredicate, Copyable {
    private final RestrictionPredicate[] predicates;

    public CompositeRestrictionValuePredicate(RestrictionPredicate... predicates) {
        this.predicates = predicates;
    }

    /**
     * Copy constructor
     */
    public CompositeRestrictionValuePredicate(CopyContext context,
            CompositeRestrictionValuePredicate original) {
        RestrictionPredicate[] copy = null;
        if (original.predicates != null) {
            copy = new RestrictionPredicate[original.predicates.length];
            for(int i=0, n=original.predicates.length; i < n; i++) {
                copy[i] = context.get(original.predicates[i]);
            }
        }
        this.predicates = copy;
    }

    public static RestrictionPredicate composite(RestrictionPredicate... predicates) {
        return new CompositeRestrictionValuePredicate(predicates);
    }

    @Override
    public boolean isValueApplicable(TypeSafeValue<?> value) {
        for(RestrictionPredicate predicate: predicates) {
            if (!predicate.isValueApplicable(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return null;
    }

}
