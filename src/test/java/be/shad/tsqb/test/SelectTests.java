package be.shad.tsqb.test;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.domain.House;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeSubQuery;


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

	@Test
	public void selectSubQueryValue() {
		TypeSafeRootQuery query = createQuery();
		House house = query.from(House.class);
		
		TypeSafeSubQuery<String> nameSubQuery = query.subquery(String.class);
		House houseSub = nameSubQuery.from(House.class);
		nameSubQuery.where(house.getId()).eq(houseSub.getId());
		
		nameSubQuery.select(houseSub.getName());

		query.selectValue(nameSubQuery);
		
		HqlQuery hql = doQuery(query);
		assertTrue("the house was selected from in the subquery", hql.getSelect().contains("from House hobj2"));
		assertTrue("the name should be selected in the subselect", hql.getSelect().contains("(select hobj2.name"));
	}

	@Test
	public void selectSubQueryValueAndProperty() {
		TypeSafeRootQuery query = createQuery();
		House house = query.from(House.class);
		
		TypeSafeSubQuery<String> nameSubQuery = query.subquery(String.class);
		House houseSub = nameSubQuery.from(House.class);
		nameSubQuery.where(house.getId()).eq(houseSub.getId());
		
		nameSubQuery.select(houseSub.getName());

		@SuppressWarnings("unchecked")
		MutablePair<Integer, String> result = query.select(MutablePair.class);
		result.setLeft(house.getFloors());
		result.setRight(nameSubQuery.getValue());

		HqlQuery hql = doQuery(query);
		hql.toString();
		assertTrue("the house was selected from in the subquery", hql.getSelect().contains("from House hobj2"));
		assertTrue("the name should be selected in the subselect", hql.getSelect().contains("(select hobj2.name"));
	}

	/**
	 * Selecting into a primitive setter should not fail.
	 */
	@Test
	public void selectPrimitiveSubQueryValue() {
		TypeSafeRootQuery query = createQuery();
		House house = query.from(House.class);
		
		TypeSafeSubQuery<Integer> nameSubQuery = query.subquery(Integer.class);
		House houseSub = nameSubQuery.from(House.class);
		nameSubQuery.where(house.getId()).eq(houseSub.getId());
		
		nameSubQuery.select(houseSub.getFloors());
		
		House houseResult = query.select(House.class);
		houseResult.setFloors(nameSubQuery.getValue());

		HqlQuery hql = doQuery(query);
		assertTrue("the house was selected from in the subquery", hql.getSelect().contains("from House hobj2"));
		assertTrue("the floors should be selected in the subselect", hql.getSelect().contains("(select hobj2.floors"));
	}
}
