package be.shad.tsqb.test.restrictions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.DirectTypeSafeValue;

public class OnGoingDateRestrictionTest extends TypeSafeQueryTest {

    @Test
    public void testBefore() {
        Date date = new Date();
        TypeSafeRootQuery query = createQuery();
        Building building = query.from(Building.class);
        
        query.where(building.getConstructionDate()).before(date);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Building hobj1 where hobj1.constructionDate < ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(date));
    }

    @Test
    public void testTypeSafeValueBefore() {
        Date date = new Date();
        TypeSafeRootQuery query = createQuery();
        Building building = query.from(Building.class);
        
        query.where(building.getConstructionDate()).before(new DirectTypeSafeValue<Date>(query, date));

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Building hobj1 where hobj1.constructionDate < ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(date));
    }

    @Test
    public void testAfter() {
        Date date = new Date();
        TypeSafeRootQuery query = createQuery();
        Building building = query.from(Building.class);
        
        query.where(building.getConstructionDate()).after(date);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Building hobj1 where hobj1.constructionDate > ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(date));
    }

    @Test
    public void testTypeSafeValueAfter() {
        Date date = new Date();
        TypeSafeRootQuery query = createQuery();
        Building building = query.from(Building.class);
        
        query.where(building.getConstructionDate()).after(new DirectTypeSafeValue<Date>(query, date));

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Building hobj1 where hobj1.constructionDate > ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(date));
    }

    @Test
    public void testNotAfter() {
        Date date = new Date();
        TypeSafeRootQuery query = createQuery();
        Building building = query.from(Building.class);
        
        query.where(building.getConstructionDate()).notAfter(date);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Building hobj1 where hobj1.constructionDate <= ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(date));
    }

    @Test
    public void testTypeSafeValueNotAfter() {
        Date date = new Date();
        TypeSafeRootQuery query = createQuery();
        Building building = query.from(Building.class);
        
        query.where(building.getConstructionDate()).notAfter(new DirectTypeSafeValue<Date>(query, date));

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Building hobj1 where hobj1.constructionDate <= ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(date));
    }

    @Test
    public void testNotBefore() {
        Date date = new Date();
        TypeSafeRootQuery query = createQuery();
        Building building = query.from(Building.class);
        
        query.where(building.getConstructionDate()).notBefore(date);

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Building hobj1 where hobj1.constructionDate >= ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(date));
    }

    @Test
    public void testTypeSafeValueNotBefore() {
        Date date = new Date();
        TypeSafeRootQuery query = createQuery();
        Building building = query.from(Building.class);
        
        query.where(building.getConstructionDate()).notBefore(new DirectTypeSafeValue<Date>(query, date));

        HqlQuery hql = doQuery(query);
        assertTrue(hql.getHql().equals(" from Building hobj1 where hobj1.constructionDate >= ?"));
        assertTrue(Arrays.asList(hql.getParams()).contains(date));
    }

}