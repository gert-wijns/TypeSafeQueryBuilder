package be.shad.tsqb.domain;

import static javax.persistence.FetchType.LAZY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import be.shad.tsqb.domain.usertype.Address;

@Entity
@Inheritance(strategy= InheritanceType.JOINED)
@Table(name = "Building")
public class Building extends DomainObject {
	private static final long serialVersionUID = -3953945063023583541L;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "TownId", nullable = false)
	private Town town;
	
	@Column
	private Style style;

	@Column
	private boolean occupied;

	@Column
	private Date constructionDate;
	
	@Type(type="be.shad.tsqb.domain.usertype.Address")
	private Address address;
	
	public Style getStyle() {
		return style;
	}
	
	public void setStyle(Style style) {
		this.style = style;
	}
	
	public boolean isOccupied() {
		return occupied;
	}
	
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Date getConstructionDate() {
		return constructionDate;
	}
	
	public void setConstructionDate(Date constructionDate) {
		this.constructionDate = constructionDate;
	}
	
}
