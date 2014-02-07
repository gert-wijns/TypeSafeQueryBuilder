package be.shad.tsqb.test;

import org.junit.Test;

import be.shad.tsqb.domain.Building;

public class OrderingTest extends TypeSafeQueryTest {

    @Test
    public void testOrderByDesc() {
        Building building = query.from(Building.class);
        query.orderBy().desc(building.getConstructionDate());

        validate(" from Building hobj1 order by hobj1.constructionDate desc");
    }

    @Test
    public void testOrderByAsc() {
        Building building = query.from(Building.class);
        query.orderBy().asc(building.getConstructionDate());

        validate(" from Building hobj1 order by hobj1.constructionDate");
    }

    @Test
    public void testOrderByMultiple() {
        Building building = query.from(Building.class);
        query.orderBy().asc(building.getConstructionDate());
        query.orderBy().desc(building.getStyle());

        validate(" from Building hobj1 order by hobj1.constructionDate, hobj1.style desc");
    }

}
