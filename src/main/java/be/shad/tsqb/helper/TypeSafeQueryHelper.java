package be.shad.tsqb.helper;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.query.TypeSafeRootQueryInternal;
import be.shad.tsqb.values.HqlQueryValue;

public interface TypeSafeQueryHelper {
    
    /**
     * Creates a fresh query instance. This is the starting point to create a new query.
     */
    TypeSafeRootQuery createQuery();
    
    /**
     * Retrieves the entity name from hibernate. Used to construct the from clause.
     */
    String getEntityName(Class<?> entityClass);

    /**
     * Uses the type safe query factory and adds method handling to delegate
     * calls to the given query.
     */
    <T> T createTypeSafeFromProxy(TypeSafeQueryInternal query, Class<T> clazz);
    
    /**
     * Uses the type safe query factory and adds method handling to delegate
     * calls to the given query.
     */
    <T> T createTypeSafeSelectProxy(TypeSafeRootQueryInternal query, Class<T> clazz);

    /**
     * Creates a proxy, adds it to the query' dataTree and sets its method listener.
     */
    TypeSafeQueryProxyData createTypeSafeJoinProxy(TypeSafeQueryInternal query, 
            TypeSafeQueryProxyData parent, String propertyName, Class<?> targetClass);
    
    /**
     * Replaces the '?'s with 'valueToLiteral's.
     */
    HqlQueryValue replaceParamsWithLiterals(HqlQueryValue value);
    
}
