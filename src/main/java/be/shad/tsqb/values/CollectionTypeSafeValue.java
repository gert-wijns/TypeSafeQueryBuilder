package be.shad.tsqb.values;

import java.util.Collection;

import be.shad.tsqb.query.TypeSafeQuery;

/**
 * The value is a collection of actual values, not proxies or property paths.
 * These values are added to the query as params.
 */
public class CollectionTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private Collection<T> value;
    
    public CollectionTypeSafeValue(TypeSafeQuery query, Collection<T> value) {
        super(query, null);
        this.value = value;
    }

    public Collection<T> getValue() {
        return value;
    }

    public void setValue(Collection<T> value) {
        this.value = value;
    }

    @Override
    public HqlQueryValueImpl toHqlQueryValue() {
        StringBuilder sb = new StringBuilder("(");
        for(int i=0; i < value.size(); i++) {
            if( sb.length() > 1 ) {
                sb.append(", ");
            }
            sb.append("?");
        }
        sb.append(")");
        return new HqlQueryValueImpl(sb.toString(), value.toArray());
    }

}
