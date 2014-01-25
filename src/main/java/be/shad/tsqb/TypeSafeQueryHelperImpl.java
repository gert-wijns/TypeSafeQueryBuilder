package be.shad.tsqb;

import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

import be.shad.tsqb.proxy.TypeSafeQueryProxyFactory;

public class TypeSafeQueryHelperImpl implements TypeSafeQueryHelper {
	private final SessionFactory sessionFactory;
	private final TypeSafeQueryProxyFactory proxyFactory;

	public TypeSafeQueryHelperImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.proxyFactory = new TypeSafeQueryProxyFactory(this);
	}
	
	@Override
	public boolean isEntity(Class<?> clazz) {
		ClassMetadata meta = sessionFactory.getClassMetadata(clazz);
		return meta != null;
	}

	@Override
	public Class<?> getTargetEntityClass(Class<?> fromClass, String property) {
		ClassMetadata meta = sessionFactory.getClassMetadata(fromClass);
		Type propertyType = meta.getPropertyType(property);
		if( CollectionType.class.isAssignableFrom(propertyType.getClass()) ) {
			CollectionType collectionType = (CollectionType) propertyType;
			Type elementType = collectionType.getElementType(
					((SessionFactoryImplementor) sessionFactory));
			return elementType.getReturnedClass();
		}
		return propertyType.getReturnedClass();
	}
	
	@Override
	public TypeSafeQueryProxyFactory getTypeSafeProxyFactory() {
		return proxyFactory;
	}

	@Override
	public String getEntityName(Class<?> entityClass) {
		return sessionFactory.getClassMetadata(entityClass).getEntityName();
	}

	@Override
	public TypeSafeQuery createQuery() {
		return new TypeSafeQuery(this);
	}

}
