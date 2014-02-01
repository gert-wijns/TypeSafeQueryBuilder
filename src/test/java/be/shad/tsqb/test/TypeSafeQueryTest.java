package be.shad.tsqb.test;

import java.util.Arrays;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;

import be.shad.tsqb.helper.TypeSafeQueryHelperImpl;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.query.TypeSafeRootQuery;

public class TypeSafeQueryTest {

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
		
		// don't look here, it's just to print the class+method name of the caller site... debug purposes!
		String dirty = Thread.currentThread().getStackTrace()[2].toString();
		dirty = dirty.substring(0, dirty.indexOf('('));
		dirty = dirty.substring(dirty.substring(0, dirty.lastIndexOf('.')).lastIndexOf('.') + 1);
		System.out.println("\n\n" + dirty + ":\n" + hqlQuery.getHql() + 
				"\n--- params: " + Arrays.toString(hqlQuery.getParams()));
		
		// call the list, this is the moment of truth:
		query.list();
		
		// return for additional checks:
		return hqlQuery;
	}
	
}
