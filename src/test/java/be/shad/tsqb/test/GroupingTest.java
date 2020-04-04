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

import java.util.Date;

import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.values.CustomTypeSafeValue;

public class GroupingTest extends TypeSafeQueryTest {

    @Test
    public void testGroupBySingleValue() {
        Building building = query.from(Building.class);
        query.selectValue(building.getConstructionDate());
        query.groupBy(building.getConstructionDate());

        validate("select hobj1.constructionDate from Building hobj1 group by hobj1.constructionDate");
    }

    @Test
    public void testGroupByMoreThanOneValue() {
        Building building = query.from(Building.class);
        query.selectValue(query.groupBy(building.getConstructionDate()).select());
        query.selectValue(query.groupBy(building.getStyle()).select());

        validate("select hobj1.constructionDate, hobj1.style "
                + "from Building hobj1 "
                + "group by hobj1.constructionDate, hobj1.style");
    }

    @Test
    public void testGroupByCustomTypeSafeValue() {
        Building building = query.from(Building.class);

        query.selectValue(building.getConstructionDate());
        query.setHqlAlias(building, "b");
        query.groupBy(new CustomTypeSafeValue<>(query, Date.class, "b.constructionDate"));

        validate("select b.constructionDate from Building b group by b.constructionDate");
    }
}
