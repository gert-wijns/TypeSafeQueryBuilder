package be.shad.tsqb.selection;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.HqlQuery;
import be.shad.tsqb.HqlQueryBuilder;
import be.shad.tsqb.TypeSafeQuery;
import be.shad.tsqb.TypeSafeQuery;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.proxy.TypeSafeQueryProxyData;
import be.shad.tsqb.values.TypeSafeValue;

public class TypeSafeQueryProjections implements HqlQueryBuilder {
	private final TypeSafeQuery query;
	private final LinkedList<TypeSafeProjection> projections = new LinkedList<>();
	private Class<?> resultClass;

	public TypeSafeQueryProjections(TypeSafeQuery query) {
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
		List<TypeSafeQueryProxyData> invocations = query.dequeueInvocations();
		TypeSafeProjection projection = null;
		if( invocations.isEmpty() ) {
			if( select instanceof TypeSafeValue<?> ) {
				projection = new SubQueryTypeSafeProjection((TypeSafeQuery) select, propertyName);
			} else if( select instanceof TypeSafeQueryProxy ) {
				projection = new DirectTypeSafeProjection(((TypeSafeQueryProxy) select).
						getTypeSafeProxyData(), propertyName);
			}
		} else {
			TypeSafeQueryProxyData data = invocations.get(0);
			projection = new DirectTypeSafeProjection(data, propertyName);
			if( !query.isInScope(data) ) {
				throw new IllegalArgumentException("Attempting to use data which is not in scope. " + data);
			}
		}
		projections.add(projection);
	}

	@Override
	public void appendTo(HqlQuery query) {
		for(TypeSafeProjection projection: projections) {
			projection.appendTo(query);
		}
	}

}
