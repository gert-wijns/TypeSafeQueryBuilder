package be.shad.tsqb.restrictions;

import java.util.ArrayList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.HqlQueryValue;
import be.shad.tsqb.values.HqlQueryValueBuilder;
import be.shad.tsqb.values.HqlQueryValueImpl;

public class RestrictionsGroup extends RestrictionChainableImpl implements RestrictionChainable, RestrictionProvider, Restriction, HqlQueryValueBuilder {
	private final TypeSafeQueryInternal query;
	private final TypeSafeQueryProxyData join;
	private List<RestrictionNode> restrictions;
	
	public RestrictionsGroup(TypeSafeQueryInternal query,
			TypeSafeQueryProxyData join) {
		this.query = query;
		this.join = join;
		this.restrictions = new ArrayList<>();
	}
	
	@Override
	public RestrictionsGroup getRestrictionsGroup() {
		return this;
	}
	
	public static RestrictionsGroup group(TypeSafeQuery query) {
		return group(query, null);
	}
	
	public static RestrictionsGroup group(TypeSafeQuery query, TypeSafeQueryProxyData join) {
		return new RestrictionsGroup((TypeSafeQueryInternal) query, join);
	}
	
	public TypeSafeQueryProxyData getJoin() {
		return join;
	}
	
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

	public Restriction and(Restriction restriction) {
		return add(restriction, RestrictionNodeType.And);
	}

	public Restriction or(Restriction restriction) {
		return add(restriction, RestrictionNodeType.Or);
	}

	public RestrictionImpl and() {
		return add(RestrictionNodeType.And);
	}

	public RestrictionImpl or() {
		return add(RestrictionNodeType.Or);
	}
	
	private RestrictionImpl add(RestrictionNodeType type) {
		RestrictionImpl next = new RestrictionImpl(query, this);
		return (RestrictionImpl) add(next, type);
	}
	
	private Restriction add(Restriction restriction, RestrictionNodeType type) {
		if( restriction.getRestrictionsGroup() != this ) {
			restriction = new RestrictionWrapper(this, restriction.getRestrictionsGroup());
		}
		restrictions.add(new RestrictionNode(restriction, restrictions.isEmpty() ? null: type));
		return restriction;
	}

}
