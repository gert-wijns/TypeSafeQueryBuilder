package be.shad.tsqb.test;

import org.junit.Test;

import be.shad.tsqb.domain.Building;

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
        query.selectValue(building.getConstructionDate());
        query.selectValue(building.getStyle());
        query.groupBy(building.getConstructionDate()).
              and(building.getStyle());

        validate("select hobj1.constructionDate, hobj1.style "
                + "from Building hobj1 "
                + "group by hobj1.constructionDate, hobj1.style");
    }
    
}
