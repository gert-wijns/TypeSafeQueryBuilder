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

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryFrom;
import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.exceptions.FromAlreadyExistsException;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.values.CustomTypeSafeValue;
import be.shad.tsqb.values.EntityTypeSafeValue;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeUpdateQueryImpl extends TypeSafeRootQueryImpl implements TypeSafeUpdateQueryInternal {
    private static final class TypeSafeUpdate {
        private TypeSafeQueryProxyData property;
        private TypeSafeValue<?> updateValue;
    }

    private final Deque<TypeSafeUpdate> updateValues = new LinkedList<>();

    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeUpdateQueryImpl(context, this);
    }

    protected TypeSafeUpdateQueryImpl(CopyContext context, TypeSafeUpdateQueryImpl original) {
        super(context, original);
        for(TypeSafeUpdate updateValue: original.updateValues) {
            TypeSafeUpdate copy = new TypeSafeUpdate();
            copy.property = context.get(updateValue.property);
            copy.updateValue = context.get(updateValue.updateValue);
            updateValues.add(copy);
        }
    }

    public TypeSafeUpdateQueryImpl(TypeSafeQueryHelper helper) {
        super(helper);
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
    public void assignUpdateValue(TypeSafeQueryProxyData property, TypeSafeValue<?> value) {
        TypeSafeUpdate updateValue = new TypeSafeUpdate();
        updateValue.property = property;
        updateValue.updateValue = value;
        updateValues.add(updateValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <VAL> VAL nullValue() {
        return (VAL) new CustomTypeSafeValue<>(this, Object.class, "NULL").select();
    }

    @Override
    public <E> TypeSafeValue<E> asReference(Class<E> entityClass, TypeSafeValue<Object> entityIdValue) {
        Class<Object> valueClass = entityIdValue.getValueClass();
        Class<?> idClass = helper.getEntityIdClass(entityClass);
        if (!idClass.equals(valueClass)) {
            throw new IllegalArgumentException("EntityIdValue (" + valueClass +
                    ") cannot represent entityClass ( " + idClass +
                    ")");
        }
        return new EntityTypeSafeValue<>(this, entityClass, entityIdValue);
    }

    @Override
    public <E> TypeSafeValue<E> asReference(Class<E> entityClass, Object entityId) {
        return asReference(entityClass, toValue(entityId));
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        if (updateValues.isEmpty() && !params.isBuildingForDisplay()) {
            throw new IllegalStateException("Update query without updateValues ...");
        }
        HqlQueryValueImpl query = new HqlQueryValueImpl("update ");

        Class<?> entityClass = getDataTree().getFroms().get(0).getRoot().getPropertyType();
        query.appendHql(helper.getEntityName(entityClass));

        query.appendHql(" set ");
        boolean first = true;
        for (TypeSafeUpdate updateValue: updateValues) {
            if (!first) query.appendHql(", ");
            HqlQueryValue updateValHql = updateValue.updateValue.toHqlQueryValue(params);
            query.appendHql(updateValue.property.getAlias() + " = " + updateValHql.getHql());
            query.addParams(updateValHql.getParams());
            first = false;
        }

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
