package be.shad.tsqb.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;
import be.shad.tsqb.joins.TypeSafeQueryJoin;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.query.TypeSafeQueryInternal;

public class TypeSafeQueryProxyDataTree implements HqlQueryBuilder {
    private final List<TypeSafeQueryFrom> froms = new ArrayList<>();
    private final Map<TypeSafeQueryProxyData, TypeSafeQueryJoin<?>> joins = new HashMap<>();
    private final Set<TypeSafeQueryProxyData> queryData = new LinkedHashSet<>();
    private final TypeSafeQueryHelper helper;
    private final TypeSafeQueryInternal query;

    public TypeSafeQueryProxyDataTree(TypeSafeQueryHelper helper, TypeSafeQueryInternal query) {
        this.helper = helper;
        this.query = query;
    }
    
    @SuppressWarnings("unchecked")
    public <T> TypeSafeQueryJoin<T> getJoin(TypeSafeQueryProxyData data) {
        return (TypeSafeQueryJoin<T>) joins.get(data);
        
    }

    /**
     * Create proxy data for the given proxy.
     * <p>
     * This data is added as child of the parent and is added to the root
     * when the parent is null. When data is part of the root, the data
     * is used to construct a FROM part of the query.
     */
    public TypeSafeQueryProxyData createData(TypeSafeQueryProxyData parent, 
            String propertyName, Class<?> propertyType, TypeSafeQueryProxy proxy) {
        TypeSafeQueryProxyData child = new TypeSafeQueryProxyData(parent, propertyName, 
                propertyType, proxy, query.createEntityAlias());
        if( parent == null ) {
            froms.add(new TypeSafeQueryFrom(helper, child));
        } else {
            TypeSafeQueryProxyData root = parent;
            while( root.getParent() != null ) {
                root = root.getParent();
            }
            for(TypeSafeQueryFrom from: froms) {
                if( from.getRoot().equals(root) ) {
                    TypeSafeQueryJoin<Object> join = new TypeSafeQueryJoin<>(query, child);
                    joins.put(child, join);
                    from.addJoin(join);
                }
            }
        }
        queryData.add(child);
        return child;
    }

    /**
     * Create proxy data for a non-hibernate entity type which can be used
     * as leaf in restrictions.
     */
    public TypeSafeQueryProxyData createData(TypeSafeQueryProxyData parent,
            String propertyName, Class<?> propertyType) {
        TypeSafeQueryProxyData child = new TypeSafeQueryProxyData(
                parent, propertyName, propertyType);
        if( parent == null ) {
            throw new IllegalArgumentException("");
        }
        queryData.add(child);
        return child;
    }
    
    /**
     * Check if this dataTree contains the data.
     * 
     * @param join optional parameter, in case of creating a where clause in a join
     *             then the join data must be the data or must be added after data.
     */
    public boolean isInScope(TypeSafeQueryProxyData data, TypeSafeQueryProxyData join) {
        if( join == null ) {
            return queryData.contains(data);
        } else if ( !queryData.contains(data) ) {
            return false;
        }
        
        // if the property is not an entity, then use the parent instead,
        // the parent should be part of one of the joins in the froms.
        data = data.getProxy() == null ? data.getParent(): data;
        if( data.equals(join) ) {
            return true;
        }
        
        // check if data was joined before join.
        for(TypeSafeQueryFrom from: froms) {
            // either the data to find is the root, or one of the joins.
            if( from.getRoot().equals(data) ) {
                return true;
            }
            if( from.getRoot().equals(join) ) {
                return false; // found join before data
            }
            for(TypeSafeQueryJoin<?> joined: from.getJoins()) {
                if( joined.getData().equals(data) ) {
                    return true;
                }
                if( joined.getData().equals(join) ) {
                    return false; // found join before data
                }
            }
        }
        // shouldn't ever happen...
        throw new IllegalStateException("The data is part of the queryData, but its join was not found?");
    }

    @Override
    public void appendTo(HqlQuery query) {
        for(TypeSafeQueryFrom from: froms) {
            from.appendTo(query);
        }
    }
    
}
