package be.shad.tsqb.values;

import java.util.LinkedList;
import java.util.List;

public class CoalesceTypeSafeValue<T extends Object> implements TypeSafeValue<T> {
	private List<TypeSafeValue<T>> values = new LinkedList<>();
	
	public void addValue(TypeSafeValue<T> value) {
		this.values.add(value);
	}

	@Override
	public HqlQueryValue toHqlQueryValue() {
		StringBuilder coalesce = new StringBuilder();
		List<Object> params = new LinkedList<>();
		for(TypeSafeValue<T> value: values) {
			if( coalesce.length() > 0 ) {
				coalesce.append(",");
			} else {
				coalesce.append("coalesce (");
			}
			HqlQueryValue valueHql = value.toHqlQueryValue();
			coalesce.append(valueHql.getHql());
			for(Object param: valueHql.getParams()) {
				params.add(param);
			}
		}
		String hql = "";
		if( coalesce.length() > 0 ) {
			hql = coalesce.append(")").toString();
		}
		return new HqlQueryValueImpl(hql, params);
	}

}
