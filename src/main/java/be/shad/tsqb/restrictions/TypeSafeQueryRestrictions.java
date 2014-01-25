package be.shad.tsqb.restrictions;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.HqlQuery;
import be.shad.tsqb.HqlQueryBuilder;

public class TypeSafeQueryRestrictions implements HqlQueryBuilder {
	private List<Restriction> restrictions = new LinkedList<>();
	
	public List<Restriction> getRestrictions() {
		return restrictions;
	}
	
	public void addRestriction(Restriction restriction) {
		restrictions.add(restriction);
	}

	@Override
	public void appendTo(HqlQuery query) {
		for(Restriction restriction: restrictions) {
			restriction.appendTo(query);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Restrictions [");
		for(Restriction restriction: restrictions) {
			sb.append("\n").append(restriction);
		}
		sb.append("]");
		return sb.toString();
	}
	
}
