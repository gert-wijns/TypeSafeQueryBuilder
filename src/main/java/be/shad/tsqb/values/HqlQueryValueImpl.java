package be.shad.tsqb.values;

import java.util.LinkedList;
import java.util.List;

public class HqlQueryValueImpl implements HqlQueryValue {
	private List<Object> params = new LinkedList<>();
	private String hql;
	
	public HqlQueryValueImpl() {
		this("");
	}
	
	public HqlQueryValueImpl(String hql, List<Object> params) {
		this.hql = hql;
		if( params != null ) {
			this.params.addAll(params);
		}
	}

	public HqlQueryValueImpl(String hql, Object... params) {
		this.hql = hql;
		if( params != null ) {
			for(Object param: params) {
				addParam(param);
			}
		}
	}
	
	public String getHql() {
		return hql;
	}
	
	public void setHql(String hql) {
		this.hql = hql;
	}
	
	public void appendHql(String hql) {
		this.hql += hql;
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
