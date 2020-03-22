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
package be.shad.tsqb.query;

import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryFrom;
import be.shad.tsqb.exceptions.FromAlreadyExistsException;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;

public class TypeSafeDeleteQueryImpl extends TypeSafeRootQueryImpl implements TypeSafeDeleteQuery {
    protected TypeSafeDeleteQueryImpl(CopyContext context, TypeSafeRootQueryImpl original) {
        super(context, original);
    }

    public TypeSafeDeleteQueryImpl(TypeSafeQueryHelper helper) {
        super(helper);
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeDeleteQueryImpl(context, this);
    }

    @Override
    public <T> T from(Class<T> fromClass, String name) {
        List<TypeSafeQueryFrom> froms = getDataTree().getFroms();
        if (!froms.isEmpty()) {
            throw new FromAlreadyExistsException("Delete query may only " +
                    "have a single from statement. From already exists for entity "
                    + froms.get(0).getRoot());
        }
        return super.from(fromClass, name);
    }

    @Override
    public String createEntityAlias() {
        // override to return an empty alias for the first
        // from statement (the entity which will be deleted from)
        if (getDataTree().getFroms().isEmpty()) {
            return "";
        } else {
            return super.createEntityAlias();
        }
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        HqlQueryValueImpl query = new HqlQueryValueImpl("delete from ");

        Class<?> entityClass = getDataTree().getFroms().get(0).getRoot().getPropertyType();
        query.appendHql(helper.getEntityName(entityClass));

        // append where part:
        HqlQueryValue hqlWhereRestrictions = getRestrictions().toHqlQueryValue(params);
        if (!hqlWhereRestrictions.getHql().isEmpty()) {
            query.appendHql(" where ");
            query.appendHql(hqlWhereRestrictions.getHql());
            query.addParams(hqlWhereRestrictions.getParams());
        }
        return query;
    }
}
