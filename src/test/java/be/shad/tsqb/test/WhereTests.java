package be.shad.tsqb.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import be.shad.tsqb.domain.House;
import be.shad.tsqb.domain.Style;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeSubQuery;

public class WhereTests extends TypeSafeQueryTest {

    /**
     * Where left is a reference and right is a value
     */
    @Test
    public void whereEnumValueEquals() {
        TypeSafeRootQuery query = createQuery();
        House house = query.from(House.class);
        
        query.where(house.getStyle()).eq(Style.the1980s);
        
        HqlQuery hql = doQuery(query);
        assertTrue("style where clause", hql.getWhere().contains("hobj1.style = ?"));
        assertTrue("filtering on 1980s", Arrays.asList(hql.getParams()).contains(Style.the1980s));
    }

    /**
     * Where left and right are both references
     */
    @Test
    public void whereByReference() {
        TypeSafeRootQuery query = createQuery();
        House house1 = query.from(House.class);
        House house2 = query.from(House.class);
        
        query.where(house1.getStyle()).eq(house2.getStyle());

        HqlQuery hql = doQuery(query);
        assertTrue("where house1.style == house2.style", hql.getWhere().contains("hobj1.style = hobj2.style"));
    }

    /**
     * Where left is a reference and right is a subquery value
     */
    @Test
    public void whereBySubquery() {
        TypeSafeRootQuery query = createQuery();
        House house = query.from(House.class);
        
        TypeSafeSubQuery<Style> houseSQ = query.subquery(Style.class);
        House houseSQV = houseSQ.from(House.class);
        houseSQ.select(houseSQV.getStyle());

        query.where(house.getStyle()).eq(houseSQ);

        HqlQuery hql = doQuery(query);
        assertTrue("where house1.style == house2.style", hql.getWhere().contains("hobj1.style = (select hobj2.style from"));
    }
    
    /**
     * 
     */
    @Test
    public void whereReferencedIsNull() {
        TypeSafeRootQuery query = createQuery();
        House house = query.from(House.class);
        
        query.where(house.getStyle()).isNull();

        HqlQuery hql = doQuery(query);
        assertTrue("style is null check", hql.getWhere().contains("hobj1.style is null"));
    }

    /**
     * 
     */
    @Test
    public void whereReferencedIsFalse() {
        TypeSafeRootQuery query = createQuery();
        House house = query.from(House.class);
        
        query.where(house.isOccupied()).isFalse();

        HqlQuery hql = doQuery(query);
        assertTrue("occupied false", hql.getWhere().contains("hobj1.occupied = ?"));
        assertTrue("filtering false", Arrays.asList(hql.getParams()).contains(Boolean.FALSE));
    }

    /**
     * 
     */
    @Test
    public void whereReferencedDateAfter() {
        TypeSafeRootQuery query = createQuery();
        House house = query.from(House.class);
        
        Date yearBeforeNow = new Date(System.currentTimeMillis() - 31556952000L);
        query.where(house.getConstructionDate()).after(yearBeforeNow);
        
        HqlQuery hql = doQuery(query);
        assertTrue("date aftere", hql.getWhere().contains("hobj1.constructionDate > ?"));
        assertTrue("filtering date", Arrays.asList(hql.getParams()).contains(yearBeforeNow));
    }
}
