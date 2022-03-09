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

import org.junit.Assert;
import org.junit.Test;

import be.shad.tsqb.domain.people.Person;

public class ToFormattedSqlQueryTest extends TypeSafeQueryTest {

	@Test
	public void testToFormattedSqlQuery() {
		Person from = query.from(Person.class);
		query.where(from.getId()).eq(1L);
		query.where(from.getName()).startsWith("Fred");

		String sql = query.toFormattedSqlQuery();

		Assert.assertEquals("select person0_.id as id1_9_, " +
				"person0_.age as age2_9_, " +
				"person0_.married as married3_9_, " +
				"person0_.name as name4_9_, " +
				"person0_.nickname as nickname5_9_, " +
				"person0_.sex as sex6_9_, " +
				"person0_.SpouseId as spouseid7_9_, " +
				"person0_.TownId as townid8_9_ " +
				"from Person person0_ " +
				"where person0_.id=1 and (person0_.name like 'Fred%')",
				sql);
	}
}
