package be.shad.tsqb.values;

import java.util.Collection;

public class CollectionTypeSafeValue<T> implements TypeSafeValue<T> {
	private Collection<T> value;
	
	public CollectionTypeSafeValue(Collection<T> value) {
		this.value = value;
	}

	public Collection<T> getValue() {
		return value;
	}

	public void setValue(Collection<T> value) {
		this.value = value;
	}

	@Override
	public HqlQueryValueImpl toHqlQueryValue() {
		StringBuilder sb = new StringBuilder("(");
		for(int i=0; i < value.size(); i++) {
			if( sb.length() > 0 ) {
				sb.append(", ");
			}
			sb.append("?");
		}
		sb.append(")");
		return new HqlQueryValueImpl(sb.toString(), value.toArray());
	}

}
