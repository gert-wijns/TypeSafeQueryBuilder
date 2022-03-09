/*
 * Copyright Gert Wijns gert.wijns@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.shad.tsqb.test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.dto.PersonValue;
import be.shad.tsqb.dto.TownValue;
import be.shad.tsqb.selection.parallel.SelectPair;

public class ManyToManyTests extends TypeSafeQueryTest {

	@Test
	public void testJoinManyToMany() {
		TestDataCreator creator = new TestDataCreator(getSessionFactory());

		Person p1 = creator.createTestPerson();
		Person p2 = creator.createTestPerson();
		Person p3 = creator.createTestPerson();

		p1.getTowns().add(p1.getTown());
		p1.getTowns().add(p2.getTown());
		p1.getTowns().add(p3.getTown());

		p2.getTowns().add(p2.getTown());
		p2.getTowns().add(p3.getTown());

		p3.getTowns().add(p3.getTown());

		getSessionFactory().getCurrentSession().save(p1);
		getSessionFactory().getCurrentSession().save(p2);
		getSessionFactory().getCurrentSession().save(p3);
		getSessionFactory().getCurrentSession().flush();
		getSessionFactory().getCurrentSession().clear();

		Person personPx = query.from(Person.class);
		Town townPx = query.join(personPx.getTowns());
		query.orderBy().asc(personPx.getId()).asc(townPx.getId());

		SelectPair<PersonValue, Collection<TownValue>> selectPair = query.select(SelectPair::new);
		selectPair.setFirst(query.groupSelectBy(
				query.subBuilder(PersonValue::builder)
								.id(personPx.getId())
								.thePersonsName(personPx.getName())
								.build()));
		query.subListBuilder(TownValue::builder, selectPair::setSecond).id(townPx.getId());

		validate("select hobj1.id as g1__id, hobj1.name as g1__thePersonsName, hobj2.id as g2__id " +
				"from Person hobj1 join hobj1.towns hobj2 " +
				"order by hobj1.id, hobj2.id");

		assertEquals(3, doQueryResult.size());
		@SuppressWarnings({"unchecked", "rawtypes"})
		List<SelectPair<PersonValue, Collection<TownValue>>> results = (List) doQueryResult;
		assertEquals(p1.getId(), results.get(0).getFirst().getId());
		assertEquals(p2.getId(), results.get(1).getFirst().getId());
		assertEquals(p3.getId(), results.get(2).getFirst().getId());

		Long t1Id = p1.getTown().getId();
		Long t2Id = p2.getTown().getId();
		Long t3Id = p3.getTown().getId();
		assertEquals(asList(t1Id, t2Id, t3Id), results.get(0).getSecond().stream().map(TownValue::getId).collect(toList()));
		assertEquals(asList(t2Id, t3Id), results.get(1).getSecond().stream().map(TownValue::getId).collect(toList()));
		assertEquals(singletonList(t3Id), results.get(2).getSecond().stream().map(TownValue::getId).collect(toList()));
	}

	@Test
	public void testJoinManyToManyInverse() {
		TestDataCreator creator = new TestDataCreator(getSessionFactory());

		Person p1 = creator.createTestPerson();
		Person p2 = creator.createTestPerson();
		Person p3 = creator.createTestPerson();

		p1.getTowns().add(p1.getTown());
		p1.getTowns().add(p2.getTown());
		p1.getTowns().add(p3.getTown());

		p2.getTowns().add(p2.getTown());
		p2.getTowns().add(p3.getTown());

		p3.getTowns().add(p3.getTown());

		getSessionFactory().getCurrentSession().save(p1);
		getSessionFactory().getCurrentSession().save(p2);
		getSessionFactory().getCurrentSession().save(p3);
		getSessionFactory().getCurrentSession().flush();
		getSessionFactory().getCurrentSession().clear();

		Town townPx = query.from(Town.class);
		Person personPx = query.join(townPx.getInhabitantsMany());
		query.orderBy().asc(personPx.getId()).asc(townPx.getId());

		SelectPair<PersonValue, Collection<TownValue>> selectPair = query.select(SelectPair::new);
		selectPair.setFirst(query.groupSelectBy(query.subBuilder(PersonValue::builder)
				.id(personPx.getId())
				.thePersonsName(personPx.getName())
				.build()));
		query.subListBuilder(TownValue::builder, selectPair::setSecond).id(townPx.getId());

		validate("select hobj2.id as g1__id, hobj2.name as g1__thePersonsName, hobj1.id as g2__id " +
				"from Town hobj1 join hobj1.inhabitantsMany hobj2 " +
				"order by hobj2.id, hobj1.id");

		assertEquals(3, doQueryResult.size());
		@SuppressWarnings({"unchecked", "rawtypes"})
		List<SelectPair<PersonValue, Collection<TownValue>>> results = (List) doQueryResult;
		assertEquals(p1.getId(), results.get(0).getFirst().getId());
		assertEquals(p2.getId(), results.get(1).getFirst().getId());
		assertEquals(p3.getId(), results.get(2).getFirst().getId());

		Long t1Id = p1.getTown().getId();
		Long t2Id = p2.getTown().getId();
		Long t3Id = p3.getTown().getId();
		assertEquals(asList(t1Id, t2Id, t3Id), results.get(0).getSecond().stream().map(TownValue::getId).collect(toList()));
		assertEquals(asList(t2Id, t3Id), results.get(1).getSecond().stream().map(TownValue::getId).collect(toList()));
		assertEquals(singletonList(t3Id), results.get(2).getSecond().stream().map(TownValue::getId).collect(toList()));
	}
}
