package be.shad.tsqb.test;

import static be.shad.tsqb.restrictions.RestrictionsGroup.group;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.domain.House;
import be.shad.tsqb.domain.Town;
import be.shad.tsqb.dto.FunctionsDto;
import be.shad.tsqb.query.TypeSafeSubQuery;
import be.shad.tsqb.values.CaseTypeSafeValue;


public class SelectTests extends TypeSafeQueryTest {

    /**
     * Select from without a select statement
     */
    @Test
    public void selectEntity() {
        query.from(House.class);
        validate(" from House hobj1");
    }

    /**
     * Select a property of the from entity.
     */
    @Test
    public void selectProperty() {
        House house = query.from(House.class);
        
        House result = query.select(House.class);
        result.setFloors(house.getFloors());
        
        validate("select hobj1.floors as floors from House hobj1");
    }

    /**
     * Select an enum property of the from entity.
     */
    @Test
    public void selectEnumProperty() {
        House house = query.from(House.class);
        
        House result = query.select(House.class);
        result.setStyle(house.getStyle());
        
        validate("select hobj1.style as style from House hobj1");
    }

    /**
     * Select the entity and a property of the entity
     */
    @Test
    public void selectEntityAndPropertyOfEntity() {
        House house = query.from(House.class);

        @SuppressWarnings("unchecked")
        MutablePair<House, Integer> result = query.select(MutablePair.class);
        result.setLeft(house);
        result.setRight(house.getFloors());
        
        validate("select hobj1 as left, hobj1.floors as right from House hobj1");
    }

    /**
     * Select a joined entity
     */
    @Test
    public void selectJoinedEntity() {
        Town town = query.from(Town.class);
        Building building = query.join(town.getBuildings());
        
        query.selectValue(building);
        validate("select hobj2 from Town hobj1 join hobj1.buildings hobj2");
    }

    /**
     * Noone would ever select this... but, 
     * check if from 2 entities selects properly:
     */
    @Test
    public void selectDoubleJoinedEntityValue() {
        House house1 = query.from(House.class);
        House house2 = query.from(House.class);

        House select = query.select(House.class);
        select.setFloors(house1.getFloors());
        select.setStyle(house2.getStyle());
        
        validate("select hobj1.floors as floors, hobj2.style as style from House hobj1, House hobj2");
    }
    
    /**
     * Select a value of a joined entity
     */
    @Test
    public void selectJoinedEntityProperty() {
        Town town = query.from(Town.class);
        Building building = query.join(town.getBuildings());
        
        query.selectValue(building.getId());

        validate("select hobj2.id from Town hobj1 join hobj1.buildings hobj2");
    }

    @Test
    public void selectSubQueryValue() {
        House house = query.from(House.class);
        
        TypeSafeSubQuery<String> nameSubQuery = query.subquery(String.class);
        House houseSub = nameSubQuery.from(House.class);
        nameSubQuery.where(house.getId()).eq(houseSub.getId());
        
        nameSubQuery.select(houseSub.getName());

        query.selectValue(nameSubQuery);
        validate("select (select hobj2.name from House hobj2 where hobj1.id = hobj2.id) from House hobj1");
    }

    @Test
    public void selectSubQueryValueAndProperty() {
        House house = query.from(House.class);
        
        TypeSafeSubQuery<String> nameSubQuery = query.subquery(String.class);
        House houseSub = nameSubQuery.from(House.class);
        nameSubQuery.where(house.getId()).eq(houseSub.getId());
        
        nameSubQuery.select(houseSub.getName());

        @SuppressWarnings("unchecked")
        MutablePair<Integer, String> result = query.select(MutablePair.class);
        result.setLeft(house.getFloors());
        result.setRight(nameSubQuery.select());

        validate("select hobj1.floors as left, (select hobj2.name from House hobj2 where hobj1.id = hobj2.id) as right from House hobj1");
    }

    /**
     * Selecting into a primitive setter should not fail.
     */
    @Test
    public void selectPrimitiveSubQueryValue() {
        House house = query.from(House.class);
        
        TypeSafeSubQuery<Integer> nameSubQuery = query.subquery(Integer.class);
        House houseSub = nameSubQuery.from(House.class);
        nameSubQuery.where(house.getId()).eq(houseSub.getId());
        
        nameSubQuery.select(houseSub.getFloors());
        
        House houseResult = query.select(House.class);
        houseResult.setFloors(nameSubQuery.select());
        
        validate("select (select hobj2.floors from House hobj2 where hobj1.id = hobj2.id) as floors from House hobj1");
    }

    /**
     * Test max function
     */
    @Test
    public void selectMax() {
        House house = query.from(House.class);

        House houseResult = query.select(House.class);
        houseResult.setFloors(query.function().max(house.getFloors()).select());

        validate("select max(hobj1.floors) as floors from House hobj1");
        
    }

    /**
     * Test count function
     */
    @Test
    @SuppressWarnings("unused")
    public void selectCount() {
        House house = query.from(House.class);

        FunctionsDto dto = query.select(FunctionsDto.class);
        dto.setTestCount(query.function().count().select());

        validate("select count(*) as testCount from House hobj1");
    }

    @Test
    public void selectCaseWhenValue() throws ParseException {
        House house = query.from(House.class);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // seems to be the format of hqldb:mem
        String dateString = "1500-02-08 12:00:00";
        Date date = df.parse(dateString);
        CaseTypeSafeValue<String> value = new CaseTypeSafeValue<String>(query, String.class);
        value.when(group(query).and(house.getFloors()).gt(40)).then("Test1");
        value.when(group(query).and(house.getName()).startsWith("Castle")).then((String) null);
        value.when(group(query).and(house.getConstructionDate()).before(date)).then("Old");
        value.otherwise(house.getName());

        @SuppressWarnings("unchecked")
        MutablePair<String, Object> pair = query.select(MutablePair.class);
        pair.setLeft(value.select());

        validate("select ("
                + "case when (hobj1.floors > 40) "
                + "then 'Test1' "
                + "when (hobj1.name like 'Castle%') "
                + "then null "
                + "when (hobj1.constructionDate < '" + dateString + "') "
                + "then 'Old' "
                + "else hobj1.name end) as left "
                + "from House hobj1");
    }
}
