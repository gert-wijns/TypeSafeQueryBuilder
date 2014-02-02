package be.shad.tsqb.restrictions;

import static java.lang.String.format;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

public class RestrictionImpl extends RestrictionChainableImpl implements Restriction {
	public final static String EQUAL = "=";
	public final static String IN = "in";
	public final static String NOT_IN = "not in";
	public final static String NOT_EQUAL = "<>";
	public final static String IS_NULL = "is null";
	public final static String IS_NOT_NULL = "is not null";
	public final static String EXISTS = "exists";
	
	private final RestrictionsGroup group;
	private final TypeSafeQueryInternal query;
	
	private TypeSafeValue<?> left;
	private String operator;
	private TypeSafeValue<?> right;
	
	public RestrictionImpl(TypeSafeQueryInternal query, 
			RestrictionsGroup restrictions) {
		this.group = restrictions;
		this.query = query;
	}

	@Override
	public RestrictionsGroup getRestrictionsGroup() {
		return group;
	}

	@Override
	public Restriction and(Restriction restriction) {
		return group.and(restriction);
	}
	
	@Override
	public Restriction or(Restriction restriction) {
		return group.or(restriction);
	}

	@Override
	public RestrictionImpl and() {
		return group.and();
	}
	
	@Override
	public RestrictionImpl or() {
		return group.or();
	}
	
	public TypeSafeQueryInternal getQuery() {
		return query;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public TypeSafeValue<?> getLeft() {
		return left;
	}
	
	public void setLeft(TypeSafeValue<?> left) {
		this.left = left;
		validateInScope(left);
	}
	
	public TypeSafeValue<?> getRight() {
		return right;
	}
	
	public void setRight(TypeSafeValue<?> right) {
		this.right = right;
		validateInScope(right);
	}
	
	private void validateInScope(TypeSafeValue<?> value) {
		if( value instanceof ReferenceTypeSafeValue<?> ) {
			if(!query.isInScope(((ReferenceTypeSafeValue<?>) value).getData(), group.getJoin())) {
				throw new IllegalArgumentException(format("Attempting to restrict with data which is not in scope. The data: [%s].", value));
			}
		}
	}
	
	@Override
	public HqlQueryValue toHqlQueryValue() {
		HqlQueryValueImpl value = new HqlQueryValueImpl();
		if( left != null ) {
			HqlQueryValue hqlQueryValue = left.toHqlQueryValue();
			value.appendHql(hqlQueryValue.getHql());
			value.addParams(hqlQueryValue.getParams());
		}
		if( operator != null ) {
			if( left != null ) {
				value.appendHql(" ");
			}
			value.appendHql(operator + " ");
		}
		if( right != null ) {
			HqlQueryValue hqlQueryValue = right.toHqlQueryValue();
			value.appendHql(hqlQueryValue.getHql());
			value.addParams(hqlQueryValue.getParams());
		}
		return value;
	}

}
