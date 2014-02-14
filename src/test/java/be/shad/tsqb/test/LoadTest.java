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

import java.util.ArrayList;
import java.util.Collection;

import be.shad.tsqb.domain.Town;
import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.domain.people.Relation;
import be.shad.tsqb.dto.LoadTestDto;
import be.shad.tsqb.hql.HqlQuery;
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

        int n = 100000;
        long time = System.currentTimeMillis();
        HqlQuery last = null;
        for(int i=0; i < n; i++) {
            query = createQuery();
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
        logger.debug(time / (double) n + "ms/query\n" + last.toFormattedString());
    }
    
}
