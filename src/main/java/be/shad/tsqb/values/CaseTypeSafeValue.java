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

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryScopeValidator;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.restrictions.RestrictionsGroupImpl;
import be.shad.tsqb.restrictions.RestrictionsGroupInternal;

/**
 * Represents a case when() then ... (else ...) end.
 */
public class CaseTypeSafeValue<T> extends TypeSafeValueImpl<T> implements OnGoingCaseWhen<T>, TypeSafeValueContainer {
    private final List<OnGoingCaseImpl<T>> cases = new LinkedList<>();

    /**
     * Copy constructor
     */
    protected CaseTypeSafeValue(CopyContext context, CaseTypeSafeValue<T> original) {
        super(context, original);
        for(OnGoingCaseImpl<T> c: original.cases) {
            cases.add(context.get(c));
        }
    }

    public CaseTypeSafeValue(TypeSafeQuery query, Class<T> valueType) {
        super(query, valueType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingCase<T> is(TypeSafeValue<T> value) {
        OnGoingCaseImpl<T> ongoingCase = new OnGoingCaseImpl<>(
                new RestrictionsGroupImpl(query, null), value);
        cases.add(ongoingCase);
        return ongoingCase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingCase<T> is(T value) {
        // passing a "null" direct value provider because "null" is allowed in a case when.
        // if value == null and there is a select/invocation value those will be taken instead.
        return is(query.toValue(value, (query) -> new CustomTypeSafeValue<>(query, getValueClass(), "null")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingCase<T> isNull() {
        return is((T) null);
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        HqlQueryValueImpl value = new HqlQueryValueImpl();
        if (!cases.isEmpty()) {
            value.appendHql("(");
        }
        boolean previous = params.setRequiresLiterals(true);
        for(int i=0; i < cases.size(); i++) {
            OnGoingCaseImpl<T> ongoingCase = cases.get(i);
            RestrictionsGroupInternal restrictions = ongoingCase.getRestrictionsGroup();
            HqlQueryValue then = ongoingCase.getValue().toHqlQueryValue(params);
            if (restrictions.isEmpty()) {
                value.appendHql(" else ");
            } else {
                if (i == 0 ){
                    value.appendHql("case when (");
                } else {
                    value.appendHql(" when (");
                }
                HqlQueryValue when = restrictions.toHqlQueryValue(params);
                value.appendHql(when.getHql());
                value.addParams(when.getParams());
                value.appendHql(") then ");
            }
            value.appendHql(then.getHql());
            value.addParams(then.getParams());

        }
        if (!cases.isEmpty()) {
            value.appendHql(" end)");
        }
        params.setRequiresLiterals(previous);
        return value;
    }

    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        for(OnGoingCaseImpl<T> ongoingCase: cases) {
            validator.validateInScope(ongoingCase.getValue());
        }
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new CaseTypeSafeValue<>(context, this);
    }

}
