package be.shad.tsqb.selection;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.values.DirectTypeSafeValue;
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
    private final LinkedList<TypeSafeProjection> projections = new LinkedList<>();
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
    
    public LinkedList<TypeSafeProjection> getProjections() {
        return projections;
    }
    
    /**
     * First checks if a TypeSafeQueryValue.select() was called. 
     * This will take precendence over everything else.
     * <p>
     * Converts the input value to a type safe value if it isn't one yet when no invocations were made.
     * Covnerts the invocation data to a type safe value otherwise.
     */
    public void project(Object select, String propertyName) {
        TypeSafeProjection projection = null;
        if( query instanceof TypeSafeRootQueryInternal ) {
            TypeSafeValue<?> value = ((TypeSafeRootQueryInternal) query).dequeueSelectedValue();
            if( value != null ) {
                projection = new TypeSafeValueProjection(value, propertyName);
                projections.add(projection);
                return;
            }
        }
        
        // No subquery was selected, check the queue or direct selections:
        List<TypeSafeQueryProxyData> invocations = query.dequeueInvocations();
        TypeSafeValue<?> value = null;
        if( invocations.isEmpty() ) {
            if( select instanceof TypeSafeValue<?> ) {
                // any value selection (check if referenced)
                if( select instanceof ReferenceTypeSafeValue<?> ) {
                    validateInScope(((ReferenceTypeSafeValue<?>) select).getData());
                }
                value = (TypeSafeValue<?>) select;
            } else if( select instanceof TypeSafeQueryProxy ) {
                // entity selection
                TypeSafeQueryProxyData data = validateInScope(((TypeSafeQueryProxy) select).getTypeSafeProxyData());
                value = new ReferenceTypeSafeValue<>(query, data);
            } else {
                // direct value selection
                value = new DirectTypeSafeValue<>(query, select);
            }
        } else {
            // value selection by proxy getter:
            TypeSafeQueryProxyData data = validateInScope(invocations.get(0));
            value = new ReferenceTypeSafeValue<>(query, data);
        }
        projections.add(new TypeSafeValueProjection(value, propertyName));
    }
    
    /**
     * Check if data is in scope, must be available on this query or one of the parent queries.
     */
    private TypeSafeQueryProxyData validateInScope(TypeSafeQueryProxyData data) {
        if( !query.isInScope(data, null) ) {
            throw new IllegalArgumentException("Attempting to use data which is not in scope. " + data);
        }
        return data;
    }

    @Override
    public void appendTo(HqlQuery query) {
        query.setResultClass(resultClass);
        for(TypeSafeProjection projection: projections) {
            projection.appendTo(query);
        }
    }

}
