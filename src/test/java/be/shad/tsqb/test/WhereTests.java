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

import org.junit.Test;

import be.shad.tsqb.domain.House;
import be.shad.tsqb.domain.Style;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.restrictions.RestrictionsGroup;
import be.shad.tsqb.restrictions.RestrictionsGroupFactory;

public class WhereTests extends TypeSafeQueryTest {

    /**
     * Where left is a reference and right is a value
     */
    @Test
    public void whereEnumValueEquals() {
        House house = query.from(House.class);
        query.where(house.getStyle()).eq(Style.the1980s);
        validate(" from House hobj1 where hobj1.style = ?", Style.the1980s);
    }

    /**
     * Where left and right are both references
     */
    @Test
    public void whereByReference() {
        House house1 = query.from(House.class);
        House house2 = query.from(House.class);
        query.where(house1.getStyle()).eq(house2.getStyle());
        validate(" from House hobj1, House hobj2 where hobj1.style = hobj2.style");
    }

    /**
     * Where left is a reference and right is a subquery value
     */
    @Test
    public void whereBySubquery() {
        House house = query.from(House.class);
        
        TypeSafeSubQuery<Style> houseSQ = query.subquery(Style.class);
        House houseSQV = houseSQ.from(House.class);
        houseSQ.select(houseSQV.getStyle());

        query.where(house.getStyle()).eq(houseSQ);
        validate(" from House hobj1 where hobj1.style = (select hobj2.style from House hobj2)");
    }

    /**
     * 
     */
    @Test
    public void whereExists() {
        House house = query.from(House.class);
        
        TypeSafeSubQuery<Style> houseSQ = query.subquery(Style.class);
        House houseSQV = houseSQ.from(House.class);
        houseSQ.where(houseSQV.getName()).eq(house.getName()).
                  and(houseSQV.getId()).not(house.getId());
        
        query.whereExists(houseSQ);

        validate(" from House hobj1 where exists ( from House hobj2 where hobj2.name = hobj1.name and hobj2.id <> hobj1.id)");
    }

    @Test
    public void whereGroupMultiOrTest() {
        House house = query.from(House.class);
        
        RestrictionsGroup group = query.whereGroup();
        group.where(house.getStyle()).eq(Style.the1980s).
                 or(house.getFloors()).gt(2);
        
        query.where(group).and(house.getName()).startsWith("Castle");
        
        validate(" from House hobj1 where (hobj1.style = ? or hobj1.floors > ?) and hobj1.name like ?", 
                Style.the1980s, 2, "Castle%");
    }

    @Test
    public void whereGroupMultiOrTestImproved() {
        RestrictionsGroupFactory factory = query.getRestrictionsGroupFactory();
        House house = query.from(House.class);
        
        query.and(
            factory.or(
                factory.where(house.getStyle()).eq(Style.the1980s),
                factory.where(house.getFloors()).gt(2)),
            factory.where(house.getName()).startsWith("Castle"));

        validate(" from House hobj1 where (hobj1.style = ? or hobj1.floors > ?) and hobj1.name like ?", 
                Style.the1980s, 2, "Castle%");
    }
    
}
