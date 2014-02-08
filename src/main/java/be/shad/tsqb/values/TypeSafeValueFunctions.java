package be.shad.tsqb.values;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeQueryInternal;

/**
 * Provides a bunch of functions, this list may grow in time.
 */
public class TypeSafeValueFunctions {
    private final TypeSafeQueryInternal query;

    public TypeSafeValueFunctions(TypeSafeQueryInternal query) {
        this.query = query;
    }
    
    public <VAL> TypeSafeValue<VAL> distinct(VAL val) {
        return distinct(query.toValue(val));
    }

    public <VAL> TypeSafeValue<VAL> distinct(TypeSafeValue<VAL> val) {
        return new WrappedTypeSafeValue<>(query, "distinct", val);
    }


    public TypeSafeValue<Long> count() {
        return new CustomTypeSafeValue<>(query, Long.class, "count(*)", null);
    }
    
    public <VAL> CoalesceTypeSafeValue<VAL> coalesce(VAL val) {
        return coalesce(query.toValue(val));
    }
    
    public <VAL> CoalesceTypeSafeValue<VAL> coalesce(TypeSafeValue<VAL> val) {
        CoalesceTypeSafeValue<VAL> coalesce = new CoalesceTypeSafeValue<>(query, val.getValueClass());
        coalesce.or(val);
        return coalesce;
    }

    public TypeSafeValue<String> upper(String val) {
        return upper(query.toValue(val));
    }

    public TypeSafeValue<String> upper(TypeSafeValue<String> val) {
        return new WrappedTypeSafeValue<>(query, "upper", val);
    }
    
    public TypeSafeValue<String> lower(String val) {
        return lower(query.toValue(val));
    }

    public TypeSafeValue<String> lower(TypeSafeValue<String> val) {
        return new WrappedTypeSafeValue<>(query, "lower", val);
    }

    public <N extends Number> TypeSafeValue<N> min(N n) {
        return minn(query.toValue(n));
    }

    public <N extends Number> TypeSafeValue<N> minn(TypeSafeValue<N> nv) {
        return new WrappedTypeSafeValue<>(query, "min", nv);
    }
    
    public TypeSafeValue<Date> max(Date n) {
        return maxd(query.toValue(n));
    }

    public TypeSafeValue<Date> maxd(TypeSafeValue<Date> nv) {
        return new WrappedTypeSafeValue<>(query, "max", nv);
    }
    
    public TypeSafeValue<Date> min(Date n) {
        return mind(query.toValue(n));
    }

    public TypeSafeValue<Date> mind(TypeSafeValue<Date> nv) {
        return new WrappedTypeSafeValue<>(query, "min", nv);
    }
    
    public <N extends Number> TypeSafeValue<N> max(N n) {
        return maxn(query.toValue(n));
    }

    public <N extends Number> TypeSafeValue<N> maxn(TypeSafeValue<N> nv) {
        return new WrappedTypeSafeValue<>(query, "max", nv);
    }
    
    public <N extends Number> TypeSafeValue<N> avg(N n) {
        return avg(query.toValue(n));
    }

    public <N extends Number> TypeSafeValue<N> avg(TypeSafeValue<N> nv) {
        return new WrappedTypeSafeValue<>(query, "avg", nv);
    }
    
    public <N extends Number> TypeSafeValue<N> sum(N n) {
        return sum(query.toValue(n));
    }

    public <N extends Number> TypeSafeValue<N> sum(TypeSafeValue<N> nv) {
        return new WrappedTypeSafeValue<>(query, "sum", nv);
    }

}
