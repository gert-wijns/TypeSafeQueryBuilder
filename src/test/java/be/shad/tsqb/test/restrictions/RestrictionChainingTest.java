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

import static be.shad.tsqb.restrictions.RestrictionsGroupImpl.group;
import static java.lang.Boolean.FALSE;
import static java.math.BigDecimal.ZERO;

import org.junit.Test;

import be.shad.tsqb.domain.House;
import be.shad.tsqb.test.TypeSafeQueryTest;

public class RestrictionChainingTest extends TypeSafeQueryTest {

    @Test
    public void testAnd() {
        House house = query.from(House.class);
        query.where(house.getFloors()).gt(4).and(house.isOccupied()).isFalse();
        validate(" from House hobj1 where hobj1.floors > ? and hobj1.occupied = ?", 4, FALSE);
    }

    @Test
    public void testOr() {
        House house = query.from(House.class);
        query.where(house.getFloors()).gt(4).or(house.isOccupied()).isFalse();
        validate(" from House hobj1 where hobj1.floors > ? or hobj1.occupied = ?", 4, FALSE);
    }
    
    @Test
    public void testAnds() {
        House house = query.from(House.class);
        query.where(house.getFloors()).gt(4).and(house.isOccupied()).isFalse().and(house.getPrice()).eq(ZERO);
        validate(" from House hobj1 where hobj1.floors > ? and hobj1.occupied = ? and hobj1.price = ?", 4, FALSE, ZERO);
    }

    @Test
    public void testOrs() {
        House house = query.from(House.class);
        query.where(house.getFloors()).gt(4).or(house.isOccupied()).isFalse().or(house.getPrice()).eq(ZERO);
        validate(" from House hobj1 where hobj1.floors > ? or hobj1.occupied = ? or hobj1.price = ?", 4, FALSE, ZERO);
    }

    @Test
    public void testGroups() {
        House house = query.from(House.class);
        
        // hopelessly complex grouping:
        query.where().and(group(query).
                and(house.getFloors()).gt(4).and(group(query).
                        and(house.isOccupied()).isFalse().
                        or(house.getPrice()).eq(ZERO)
                ).and(group(query).
                        and(house.getName()).startsWith("Cas").
                        or(house.getName()).startsWith("Chu")
                ));

        validate(" from House hobj1 where (hobj1.floors > ? and (hobj1.occupied = ? or hobj1.price = ?) and (hobj1.name like ? or hobj1.name like ?))", 
                4, FALSE, ZERO, "Cas%", "Chu%");
    }
    
}
