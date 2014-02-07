package be.shad.tsqb.test.restrictions;

import java.util.Date;

import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.DirectTypeSafeValue;

public class OnGoingDateRestrictionTest extends TypeSafeQueryTest {
    private Date date = new Date();

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

}