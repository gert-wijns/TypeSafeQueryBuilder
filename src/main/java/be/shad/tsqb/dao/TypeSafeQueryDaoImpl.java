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
package be.shad.tsqb.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import be.shad.tsqb.NamedParameter;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;

public class TypeSafeQueryDaoImpl implements TypeSafeQueryDao {
    private final SessionFactory sessionFactory;
    
    public TypeSafeQueryDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> doQuery(TypeSafeRootQuery tsqbQuery) {
        HqlQuery hqlQuery = tsqbQuery.toHqlQuery();
        
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery(hqlQuery.getHql());
        int position = 0;
        for(Object param: hqlQuery.getParams()) {
            if (param instanceof NamedParameter) {
                NamedParameter named = (NamedParameter) param;
                if (named.getValue() instanceof Collection<?>) {
                    query.setParameterList(named.getName(), (Collection<?>) named.getValue());
                } else {
                    query.setParameter(named.getName(), named.getValue());
                }
            } else {
                query.setParameter(position++, param);
            }
        }
        if (tsqbQuery.getFirstResult() >= 0) {
            query.setFirstResult(tsqbQuery.getFirstResult());
        }
        if (tsqbQuery.getFirstResult() > 0) {
            query.setMaxResults(tsqbQuery.getMaxResults());
        }
        query.setResultTransformer(hqlQuery.getResultTransformer());
        
        return query.list();
    }

}
