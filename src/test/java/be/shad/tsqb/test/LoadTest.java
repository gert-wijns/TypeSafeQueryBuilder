package be.shad.tsqb.test;

import java.util.ArrayList;
import java.util.Collection;

import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.dto.LoadTestDto;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeSubQuery;

public class LoadTest extends TypeSafeQueryTest {
    
    public static final void main(String[] argv) {
        LoadTest loadTest = new LoadTest();
        loadTest.initialize();
        loadTest.loadTest();
    }

    public void loadTest() {
        Collection<String> names = new ArrayList<>();
        for(int i=0; i < 500; i++) {
            names.add("name" + i);
        }
        
        long time = System.currentTimeMillis();
        HqlQuery last = null;
        for(int i=0; i < 100000; i++) {
            TypeSafeRootQuery query = createQuery();
            Town town = query.from(Town.class);
            Person inhabitant = query.join(town.getInhabitants());
            Relation childRelation = query.join(inhabitant.getChildRelations());
            
            query.where(childRelation.getChild().getTown().getName()).eq(town.getName());
            query.groupBy(town.getName());
            
            TypeSafeSubQuery<Long> subquery = query.subquery(long.class);
            Person personCnt = subquery.from(Person.class);
            subquery.where(personCnt.getAge()).gte(50).
                       and(personCnt.getName()).in(names);
            subquery.select(query.function().count().select());
            
            LoadTestDto dto = query.select(LoadTestDto.class);
            dto.setTownName(town.getName());
            dto.setMaxAge(query.function().max(childRelation.getChild().getAge()).select());
            dto.setFiftyPlusCount(subquery.select());
            
            last = query.toHqlQuery();
        }
        time = (System.currentTimeMillis() - time);
        logger.debug(time / 1000.0 + "ms/query\n" + last.toFormattedString());
    }
    
}
