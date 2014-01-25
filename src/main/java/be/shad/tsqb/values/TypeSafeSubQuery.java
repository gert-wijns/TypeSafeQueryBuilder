package be.shad.tsqb.values;

import be.shad.tsqb.EntityAliasProvider;
import be.shad.tsqb.TypeSafeQuery;
import be.shad.tsqb.TypeSafeQueryHelper;
import be.shad.tsqb.TypeSafeQueryInvocationQueue;

public class TypeSafeSubQuery<T extends Object> extends TypeSafeQuery implements TypeSafeValue<T> {

	public TypeSafeSubQuery(TypeSafeQueryHelper helper, 
			TypeSafeQuery parent, 
			EntityAliasProvider aliasProvider,
			TypeSafeQueryInvocationQueue invocationQueue) {
		super(helper, parent, aliasProvider, invocationQueue);
	}

	@Override
	public HqlQueryValue toHqlQueryValue() {
		return toHqlQuery();
	}

}
