package be.shad.tsqb.restrictions;

import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * The restriction exists of three parts.
 * <ul>
 * <li>Left: the part left of an operator
 * <li>Operator: the operator
 * <li>Right: the part right of an operator
 * </ul>
 * Depending on the used operator, either left, right, or both parts
 * must not be null to get a valid restriction.
 * <p>
 * The <b>exists</b> can be used without a left part.<br>
 * The <b>is_null</b>, <b>is_not_null</b> can be used without a right part.<br>
 * The rest requires both parts.
 */
public class RestrictionImpl extends RestrictionChainableImpl implements Restriction {
    public final static String EQUAL = "=";
    public final static String IN = "in";
    public final static String NOT_IN = "not in";
    public final static String NOT_EQUAL = "<>";
    public final static String IS_NULL = "is null";
    public final static String IS_NOT_NULL = "is not null";
    public final static String EXISTS = "exists";
    
    private final RestrictionsGroupImpl group;
    private final TypeSafeQueryInternal query;
    
    private TypeSafeValue<?> left;
    private String operator;
    private TypeSafeValue<?> right;
    
    public RestrictionImpl(TypeSafeQueryInternal query, 
            RestrictionsGroupImpl restrictions) {
        this.group = restrictions;
        this.query = query;
    }

    @Override
    public RestrictionsGroup getRestrictionsGroup() {
        return group;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Continue the chain in the same group.
     */
    @Override
    public Restriction and(Restriction restriction) {
        return group.and(restriction);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Continue the chain in the same group.
     */
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
        query.validateInScope(value, group.getJoin());
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
