package be.shad.tsqb.domain.people;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import be.shad.tsqb.domain.DomainObject;

@Entity
@Table(name = "Relation")
public class Relation extends DomainObject {
	private static final long serialVersionUID = 75607517083941564L;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "ParentId", nullable = false)
	private Person parent;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "ChildId", nullable = false)
	private Person child;

	public Person getParent() {
		return parent;
	}

	public void setParent(Person parent) {
		this.parent = parent;
	}

	public Person getChild() {
		return child;
	}

	public void setChild(Person child) {
		this.child = child;
	}
	
}
