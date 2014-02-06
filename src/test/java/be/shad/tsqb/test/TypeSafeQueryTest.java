package be.shad.tsqb.test;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import be.shad.tsqb.helper.TypeSafeQueryHelperImpl;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;

public class TypeSafeQueryTest {
    
    private final Logger logger = LogManager.getLogger(getClass());
    @Rule public TestName name = new TestName();
    
    private SessionFactory sessionFactory;
    private TypeSafeQueryHelperImpl helper;

    /**
     * Initialize the sessionFactory and helper.
     * The helper has an override to generate shorter entity names 
     * for readability (and it also works in hibernate...)
     */
    @Before
    public void initialize() {
        Configuration config = new Configuration();
        config.configure("be/shad/tsqb/tests/hibernate.cfg.xml");
        sessionFactory = config.buildSessionFactory();
        helper = new TypeSafeQueryHelperImpl(sessionFactory) {
            // trim package for readability:
            @Override
            public String getEntityName(Class<?> entityClass) {
                String entityName = super.getEntityName(entityClass);
                return entityName.substring(entityName.lastIndexOf(".")+1);
            }
        };
    }

    protected TypeSafeRootQuery createQuery() {
        return helper.createQuery();
    }
    
    /**
     * Creates a query using a session calls list, hibernate should 
     * not complain if the query is syntactically correct.
     * <p>
     * The hql query is returned.
     */
    protected HqlQuery doQuery(TypeSafeRootQuery typeSafeQuery) {
        HqlQuery hqlQuery = typeSafeQuery.toHqlQuery();
        
        // Create a hibernate query object using the generated hql+params:
        Query query = sessionFactory.openSession().createQuery(hqlQuery.getHql());
        Object[] params = hqlQuery.getParams();
        for(int i=0; i < params.length; i++) {
            query.setParameter(i, params[i]);
        }
        
        logger.debug(String.format("%s:\n %s\n--- params: %s\n", name.getMethodName(), 
                hqlQuery.getHql(), Arrays.toString(hqlQuery.getParams())));
        
        // call the list, this is the moment of truth:
        query.list();
        
        // return for additional checks:
        return hqlQuery;
    }
    
}
