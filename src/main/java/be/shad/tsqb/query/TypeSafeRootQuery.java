package be.shad.tsqb.query;

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

}
