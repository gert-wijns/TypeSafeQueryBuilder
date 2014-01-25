package be.shad.tsqb;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.proxy.TypeSafeQueryProxyData;

public class TypeSafeQueryInvocationQueue {
	private List<TypeSafeQueryProxyData> invocationQueue = new LinkedList<>();

	public void invocationWasMade(TypeSafeQueryProxyData data) {
		invocationQueue.add(data);
	}
	
	public List<TypeSafeQueryProxyData> dequeueInvocations() {
		List<TypeSafeQueryProxyData> old = invocationQueue;
		invocationQueue = new LinkedList<>();
		return old;
	}
	
}
