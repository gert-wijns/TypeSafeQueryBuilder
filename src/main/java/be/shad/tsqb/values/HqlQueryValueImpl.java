package be.shad.tsqb.values;

import java.util.LinkedList;
import java.util.List;

public class HqlQueryValueImpl implements HqlQueryValue {
	private List<Object> params = new LinkedList<>();
	private StringBuilder hql;
	
	public HqlQueryValueImpl() {
		this("");
	}
	
	public HqlQueryValueImpl(String hql, List<Object> params) {
		this.hql = new StringBuilder(hql);
		if( params != null ) {
			this.params.addAll(params);
		}
	}

	public HqlQueryValueImpl(String hql, Object... params) {
		this.hql = new StringBuilder(hql);
		if( params != null ) {
			for(Object param: params) {
				addParam(param);
			}
		}
	}
	
	public String getHql() {
		return hql.toString();
	}
	
	public void setHql(String hql) {
		this.hql = new StringBuilder(hql);
	}
	
	public StringBuilder appendHql(String hql) {
		this.hql.append(hql);
		return this.hql;
	}
	
	public Object[] getParams() {
		return params.toArray();
	}
	
	public void addParam(Object param) {
		params.add(param);
	}

	public void addParams(Object[] params) {
		for(Object param: params) {
			this.params.add(param);
		}
	}
	
}
