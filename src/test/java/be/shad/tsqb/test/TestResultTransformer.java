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
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.junit.Test;

import be.shad.tsqb.domain.Product;
import be.shad.tsqb.domain.properties.ManyProperties;
import be.shad.tsqb.domain.properties.PlanningProperties;
import be.shad.tsqb.dto.StringToPlanningPropertiesTransformer;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.selection.TypeSafeQueryProjections;
import be.shad.tsqb.selection.TypeSafeQueryResultTransformer;
import be.shad.tsqb.values.HqlQueryBuilderParams;
import be.shad.tsqb.values.HqlQueryBuilderParamsImpl;

public class TestResultTransformer extends TypeSafeQueryTest {
    protected final Logger logger = LogManager.getLogger(getClass());

    private TypeSafeQueryProjections getProjections() {
        return ((TypeSafeRootQueryInternal) this.query).getProjections();
    }

    /**
     *
     */
    @Test
    public void testResultTransformerSetsNestedFields() {
        //String hql = "select hobj1.name as name, hobj1.name as properties_planning_algorithm from Product hobj1";
        List<String[]> paths = new ArrayList<>();
        paths.add(new String[] { "name" });
        paths.add(new String[] { "properties", "planning", "algorithm" });

        Product selectProxy = query.select(Product.class);
        selectProxy.setName("Name");
        selectProxy.getProperties().getPlanning().setAlgorithm("Algo");

        HqlQueryBuilderParams params = new HqlQueryBuilderParamsImpl();
        HqlQuery query = new HqlQuery();
        getProjections().appendTo(query, params);

        TypeSafeQueryResultTransformer tf = (TypeSafeQueryResultTransformer) query.getResultTransformer();
        Object transformTuple = tf.transformTuple(new Object[] {"Name", "Algo"}, new String[paths.size()]);
        List<?> transformList = tf.transformList(Arrays.asList(transformTuple));
        Object transformed = transformList.get(0);

        assertTrue(transformed instanceof Product);
        Product product = (Product) transformed;
        assertEquals("Name", product.getName());
        assertEquals("Algo", product.getProperties().getPlanning().getAlgorithm());
    }

    @Test
    public void testResultTransformerSetsConvertedFields() {
        //String hql = "select hobj1.name as name, hobj1.name as properties_planning from Product hobj1";
        Product fromProxy = query.from(Product.class);

        Product selectProxy = query.select(Product.class);
        selectProxy.setName("Name");
        selectProxy.getProperties().setPlanning(query.select(PlanningProperties.class,
                fromProxy.getProperties().getPlanning().getAlgorithm(),
                new StringToPlanningPropertiesTransformer()));

        HqlQueryBuilderParams params = new HqlQueryBuilderParamsImpl();
        HqlQuery query = new HqlQuery();
        getProjections().appendTo(query, params);

        TypeSafeQueryResultTransformer tf = (TypeSafeQueryResultTransformer) query.getResultTransformer();
        Object transformTuple = tf.transformTuple(new Object[] {"Name", "Algo"}, new String[2]);
        List<?> transformList = tf.transformList(Arrays.asList(transformTuple));
        Object transformed = transformList.get(0);

        assertTrue(transformed instanceof Product);
        Product product = (Product) transformed;
        assertEquals("Name", product.getName());
        assertEquals("Algo", product.getProperties().getPlanning().getAlgorithm());
    }

    @Test
    public void resultTransformerLoadTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Product product = query.select(Product.class);
        ManyProperties manyProperties = product.getManyProperties();

        List<String> aliases = new ArrayList<>();
        List<String> aliasToBeanResultAliases = new ArrayList<>();
        for(int i=1; i < 80; i++) {
            manyProperties.getClass().getMethod("setProperty"+i, String.class).invoke(manyProperties, "");
            aliases.add("manyProperties_property" + i);
            aliasToBeanResultAliases.add("property" + i);
        }

        String[] aliasToBeanResultAliasesArray = aliasToBeanResultAliases.toArray(new String[0]);
        String[] aliasesArray = aliases.toArray(new String[0]);
        Object[] valuesArray = new Object[aliasesArray.length];
        for(int i=0; i < aliasesArray.length; i++) {
            valuesArray[i] = "value"+i;
        }

        int outer = 5;
        int inner = 100000;
        long time = System.currentTimeMillis();
        for(int i=0; i < outer; i++) {
            AliasToBeanResultTransformer tf = new AliasToBeanResultTransformer(ManyProperties.class);
            for(int j=0; j < inner; j++) {
                tf.transformTuple(valuesArray, aliasToBeanResultAliasesArray);
            }
        }
        time = (System.currentTimeMillis() - time);
        logger.debug((time / (double) (outer*inner)) + "ms/transform (original)");

        time = System.currentTimeMillis();
        for(int i=0; i < outer; i++) {
            HqlQueryBuilderParams params = new HqlQueryBuilderParamsImpl();
            HqlQuery query = new HqlQuery();
            getProjections().appendTo(query, params);
            for(int j=0; j < inner; j++) {
                query.getResultTransformer().transformTuple(valuesArray, aliasesArray);
            }
        }
        time = (System.currentTimeMillis() - time);
        logger.debug((time / (double) (outer*inner)) + "ms/transform");
    }

}
