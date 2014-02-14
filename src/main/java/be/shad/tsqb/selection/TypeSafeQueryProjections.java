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
package be.shad.tsqb.selection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.DirectTypeSafeValue;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Container for all projections of a query.
 * Projections can be added using the {@link #project(Object, String)}.
 * This method should not be called from outside the query builder,
 * but it would be allowed if needed.
 */
public class TypeSafeQueryProjections implements HqlQueryBuilder {
    private final TypeSafeQueryInternal query;
    private final LinkedList<TypeSafeValueProjection> projections = new LinkedList<>();
    private Class<?> resultClass;

    public TypeSafeQueryProjections(TypeSafeQueryInternal query) {
        this.query = query;
    }

    public void setResultClass(Class<?> resultClass) {
        this.resultClass = resultClass;
    }
    
    public Class<?> getResultClass() {
        return resultClass;
    }
    
    public LinkedList<TypeSafeValueProjection> getProjections() {
        return projections;
    }

    public void addProjection(TypeSafeValueProjection projection) {
        projections.add(projection);
    }
    
    /**
     * First checks if a TypeSafeQueryValue.select() was called. 
     * This will take precendence over everything else.
     * <p>
     * Converts the input value to a type safe value if it isn't one yet when no invocations were made.
     * Covnerts the invocation data to a type safe value otherwise.
     */
    public void project(Object select, String propertyName) {
        TypeSafeValueProjection projection = null;
        TypeSafeValue<?> value = query.getRootQuery().dequeueSelectedValue();
        if( value != null ) {
            query.validateInScope(value, null);
            projection = new TypeSafeValueProjection(value, propertyName);
            projections.add(projection);
            return;
        }
        
        // No subquery was selected, check the queue or direct selections:
        List<TypeSafeQueryProxyData> invocations = query.dequeueInvocations();
        if( invocations.isEmpty() ) {
            if( select instanceof TypeSafeValue<?> ) {
                // any value selection (check if referenced)
                value = (TypeSafeValue<?>) select;
            } else if( select instanceof TypeSafeQueryProxy ) {
                // entity selection
                value = new ReferenceTypeSafeValue<>(query, ((TypeSafeQueryProxy) select).getTypeSafeProxyData());
            } else {
                // direct value selection
                value = new DirectTypeSafeValue<>(query, select);
            }
        } else {
            // value selection by proxy getter:
            value = new ReferenceTypeSafeValue<>(query, invocations.get(0));
        }
        query.validateInScope(value, null);
        projections.add(new TypeSafeValueProjection(value, propertyName));
    }

    @Override
    public void appendTo(HqlQuery query) {
        List<String[]> paths = new ArrayList<>();
        for(TypeSafeValueProjection projection: projections) {
            HqlQueryValue val = projection.getValue().toHqlQueryValue();
            if( projection.getValue() instanceof DirectTypeSafeValue<?> ) {
                val = this.query.getHelper().replaceParamsWithLiterals(val);
            }
            String alias = "";
            if( projection.getAlias() != null ) {
                alias = projection.getAlias().replace(".", "_");
                paths.add(projection.getAlias().split("\\."));
                alias = " as " + alias;
            }
            query.appendSelect(val.getHql() + alias);
            query.addParams(val.getParams());
        }
        if( !paths.isEmpty() ) {
            query.setResultTransformer(new TypeSafeQueryResultTransformer(resultClass, paths));
        }
    }

}
