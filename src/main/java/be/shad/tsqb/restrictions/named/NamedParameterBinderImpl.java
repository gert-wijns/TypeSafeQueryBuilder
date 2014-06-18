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
package be.shad.tsqb.restrictions.named;

import java.util.Collection;

import be.shad.tsqb.param.QueryParameter;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.restrictions.ContinuedOnGoingRestriction;
import be.shad.tsqb.restrictions.OnGoingRestriction;

public class NamedParameterBinderImpl<VAL, CONTINUED extends 
        ContinuedOnGoingRestriction<VAL, CONTINUED, ORIGINAL>, 
        ORIGINAL extends OnGoingRestriction<VAL, CONTINUED, ORIGINAL>> 
    implements SingleNamedParameterBinder<VAL, CONTINUED, ORIGINAL>, 
               CollectionNamedParameterBinder<VAL, CONTINUED, ORIGINAL> {
    
    private final TypeSafeQueryInternal query;
    private final QueryParameter<VAL> parameter;
    private final CONTINUED chainable;
    
    public NamedParameterBinderImpl(TypeSafeQueryInternal query, 
            QueryParameter<VAL> parameter,
            CONTINUED chainable) {
        this.parameter = parameter;
        this.chainable = chainable;
        this.query = query;
    }

    @Override
    public CONTINUED named(String alias) {
        query.bindAlias(parameter, alias);
        return chainable;
    }

    @Override
    public CONTINUED named(String alias, VAL value) {
        parameter.setValue(value);
        return named(alias);
    }

    @Override
    public <T extends VAL> CONTINUED named(String alias, Collection<T> value) {
        parameter.setValue(value);
        return named(alias);
    }
    
}
