package be.shad.tsqb.restrictions;

public class RestrictionNode {
	private Restriction restriction;
	private RestrictionNodeType type;
	
	public RestrictionNode(Restriction restriction, RestrictionNodeType type) {
		this.restriction = restriction;
		this.type = type;
	}

	public Restriction getRestriction() {
		return restriction;
	}
	
	public RestrictionNodeType getType() {
		return type;
	}

}
