package be.shad.tsqb.restrictions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueImpl;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Groups restrictions, either to restrict the where clause of a query or to restrict the with clause of a join.
 * <p>
 * A restriction group may be nested, to group a sequence of 'ors' in one part of a query for example.
 */
public class RestrictionsGroupImpl extends RestrictionChainableImpl implements RestrictionProvider, Restriction, RestrictionsGroupInternal {
    private final TypeSafeQueryInternal query;
    private final TypeSafeQueryProxyData join;
    private List<RestrictionNode> restrictions;
    
    public RestrictionsGroupImpl(TypeSafeQueryInternal query,
            TypeSafeQueryProxyData join) {
        this.query = query;
        this.join = join;
        this.restrictions = new ArrayList<>();
    }
    
    @Override
    public RestrictionChainable where(RestrictionsGroup group) {
        return and(group.getRestrictions());
    }
    
    @Override
    public Restriction where(Restriction restriction) {
        return and(restriction);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionsGroupImpl getRestrictionsGroup() {
        return this;
    }
    
    /**
     * Creates a new group which is used to restrict the where part of a query.
     */
    public static RestrictionsGroupImpl group(TypeSafeQuery query) {
        return group(query, null);
    }

    /**
     * Creates a new group which is used to restrict the with part of a join.
     * The join data is kept in order to be able to validate if the used values are in scope.
     */
    public static RestrictionsGroupImpl group(TypeSafeQuery query, TypeSafeQueryProxyData join) {
        return new RestrictionsGroupImpl((TypeSafeQueryInternal) query, join);
    }
    
    /**
     * The joined entity proxy data, might be null if this group is not used in a with (restrictions) clause.
     */
    public TypeSafeQueryProxyData getJoin() {
        return join;
    }
    
    /**
     * Loops the restrictions and links them together with ands and ors.
     */
    @Override
    public HqlQueryValueImpl toHqlQueryValue() {
        HqlQueryValueImpl value = new HqlQueryValueImpl();
        for(RestrictionNode item: restrictions) {
            HqlQueryValue nextValue = item.getRestriction().toHqlQueryValue();
            if( item.getType() == RestrictionNodeType.And ) {
                value.appendHql(" and ");
            } else if( item.getType() == RestrictionNodeType.Or ) {
                value.appendHql(" or ");
            } // else null, root
            value.appendHql(nextValue.getHql());
            value.addParams(nextValue.getParams());
        }
        return value;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Restrictions [");
        for(RestrictionNode item: restrictions) {
            sb.append("\n").append(item.getRestriction());
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable where() {
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Restriction and(Restriction restriction) {
        return add(restriction, RestrictionNodeType.And);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable and(RestrictionsGroup group) {
        return and(group.getRestrictions());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestrictionChainable or(RestrictionsGroup group) {
        return or(group.getRestrictions());
    }

    /**
     * {@inheritDoc}
     */
    public Restriction or(Restriction restriction) {
        return add(restriction, RestrictionNodeType.Or);
    }

    /**
     * {@inheritDoc}
     */
    public RestrictionImpl and() {
        return add(RestrictionNodeType.And);
    }

    /**
     * {@inheritDoc}
     */
    public RestrictionImpl or() {
        return add(RestrictionNodeType.Or);
    }

    /**
     * Creates the restrictions and adds it to the chain, with the given <code>type</code>.
     */
    private RestrictionImpl add(RestrictionNodeType type) {
        RestrictionImpl next = new RestrictionImpl(query, this);
        return (RestrictionImpl) add(next, type);
    }
    
    /**
     * Wraps the restriction in a group if its group was different.
     * This happens when a group was created to group 'ors' for example,
     * and this group is added to this group to add it to the query restrictions.
     * <p>
     * Example:
     * <pre>
     * Person person = query.from(Person.class);
     *  query.where(person.isMarried()).isTrue().
     *          and(RestrictionsGroup.group(query).
     *              and(person.getName()).startsWith("Jef").
     *               or(person.getName()).startsWith("John"));
     * </pre>
     * The RestrictionsGroup is created, and restrictions are added using the chaining methods.
     * At the end, when all the chaining finishes, the last restriction of the chain is returned
     * and add to this group. At this point it is detected that the restriction was part
     * of a sub-group because its restriction group is different.
     */
    private Restriction add(Restriction restriction, RestrictionNodeType type) {
        if( restriction.getRestrictionsGroup() != this ) {
            restriction = new RestrictionWrapper(this, restriction.getRestrictionsGroup().getRestrictions());
        }
        restrictions.add(new RestrictionNode(restriction, restrictions.isEmpty() ? null: type));
        return restriction;
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> whereEnum(TypeSafeValue<E> value) {
        return andEnum(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public <E extends Enum<E>> OnGoingEnumRestriction<E> where(E value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingBooleanRestriction whereBoolean(TypeSafeValue<Boolean> value) {
        return andBoolean(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingBooleanRestriction where(Boolean value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingNumberRestriction whereNumber(TypeSafeValue<Number> value) {
        return andNumber(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingNumberRestriction where(Number value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingDateRestriction whereDate(TypeSafeValue<Date> value) {
        return andDate(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingDateRestriction where(Date value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingTextRestriction whereString(TypeSafeValue<String> value) {
        return andString(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public OnGoingTextRestriction where(String value) {
        return and(value);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public RestrictionChainable whereExists(TypeSafeSubQuery<?> subquery) {
        return andExists(subquery);
    }

    /**
     * Delegate the call to and().
     */
    @Override
    public Restriction getRestrictions() {
        return this;
    }

}
