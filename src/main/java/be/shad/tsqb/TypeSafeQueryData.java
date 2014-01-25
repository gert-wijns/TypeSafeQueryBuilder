package be.shad.tsqb;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.restrictions.Restriction;

public class TypeSafeQueryData {

	private final Map<String, TypeSafeQueryData> children = new HashMap<>();
	private final TypeSafeQueryData parent;
	private final TypeSafeQueryProxy proxy;
	private String propertyPath;
	
	private List<Restriction> restrictions = new LinkedList<>();
	private String fetchMode;

	public TypeSafeQueryData(
			TypeSafeQueryData parent, 
			TypeSafeQueryProxy proxy) {
		this.parent = parent;
		this.proxy = proxy;
		if( parent != null ) {
			parent.addChild(this);
		}
	}

	public Class<?> getProxyFromClass() {
		return proxy.getClass().getSuperclass();
	}

	public TypeSafeQueryProxy getProxy() {
		return proxy;
	}

	public String getPropertyPath() {
		return propertyPath;
	}
	
	public void setPropertyPath(String propertyPath) {
		this.propertyPath = propertyPath;
	}
	
	public List<Restriction> getRestrictions() {
		return restrictions;
	}
	
	public void addRestriction(Restriction restriction) {
		restrictions.add(restriction);
	}
	
	public String getFetchMode() {
		return fetchMode;
	}

	public void setFetchMode(String fetchMode) {
		this.fetchMode = fetchMode;
	}

	public TypeSafeQueryData getParent() {
		return parent;
	}
	
	public void addChild(TypeSafeQueryData child) {
		children.put(child.getPropertyPath(), child);
	}
	
	public TypeSafeQueryData getChild(String propertyPath) {
		return children.get(propertyPath);
	}
	
	private int getDepth() {
		int depth = 0;
		TypeSafeQueryData parent = this.parent;
		while( parent != null ) {
			parent = parent.getParent();
			depth++;
		}
		return depth;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(format("\n"));
		for(int i=0, n=getDepth()*2; i < n; i++) {
			sb.append(" ");
		}
		String prefix = sb.toString();
		sb.append("Class [").append(getProxyFromClass().getSimpleName()).append("]");
		if( propertyPath != null ) {
			sb.append(" for path ").append(propertyPath);
		}
		if( fetchMode != null ) {
			sb.append(" with fetchMode ").append(fetchMode);
		}
		for(Restriction restriction: restrictions) {
			sb.append(prefix).append("  ").append(restriction);
		}
		
		//String nl = String.format("\n%" + getDepth()+1 + "s", "");
		for(TypeSafeQueryData child: children.values()) {
			sb.append(child.toString());
		}
		return sb.toString();
	}
}
