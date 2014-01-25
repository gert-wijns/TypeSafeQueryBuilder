package be.shad.tsqb.values;

public class CaseTypeSafeValue<T extends Object> implements TypeSafeValue<T> {

	@Override
	public HqlQueryValue toHqlQueryValue() {
		return null;
	}

}
