package be.shad.tsqb.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.hql.HqlQuery;

public class GroupingTest extends TypeSafeQueryTest {

    @Test
    public void testGroupBySingleValue() {
        Building building = query.from(Building.class);
        query.selectValue(building.getConstructionDate());
        query.groupBy(building.getConstructionDate());

        HqlQuery hql = doQuery(query);
        assertTrue("group by date", hql.getHql().equals("select hobj1.constructionDate from Building hobj1 group by hobj1.constructionDate"));
    }

    @Test
    public void testGroupByMoreThanOneValue() {
        Building building = query.from(Building.class);
        query.selectValue(building.getConstructionDate());
        query.selectValue(building.getStyle());
        query.groupBy(building.getConstructionDate()).
              and(building.getStyle());

        HqlQuery hql = doQuery(query);
        assertTrue("group by date and style", hql.getHql().equals(
                "select hobj1.constructionDate, hobj1.style "
                + "from Building hobj1 "
                + "group by hobj1.constructionDate, hobj1.style"));
    }
    
}
