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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.hibernate.Query;
import org.junit.Test;

import be.shad.tsqb.domain.Apartment;
import be.shad.tsqb.domain.Building;
import be.shad.tsqb.domain.House;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.query.JoinType;
import be.shad.tsqb.restrictions.RestrictionsGroupFactory;

public class GetSuperTypeAsSubtypeAndBuildQueryTest extends TypeSafeQueryTest {

    /**
     * Assuming the property path doesn't need to be on the super type
     * if it is on one of the subtypes of a hibernate types 
     * with an @Inheritance annotation.
     * <p>
     * If this doesn't hold true, then creating this query will surely fail.
     */
    @Test
    public void testHibernateAssumption() {
        Query query = getSessionFactory().getCurrentSession().
                createQuery("from Building b where b.revenue > 10.0");
        query.list();
    }

    @Test
    public void testGetInheritanceTypeAsSubtypeFrom() {
        BigDecimal argument = new BigDecimal(10.0);
        Building buildingProxy = query.from(Building.class);
        
        Apartment apartmentProxy = query.getAsSubtype(buildingProxy, Apartment.class);
        query.where(apartmentProxy.getRevenue()).gt(argument);
        
        validate(" from Building hobj1 where hobj1.revenue > :np1", argument);
    }
    
    @Test
    public void testGetInheritanceTypeAsSubtypeJoined() {
        BigDecimal revenue = new BigDecimal(10.0);
        TestDataCreator creator = new TestDataCreator(getSessionFactory());
        Town town = creator.createTestTown();
        House house1 = creator.createTestHouse(town, "Housy", 10);
        House house2 = creator.createTestHouse(town, "Housy", 4);
        Apartment apartment = creator.createTestApartment(town, revenue.add(new BigDecimal(1.0)));
        
        RestrictionsGroupFactory res = query.factories().getRestrictionsGroupFactory();
        Town townProxy = query.from(Town.class);
        Building buildingProxy = query.join(townProxy.getBuildings(), JoinType.Left);

        Apartment apartmentProxy = query.getAsSubtype(buildingProxy, Apartment.class);
        House houseProxy = query.getAsSubtype(buildingProxy, House.class);
        query.or(
            res.where(apartmentProxy.getRevenue()).gt(revenue),
            res.where(houseProxy.getFloors()).gt(5));
        query.selectValue(query.function().distinct(buildingProxy));
        
        validate("select distinct hobj2 from Town hobj1 "
                + "left join hobj1.buildings hobj2 "
                + "where hobj2.revenue > :np1 or hobj2.floors > :np2", revenue, 5);
        
        assertEquals("Exected the first house and the apartment", 2, doQueryResult.size());
        assertTrue("Expected house1 in the result.", doQueryResult.contains(house1));
        assertFalse("Didn't expect house2 in the result.", doQueryResult.contains(house2));
        assertTrue("Expected apartment in the result.", doQueryResult.contains(apartment));
    }
    
}
