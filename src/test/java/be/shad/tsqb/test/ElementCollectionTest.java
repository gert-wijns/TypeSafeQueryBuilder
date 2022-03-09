package be.shad.tsqb.test;

import static be.shad.tsqb.domain.ElementCollectionHolder.PropertyType.PLANNING;
import static be.shad.tsqb.domain.ElementCollectionHolder.PropertyType.SALES;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import be.shad.tsqb.domain.ElementCollectionHolder;
import be.shad.tsqb.domain.ElementCollectionHolder.PropertyType;
import be.shad.tsqb.domain.ElementCollectionHolder.PropertyValue;
import be.shad.tsqb.joins.MapJoin;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.selection.parallel.SelectPair;
import be.shad.tsqb.selection.parallel.SelectTriplet;

public class ElementCollectionTest extends TypeSafeQueryTest {

	@Test
	public void testElementCollectionEnumKeyEmbeddableValue() {
		ElementCollectionHolder holder = new ElementCollectionHolder();
		holder.setId(1L);
		holder.getPropertiesByEnum().put(PLANNING, value("A", "B"));
		holder.getPropertiesByEnum().put(SALES, value("C", "D"));
		getSessionFactory().getCurrentSession().save(holder);

		ElementCollectionHolder hobj1 = query.from(ElementCollectionHolder.class);
		MapJoin<PropertyType, PropertyValue> hobj2 = query.join(hobj1.getPropertiesByEnum(), JoinType.Left);
		query.joinWith(hobj2).where(hobj2.getKey()).eq(PLANNING);

		@SuppressWarnings("unchecked")
		SelectPair<PropertyType, String> select = query.select(SelectPair.class);
		select.setFirst(hobj2.getKey());
		select.setSecond(hobj2.getValue().getValue1());

		validate("select index(hobj2) as first, hobj2.value1 as second " +
				"from ElementCollectionHolder hobj1 " +
				"left join hobj1.propertiesByEnum hobj2 with index(hobj2) = :np1",
				PLANNING);

		SelectPair<PropertyType, String> result = getSingleQueryResults();
		Assert.assertEquals(PLANNING, result.getFirst());
		Assert.assertEquals("A", result.getSecond());
	}

	@Test
	public void testElementCollectionStringKeyEmbeddableValue() {
		ElementCollectionHolder holder = new ElementCollectionHolder();
		holder.setId(1L);
		holder.getPropertiesByString().put(PLANNING.toString(), value("A", "B"));
		holder.getPropertiesByString().put(SALES.toString(), value("C", "D"));
		getSessionFactory().getCurrentSession().save(holder);

		ElementCollectionHolder hobj1 = query.from(ElementCollectionHolder.class);
		MapJoin<String, PropertyValue> hobj2 = query.join(hobj1.getPropertiesByString(), JoinType.Left);
		query.joinWith(hobj2).where(hobj2.getKey()).eq(PLANNING.toString());

		@SuppressWarnings("unchecked")
		SelectPair<String, String> select = query.select(SelectPair.class);
		select.setFirst(hobj2.getKey());
		select.setSecond(hobj2.getValue().getValue1());

		validate("select index(hobj2) as first, hobj2.value1 as second " +
						"from ElementCollectionHolder hobj1 " +
						"left join hobj1.propertiesByString hobj2 with index(hobj2) = :np1",
				PLANNING.toString());

		SelectPair<String, String> result = getSingleQueryResults();
		Assert.assertEquals(PLANNING.toString(), result.getFirst());
		Assert.assertEquals("A", result.getSecond());
	}

	@Test
	public void testElementCollectionStringKeyValue() {
		ElementCollectionHolder holder = new ElementCollectionHolder();
		holder.setId(1L);
		holder.getValuesByString().put(PLANNING.toString(), "A");
		holder.getValuesByString().put(SALES.toString(), "B");
		getSessionFactory().getCurrentSession().save(holder);

		ElementCollectionHolder hobj1 = query.from(ElementCollectionHolder.class);
		MapJoin<String, String> hobj2 = query.join(hobj1.getValuesByString(), JoinType.Left);
		query.joinWith(hobj2).where(hobj2.getKey()).eq(PLANNING.toString());

		@SuppressWarnings("unchecked")
		SelectPair<String, String> select = query.select(SelectPair.class);
		select.setFirst(hobj2.getKey());
		select.setSecond(hobj2.getValue());

		validate("select index(hobj2) as first, hobj2 as second " +
						"from ElementCollectionHolder hobj1 " +
						"left join hobj1.valuesByString hobj2 with index(hobj2) = :np1",
				PLANNING.toString());

		SelectPair<String, String> result = getSingleQueryResults();
		Assert.assertEquals(PLANNING.toString(), result.getFirst());
		Assert.assertEquals("A", result.getSecond());
	}

	@Test
	public void testElementCollectionSelectKeyValue() {
		ElementCollectionHolder holder = new ElementCollectionHolder();
		holder.setId(1L);
		holder.getValuesByString().put(PLANNING.toString(), "A");
		holder.getValuesByString().put(SALES.toString(), "B");
		holder.getPropertiesByEnum().put(PLANNING, value("PV1", "PV2"));
		holder.getPropertiesByEnum().put(SALES, value("SV1", "SV2"));
		getSessionFactory().getCurrentSession().save(holder);

		ElementCollectionHolder hobj1 = query.from(ElementCollectionHolder.class);
		MapJoin<String, String> hobj2 = query.join(hobj1.getValuesByString());
		MapJoin<PropertyType, PropertyValue> hobj3 = query.join(hobj1.getPropertiesByEnum());
		query.orderBy().asc(hobj2.getKey()).asc(hobj3.getKey());

		SelectTriplet<Long,
				Collection<SelectPair<String, String>>,
				Collection<SelectPair<PropertyType, PropertyValue>>> selectDto = query.select(SelectTriplet.class);
		SelectPair<String, String> second = query.subListBuilder(SelectPair::new, selectDto::setSecond);
		SelectPair<PropertyType, PropertyValue> third = query.subListBuilder(SelectPair::new, selectDto::setThird);

		selectDto.setFirst(query.groupSelectBy(hobj1.getId()));
		second.setFirst(hobj2.getKey());
		second.setSecond(hobj2.getValue());
		third.setFirst(hobj3.getKey());
		third.setSecond(hobj3.getValue());

		validate("select hobj1.id as first, " +
						"index(hobj2) as g1__first, hobj2 as g1__second, " +
						"index(hobj3) as g2__first, hobj3 as g2__second " +
						"from ElementCollectionHolder hobj1 " +
						"join hobj1.valuesByString hobj2 "+
						"join hobj1.propertiesByEnum hobj3 " +
						"order by index(hobj2), index(hobj3)");

		SelectTriplet<Long,
				Collection<SelectPair<String, String>>,
				Collection<SelectPair<PropertyType, PropertyValue>>> result = getSingleQueryResults();
		Assert.assertEquals(Arrays.asList(
				new SelectPair<>(PLANNING.toString(), "A"),
				new SelectPair<>(SALES.toString(), "B")),
				result.getSecond());
		Assert.assertEquals(Arrays.asList(
				new SelectPair<>(PLANNING, value("PV1", "PV2")),
				new SelectPair<>(SALES, value("SV1", "SV2"))),
				result.getThird());

	}

	private PropertyValue value(String value1, String value2) {
		PropertyValue value = new PropertyValue();
		value.setValue1(value1);
		value.setValue2(value2);
		return value;
	}
}
