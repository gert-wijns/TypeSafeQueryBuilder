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
package be.shad.tsqb.query;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.selection.SelectionValueTransformer;
import be.shad.tsqb.selection.parallel.ParallelSelectionMerger;
import be.shad.tsqb.selection.parallel.ParallelSelectionMerger1;
import be.shad.tsqb.selection.parallel.ParallelSelectionMerger2;
import be.shad.tsqb.selection.parallel.ParallelSelectionMerger3;
import be.shad.tsqb.selection.parallel.SelectPair;
import be.shad.tsqb.selection.parallel.SelectTriplet;
import be.shad.tsqb.selection.parallel.SelectValue;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * For the general information, see {@link TypeSafeQuery}.
 * <p>
 * Example query
 * <pre>
 * TypeSafeRootQuery query = TypeSafeQueryHelper#createQuery();
 * 
 * Cat cat = query.from(Cat.class); // returns a cat proxy, which will capture the method calls on cat
 * 
 * Kitten kitten = query.join(cat.getKittens()); // returns a proxy of the collection generic type (the information 
 *                                               // is retrieved from the sessionFactory, hibernate knows the type)
 *                                               
 * query.where(cat.isFemale()).isTrue()). // only mothers are fetched
 *    and(cat.getName()).startsWith("E"); // only cats with a name starting with E are fetched
 *    
 * query.order().desc(cat.getAge()); // older cats will be earlier in the query results
 * 
 * CatDto catDto = query.select(CatDto.class); // data container for the selected values
 * catDto.setCatName(cat.getName()); // the cat's name will be selected into the catName of the catDto
 * 
 * HqlQuery hql = query.toHqlQuery(); // converts the query to an Hql query, which contains the hql string, its params and the result type.
 * 
 * ... create query with hql and query...
 * </pre>
 */
public interface TypeSafeRootQuery extends TypeSafeQuery {
    
    /**
     * Creates a copy of this query, any adjustments made to the copy
     * will (or should, because we can't control everything) 
     * not affect the original query.
     */
    TypeSafeRootQuery copy();
    
    /**
     * Converts this query to an hqlQuery. 
     * <p>
     * The hqlQuery can be used to get the hql and the 
     * params to create a hibernate query object.
     */
    HqlQuery toHqlQuery();
    
    /**
     * Can be used when not selecting into a result type,
     * or when selecting a single value in a subquery.
     * <p>
     * This is not the preferred way to select when
     * working with a root query.
     * <p>
     * The selects will not receive an alias.
     */
    void selectValue(Object value);
    
    /**
     * Create a proxy to select into. The proxy is used to generate aliases for the
     * selected values, which can then be used with an AliasToBeanResultTransformer.
     * <p>
     * Usage:
     * <pre>
     * CatDto catDto = query.select(CatDto.class); // data container for the selected values
     * catDto.setCatName(cat.getName()); // the cat's name will be selected into the catName of the catDto
     * </pre>
     * <p>
     * It can also be used to select subquery values into, example:
     * <pre>
     * TypeSafeSubQuery<Long> ageSQ = query.subquery(Long.class); // age subquery
     * CatDto catDto = query.select(CatDto.class); // data container for the selected values
     * catDto.setAge(ageSQ.getValue()); // selects the subquery's selected value into the age property of the catDto
     * </pre>
     */
    <T> T select(Class<T> resultClass);
    
    /**
     * Create a proxy to select into. This is a sub selected proxy which will be added to the result dto using the merger.
     * The merger type T should be the same as the selected resultClass.
     * <p>
     * The type S 
     */
    <T, SUB> SUB selectParallel(T resultDto, Class<SUB> subselectClass, ParallelSelectionMerger<T, SUB> merger);

    /**
     * Convenience method to select a single value which can be used to set some value on the resultDto
     * This is basically the same as {@link #select(Class, Object, SelectionValueTransformer)}.
     * <p>
     * For more values or stricter naming, use {@link #selectParallel(Object, Class, ParallelSelectionMerger)} with a dtoClass.
     */
    <T, A> SelectValue<A> selectParallel(T resultDto, ParallelSelectionMerger1<T, A> merger);
    
    /**
     * Convenience method to select two values which can be used to set values on the result dto.
     * <p>
     * For more values or stricter naming, use {@link #selectParallel(Object, Class, ParallelSelectionMerger)} with a dtoClass.
     */
    <T, A, B> SelectPair<A, B> selectParallel(T resultDto, ParallelSelectionMerger2<T, A, B> merger);
    
    /**
     * Convenience method to select three values which can be used to set values on the result dto.
     * <p>
     * For more values or stricter naming, use {@link #selectParallel(Object, Class, ParallelSelectionMerger)} with a dtoClass.
     */
    <T, A, B, C> SelectTriplet<A, B, C> selectParallel(T resultDto, ParallelSelectionMerger3<T, A, B, C> merger);

    /**
     * Registers the transformer to be used for the selection value
     * when the default result transformer is used.
     */
    <T, V> V select(Class<V> transformedClass, T value, SelectionValueTransformer<T, V> transformer);
    
    /**
     * @see #distinct(TypeSafeValue)
     */
    <VAL> VAL distinct(VAL value);

    /**
     * Shorthand to make use of the distinct function.
     * <p>
     * Calls query.function().distinct(value).select().
     */
    <VAL> VAL distinct(TypeSafeValue<VAL> value);

    /**
     * Purpose: {@link org.hibernate.Query#setFirstResult}
     */
    void setFirstResult(int firstResult);

    /**
     * The first result to fetch, default is -1, see {@link org.hibernate.Query#setFirstResult}
     */
    int getFirstResult();
    
    /**
     * Purpose: {@link org.hibernate.Query#setMaxResults}
     */
    void setMaxResults(int maxResults);

    /**
     * The amount of results to fetch, default is -1, see {@link org.hibernate.Query#setMaxResults}
     */
    int getMaxResults();
    
}
