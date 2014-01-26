package be.shad.tsqb.restrictions;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.TypeSafeValue;

public class RestrictionBase implements Restriction, RestrictionChainable {
	public final static String EQUAL = "=";
	public final static String IN = "in";
	public final static String NOT_IN = "in";
	public final static String NOT_EQUAL = "<>";
	
	private final TypeSafeQueryInternal query;
	private TypeSafeValue<?> left;
	private String operator;
	private TypeSafeValue<?> right;
	
	public RestrictionBase(TypeSafeQueryInternal query) {
		this.query = query;
		this.query.getRestrictions().addRestriction(this);
	}

	@Override
	public OnGoingTextRestriction and(String value) {
		return new OnGoingTextRestriction(new RestrictionBase(query), value);
	}

	@Override
	public OnGoingTextRestriction andt(TypeSafeValue<String> value) {
		return new OnGoingTextRestriction(new RestrictionBase(query), value);
	}

	@Override
	public OnGoingSubQueryTextRestriction andt(TypeSafeSubQuery<String> value) {
		return new OnGoingSubQueryTextRestriction(new RestrictionBase(query), value);
	}

	@Override
	public OnGoingNumberRestriction and(Number value) {
		return new OnGoingNumberRestriction(new RestrictionBase(query), value);
	}
	
	@Override
	public OnGoingNumberRestriction andn(TypeSafeValue<Number> value) {
		return new OnGoingNumberRestriction(new RestrictionBase(query), value);
	}
	
	@Override
	public OnGoingSubQueryNumberRestriction andn(TypeSafeSubQuery<Number> value) {
		return new OnGoingSubQueryNumberRestriction(new RestrictionBase(query), value);
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
	}
	
	public TypeSafeValue<?> getRight() {
		return right;
	}
	
	public void setRight(TypeSafeValue<?> right) {
		this.right = right;
	}
	
	@Override
	public void appendTo(HqlQuery query) {
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
		query.appendWhere(value.getHql());
		query.addParams(value.getParams());
	}

}
