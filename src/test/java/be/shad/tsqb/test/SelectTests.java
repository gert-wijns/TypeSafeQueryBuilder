package be.shad.tsqb.test;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.domain.House;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;


public class SelectTests extends TypeSafeQueryTest {

	/**
	 * Select from without a select statement
	 */
	@Test
	public void selectEntity() {
		TypeSafeRootQuery query = createQuery();
		query.from(House.class);
		
		HqlQuery hql = doQuery(query);
		assertTrue("no select should be present, simple 'from Entity'", hql.getSelect().equals(""));
	}

	/**
	 * Select a property of the from entity.
	 */
	@Test
	public void selectProperty() {
		TypeSafeRootQuery query = createQuery();
		House house = query.from(House.class);
		House result = query.select(House.class);
		result.setFloors(house.getFloors());
		
		HqlQuery hql = doQuery(query);
		assertTrue("floors should be selected into property 'floors'.", hql.getSelect().equals("select hobj1.floors as floors"));
	}

	/**
	 * Select the entity and a property of the entity
	 */
	@Test
	public void selectEntityAndPropertyOfEntity() {
		TypeSafeRootQuery query = createQuery();
		House house = query.from(House.class);

		@SuppressWarnings("unchecked")
		MutablePair<House, Integer> result = query.select(MutablePair.class);
		result.setLeft(house);
		result.setRight(house.getFloors());
		
		HqlQuery hql = doQuery(query);
		assertTrue("select house entity into left", hql.getSelect().contains("hobj1 as left"));
		assertTrue("select houses' floor into right", hql.getSelect().contains("hobj1.floors as right"));
	}

	/**
	 * Select a joined entity
	 */
	@Test
	public void selectJoinedEntity() {
		TypeSafeRootQuery query = createQuery();
		Town town = query.from(Town.class);
		Building building = query.join(town.getBuildings());
		
		query.selectValue(building);

		HqlQuery hql = doQuery(query);
		assertTrue("building, joined second", hql.getFrom().contains(".buildings hobj2"));
		assertTrue("select building", hql.getSelect().equals("select hobj2"));
	}

	/**
	 * Select a value of a joined entity
	 */
	@Test
	public void selectJoinedEntityProperty() {
		TypeSafeRootQuery query = createQuery();
		Town town = query.from(Town.class);
		Building building = query.join(town.getBuildings());
		
		query.selectValue(building.getId());

		HqlQuery hql = doQuery(query);
		assertTrue("buildings should be hobj2, it was joined second", hql.getFrom().contains(".buildings hobj2 "));
		assertTrue("the id of buildings should be selected", hql.getSelect().equals("select hobj2.id"));
	}
	
}
