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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.restrictions.Restriction;
import be.shad.tsqb.selection.SelectionValueTransformer;
import be.shad.tsqb.selection.collection.ResultIdentifierBinder;
import be.shad.tsqb.selection.parallel.MapSelectionMerger;
import be.shad.tsqb.selection.parallel.SelectPair;
import be.shad.tsqb.selection.parallel.SelectTriplet;
import be.shad.tsqb.selection.parallel.SelectValue;
import be.shad.tsqb.selection.parallel.SelectionMerger;
import be.shad.tsqb.selection.parallel.SelectionMerger1;
import be.shad.tsqb.selection.parallel.SelectionMerger2;
import be.shad.tsqb.selection.parallel.SelectionMerger3;
import be.shad.tsqb.values.HqlQueryBuilderParams;
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
     * Delegates to toHqlQuery with default HqlQueryBuilderParams.
     */
    HqlQuery toHqlQuery();

    /**
     * Converts this query to an hqlQuery.
     * <p>
     * The hqlQuery can be used to get the hql and the
     * params to create a hibernate query object.
     * <p>
     * Query builder params affect the resulting hql.
     */
    HqlQuery toHqlQuery(HqlQueryBuilderParams params);

    <T> T groupSelectBy(T invocation);

    /**
     * Can be used when:
     * <ul>
     *     <li>not selecting into a result type</li>
     *     <li>when selecting a single value in a subquery</li>
     *     <li>with a selection proxy (marking it as the main result)</li>
     * </ul>
     */
    <T> void selectValue(T value);

    /**
     * Delegates to {@link #select(Class, ResultIdentifierBinder)} with null.
     */
    <T> T select(Class<T> resultClass);

    /**
     * Determines the resultclass by invoking the supplier to generate an new instance
     * and then taking that objects class.
     *
     * Delegates to {@link #select(Class, ResultIdentifierBinder)}.
     */
    <B> B select(Supplier<B> selectionBuilderSupplier);

    /**
     * Determines the resultclass by invoking the supplier to generate an new instance
     * and then taking that objects class.
     *
     * Delegates to {@link #select(Class, ResultIdentifierBinder)}.
     */
    <T> T subBuilder(Supplier<T> selectionBuilderSupplier);

    /**
     * Delegates to {@link #subCollectionBuilder(Supplier, Consumer, Supplier)} with HashSet::new
     */
    <T, V> T subSetBuilder(Supplier<T> selectionBuilderSupplier, Consumer<Set<V>> collectionSetter);

    /**
     * Delegates to {@link #subCollectionBuilder(Supplier, Consumer, Supplier)} with ArrayList::new
     */
    <T, V> T subListBuilder(Supplier<T> selectionBuilderSupplier, Consumer<List<V>> collectionSetter);

    /**
     * Given a builder and a collection, returns a new builder to select values of a dto which will be put in the collection.
     * The collection will be created using the given {@code collectionProvider}?
     *
     * @param selectionBuilderSupplier a supplier method to create a new selection builder
     *                                 (example: SelectValue::builder, or SelectDto::new)
     *
     * @param collectionSetter a setter method reference on a select proxy
     *                         (example: selectDto::setProperties),
     *                         the collection will be set to the given setter during result transformation.
     *
     * @param collectionProvider a supplier to create the needed collection during result transformation
     *                           (example: ArrayList::new)
     */
    <T, V, C extends Collection<V>> T subCollectionBuilder(Supplier<T> selectionBuilderSupplier,
                                                           Consumer<C> collectionSetter,
                                                           Supplier<C> collectionProvider);

    /**
     * Creates a new builder which is used to select values into a sub selection dto.
     * The sub selection dto must still be set onto the main selection dto in order to appear in the result.
     */
    <T> T subBuilder(Class<T> selectionBuilderClass);

    /**
     * Select the restriction as a case when(restriction) then true else false.
     */
    Boolean selectBoolean(Restriction restriction);

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
     *
     * @param resultIdentifierBinder identifier binder in case a collection of this dto will be subselected
     *                               In the usual case, it is most convenient to just subclass IdentityFieldProvider.
     */
    <ID, T extends ID> T select(Class<T> resultClass, ResultIdentifierBinder<ID> resultIdentifierBinder);

    /**
     * Subselect dtos for <code>collectionItemClass</code> into the collection property of a result dto.
     * <p>
     * The owner of the collection must be a select proxy. If a resultIdentifierProvider was specified for the
     * owner of the collection, then that one will be used to decide where to add the item.
     * If no resultIdentifierProvider was set, then all properties of the owner are checked for equality.
     *
     * @param resultIdentifierBinder identifier binder in case a collection of this dto will be subselected
     *                               In the usual case, it is most convenient to just subclass IdentityFieldProvider.
     */
    <ID, T extends ID> T select(List<T> collection, Class<T> collectionItemClass, ResultIdentifierBinder<ID> resultIdentifierBinder);

    /**
     * Subselect dtos for <code>collectionItemClass</code> into the collection property of a result dto.
     * <p>
     * The owner of the collection must be a select proxy. If a resultIdentifierProvider was specified for the
     * owner of the collection, then that one will be used to decide where to add the item.
     * If no resultIdentifierProvider was set, then all properties of the owner are checked for equality.
     *
     * @param resultIdentifierBinder identifier binder in case a collection of this dto will be subselected
     *                               In the usual case, it is most convenient to just subclass IdentityFieldProvider.
     */
    <ID, T extends ID> T select(Set<T> collection, Class<T> collectionItemClass, ResultIdentifierBinder<ID> resultIdentifierBinder);

    /**
     * Subselect dtos for <code>collectionItemClass</code> into the collection property of a result dto.
     * <p>
     * The owner of the collection must be a select proxy. If a resultIdentifierProvider was specified for the
     * owner of the collection, then that one will be used to decide where to add the item.
     * If no resultIdentifierProvider was set, then all properties of the owner are checked for equality.
     *
     * @param resultIdentifierBinder identifier binder in case a collection of this dto will be subselected
     *                               In the usual case, it is most convenient to just subclass IdentityFieldProvider.
     */
    <ID, T extends ID> T select(Collection<T> collection, Class<T> subselectClass, ResultIdentifierBinder<ID> resultIdentifierBinder);

    /**
     * Create an additional proxy to select into, which is merged with the result dto during result transforming.
     */
    <T, SUB> SUB selectMergeValues(T resultDto, Class<SUB> subselectClass, SelectionMerger<T, SUB> merger);

    /**
     * Convenience method to select a multiple values into a map which can be used to set some values on the resultDto
     * <p>
     * For more values or stricter naming, use {@link #selectMergeValues(Object, Class, SelectionMerger)} with a dtoClass.
     */
    <T, K, V> Map<K, V> selectMergeValues(T resultDto, MapSelectionMerger<T, K, V> merger);

    /**
     * Convenience method to select a single value which can be used to set some value on the resultDto
     * This is basically the same as {@link #select(Class, Object, SelectionValueTransformer)}.
     * <p>
     * For more values or stricter naming, use {@link #selectMergeValues(Object, Class, SelectionMerger)} with a dtoClass.
     */
    <T, A> SelectValue<A> selectMergeValues(T resultDto, SelectionMerger1<T, A> merger);

    /**
     * Convenience method to select two values which can be used to set values on the result dto.
     * <p>
     * For more values or stricter naming, use {@link #selectMergeValues(Object, Class, SelectionMerger)} with a dtoClass.
     */
    <T, A, B> SelectPair<A, B> selectMergeValues(T resultDto, SelectionMerger2<T, A, B> merger);

    /**
     * Convenience method to select three values which can be used to set values on the result dto.
     * <p>
     * For more values or stricter naming, use {@link #selectMergeValues(Object, Class, SelectionMerger)} with a dtoClass.
     */
    <T, A, B, C> SelectTriplet<A, B, C> selectMergeValues(T resultDto, SelectionMerger3<T, A, B, C> merger);

    /**
     * Delegates to {@link #select(Class, Object, SelectionValueTransformer)} and selects immediately.
     */
    <T, V> V select(Class<V> transformedClass, T value, SelectionValueTransformer<T, V> transformer);

    /**
     * Registers the transformer to be used for the selection value
     * when the default result transformer is used.
     */
    <T, V> TypeSafeValue<V> selectValue(Class<V> transformedClass, T value, SelectionValueTransformer<T, V> transformer);

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
     * Purpose: {@link org.hibernate.query.Query#setFirstResult}
     */
    void setFirstResult(int firstResult);

    /**
     * The first result to fetch, default is -1, see {@link org.hibernate.query.Query#setFirstResult}
     */
    int getFirstResult();

    /**
     * Purpose: {@link org.hibernate.query.Query#setMaxResults}
     */
    void setMaxResults(int maxResults);

    /**
     * The amount of results to fetch, default is -1, see {@link org.hibernate.query.Query#setMaxResults}
     */
    int getMaxResults();

    /**
     * Converts this query to an sql query with parameters filled in as literals.
     */
    String toFormattedSqlQuery();
}
