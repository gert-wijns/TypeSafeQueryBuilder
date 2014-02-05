package be.shad.tsqb.domain.people;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import be.shad.tsqb.domain.DomainObject;

@Entity
@Table(name = "PersonProperty")
public class PersonProperty extends DomainObject {
	private static final long serialVersionUID = 3104165712544802569L;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "PersonId", nullable = false)
	private Person person;
	
	@Column(nullable = false)
	private String propertyKey;
	
	@Column
	private String propertyValue;
	
	public Person getPerson() {
		return person;
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public void setPropertyKey(String propertyKey) {
		this.propertyKey = propertyKey;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
}
