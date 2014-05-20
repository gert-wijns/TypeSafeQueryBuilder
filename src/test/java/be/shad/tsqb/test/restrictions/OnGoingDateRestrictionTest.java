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
package be.shad.tsqb.test.restrictions;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.domain.Style;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.DirectTypeSafeValue;

public class OnGoingDateRestrictionTest extends TypeSafeQueryTest {
    private Date date = new Date();
    private Date date2 = new Date(date.getTime() + TimeUnit.DAYS.toMillis(1));

    @Test
    public void testBefore() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).before(date);
        validate(" from Building hobj1 where hobj1.constructionDate < ?", date);
    }

    @Test
    public void testTypeSafeValueBefore() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).before(new DirectTypeSafeValue<Date>(query, date));
        validate(" from Building hobj1 where hobj1.constructionDate < ?", date);
    }

    @Test
    public void testAfter() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).after(date);
        validate(" from Building hobj1 where hobj1.constructionDate > ?", date);
    }

    @Test
    public void testTypeSafeValueAfter() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).after(new DirectTypeSafeValue<Date>(query, date));
        validate(" from Building hobj1 where hobj1.constructionDate > ?", date);
    }

    @Test
    public void testNotAfter() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).notAfter(date);
        validate(" from Building hobj1 where hobj1.constructionDate <= ?", date);
    }

    @Test
    public void testTypeSafeValueNotAfter() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).notAfter(new DirectTypeSafeValue<Date>(query, date));
        validate(" from Building hobj1 where hobj1.constructionDate <= ?", date);
    }

    @Test
    public void testNotBefore() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).notBefore(date);
        validate(" from Building hobj1 where hobj1.constructionDate >= ?", date);
    }

    @Test
    public void testTypeSafeValueNotBefore() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).notBefore(new DirectTypeSafeValue<Date>(query, date));
        validate(" from Building hobj1 where hobj1.constructionDate >= ?", date);
    }

    @Test
    public void testBetweenAsContinuedOngoingRestriction() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).after(date).before(date2);
        validate(" from Building hobj1 where hobj1.constructionDate > ? and hobj1.constructionDate < ?", date, date2);
    }

    @Test
    public void testBetweenAsContinuedOngoingRestrictionAlternative() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).after(date).and().before(date2);
        validate(" from Building hobj1 where hobj1.constructionDate > ? and hobj1.constructionDate < ?", date, date2);
    }

    @Test
    public void testNotBetweenAsContinuedOngoingRestriction() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).before(date).or().after(date2);
        validate(" from Building hobj1 where hobj1.constructionDate < ? or hobj1.constructionDate > ?", date, date2);
    }

    @Test
    public void testContinueAfterContinuedRestriction() {
        Building building = query.from(Building.class);
        query.where(building.getConstructionDate()).after(date).and().before(date2).and(building.getStyle()).eq(Style.the1980s);
        validate(" from Building hobj1 where hobj1.constructionDate > ? and hobj1.constructionDate < ? and hobj1.style = ?", date, date2, Style.the1980s);
    }

}