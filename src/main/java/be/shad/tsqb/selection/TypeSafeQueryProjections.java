package be.shad.tsqb.selection;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

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
     * 
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
        if( invocations.isEmpty() ) {
            if( select instanceof TypeSafeValue<?> ) {
                projection = new TypeSafeValueProjection((TypeSafeSubQuery<?>) select, propertyName);
            } else if( select instanceof TypeSafeQueryProxy ) {
                TypeSafeQueryProxyData data = ((TypeSafeQueryProxy) select).getTypeSafeProxyData();
                TypeSafeValue<?> value = new ReferenceTypeSafeValue<>(query, data);
                projection = new TypeSafeValueProjection(value, propertyName);
            }
        } else {
            TypeSafeQueryProxyData data = invocations.get(0);
            TypeSafeValue<?> value = new ReferenceTypeSafeValue<>(query, data);
            projection = new TypeSafeValueProjection(value, propertyName);
            if( !query.isInScope(data, null) ) {
                throw new IllegalArgumentException("Attempting to use data which is not in scope. " + data);
            }
        }
        projections.add(projection);
    }

    @Override
    public void appendTo(HqlQuery query) {
        query.setResultClass(resultClass);
        for(TypeSafeProjection projection: projections) {
            projection.appendTo(query);
        }
    }

}
