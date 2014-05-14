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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.junit.Test;

import be.shad.tsqb.domain.Product;
import be.shad.tsqb.domain.properties.ManyProperties;
import be.shad.tsqb.dto.StringToPlanningPropertiesTransformer;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.selection.SelectionValueTransformer;
import be.shad.tsqb.selection.TypeSafeQueryProjections;
import be.shad.tsqb.selection.TypeSafeQueryResultTransformer;
import be.shad.tsqb.selection.TypeSafeValueProjection;
import be.shad.tsqb.values.CustomTypeSafeValue;
import be.shad.tsqb.values.TypeSafeValue;

public class TestResultTransformer {
    protected final Logger logger = LogManager.getLogger(getClass());

    /**
     * 
     */
    @Test
    public void testResultTransformerSetsNestedFields() {
        //String hql = "select hobj1.name as name, hobj1.name as properties_planning_algorithm from Product hobj1";
        List<String[]> paths = new ArrayList<>();
        paths.add(new String[] { "name" });
        paths.add(new String[] { "properties", "planning", "algorithm" });
        List<SelectionValueTransformer<?, ?>> transformers = new ArrayList<>();
        transformers.add(null);
        transformers.add(null);
        
        TypeSafeQueryResultTransformer tf = new TypeSafeQueryResultTransformer(Product.class, paths, transformers);
        Object transformTuple = tf.transformTuple(new Object[] {"Name", "Algo"}, new String[paths.size()]);
        
        assertTrue(transformTuple instanceof Product);
        Product product = (Product) transformTuple;
        assertEquals("Name", product.getName());
        assertEquals("Algo", product.getProperties().getPlanning().getAlgorithm());
    }

    @Test
    public void testResultTransformerSetsConvertedFields() {
        //String hql = "select hobj1.name as name, hobj1.name as properties_planning from Product hobj1";
        List<String[]> paths = new ArrayList<>();
        paths.add(new String[] { "name" });
        paths.add(new String[] { "properties", "planning" });
        List<SelectionValueTransformer<?, ?>> transformers = new ArrayList<>();
        transformers.add(null);
        transformers.add(new StringToPlanningPropertiesTransformer());

        TypeSafeQueryResultTransformer tf = new TypeSafeQueryResultTransformer(Product.class, paths, transformers);
        Object transformTuple = tf.transformTuple(new Object[] {"Name", "Algo"}, new String[paths.size()]);
        
        assertTrue(transformTuple instanceof Product);
        Product product = (Product) transformTuple;
        assertEquals("Name", product.getName());
        assertEquals("Algo", product.getProperties().getPlanning().getAlgorithm());
    }

    @Test
    public void resultTransformerLoadTest() {
        List<String> aliases = new ArrayList<>();
        List<String> aliasToBeanResultAliases = new ArrayList<>();
        TypeSafeValue<String> dummy = new CustomTypeSafeValue<>(null, String.class, "", null);
        TypeSafeQueryProjections projections = new TypeSafeQueryProjections(null);
        for(int i=1; i < 80; i++) {
            projections.addProjection(new TypeSafeValueProjection(dummy, "manyProperties.property" + i, null));
            aliases.add("manyProperties_property" + i);
            aliasToBeanResultAliases.add("property" + i);
        }

        String[] aliasToBeanResultAliasesArray = aliasToBeanResultAliases.toArray(new String[0]);
        String[] aliasesArray = aliases.toArray(new String[0]);
        Object[] valuesArray = new Object[aliasesArray.length];
        
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
            HqlQuery query = new HqlQuery();
            projections.setResultClass(Product.class);
            projections.appendTo(query);
            for(int j=0; j < inner; j++) {
                query.getResultTransformer().transformTuple(valuesArray, aliasesArray);
            }
        }
        time = (System.currentTimeMillis() - time);
        logger.debug((time / (double) (outer*inner)) + "ms/transform");
    }
    
}
