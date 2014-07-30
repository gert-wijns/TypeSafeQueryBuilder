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
import be.shad.tsqb.domain.Style;

public class HavingTests extends TypeSafeQueryTest {

    @Test
    public void testHavingSingleValue() {
        Building building = query.from(Building.class);
        query.select(building.getConstructionDate());
        query.groupBy(building.getConstructionDate());

        Date dateArg = new Date();
        query.having(building.getConstructionDate()).after(dateArg);

        validate("select hobj1.constructionDate from Building hobj1 group by hobj1.constructionDate having hobj1.constructionDate > :np1", dateArg);
    }

    @Test
    public void testGroupByMoreThanOneValue() {
        Building building = query.from(Building.class);
        query.select(building.getConstructionDate());
        query.select(building.getStyle());
        query.groupBy(building.getConstructionDate());
        query.groupBy(building.getStyle());

        Date dateArg = new Date();
        query.having(building.getConstructionDate()).before(dateArg);
        query.having(building.getStyle()).eq(Style.the1980s);
        
        validate("select hobj1.constructionDate, hobj1.style "
                + "from Building hobj1 "
                + "group by hobj1.constructionDate, hobj1.style "
                + "having hobj1.constructionDate < :np1 and hobj1.style = :np2", 
                dateArg, Style.the1980s);
    }

}
