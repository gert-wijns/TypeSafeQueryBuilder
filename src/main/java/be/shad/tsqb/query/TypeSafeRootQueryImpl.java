package be.shad.tsqb.query;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.helper.TypeSafeQueryHelper;

public class TypeSafeRootQueryImpl extends AbstractTypeSafeQuery implements TypeSafeRootQuery, TypeSafeRootQueryInternal {
	
	private List<TypeSafeQueryProxyData> invocationQueue = new LinkedList<>();
	private int entityAliasCount = 1;

	public TypeSafeRootQueryImpl(TypeSafeQueryHelper helper) {
		super(helper);
		setRootQuery(this);
	}

	/**
	 * Track calls on the created proxies.
	 */
	public void invocationWasMade(TypeSafeQueryProxyData data) {
		invocationQueue.add(data);
	}

	/**
	 * Dequeue all proxy calls and use them to append
	 * items to the query.
	 */
	public List<TypeSafeQueryProxyData> dequeueInvocations() {
		List<TypeSafeQueryProxyData> old = invocationQueue;
		invocationQueue = new LinkedList<>();
		return old;
	}
	
	@Override
	public TypeSafeQueryProxyData dequeueInvocation() {
		List<TypeSafeQueryProxyData> invocations = dequeueInvocations();
		if( invocations.isEmpty() ) {
			return null;
		}
		if( invocations.size() > 1 ) {
			throw new IllegalStateException(String.format("There are %d invocations pending. Only 1 should be pending. "
					+ "The one that was used to call join(value, joinType).", invocations.size()));
		}
		return invocations.get(0);
	}

	@Override
	public String createEntityAlias() {
		return "hobj"+ entityAliasCount++;
	}

	@Override
	public <T> T select(Class<T> resultClass) {
		return helper.createTypeSafeSelectProxy(this, resultClass);
	}

}
