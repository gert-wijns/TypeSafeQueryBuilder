package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQueryInternal;

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
        return upper(query.toValue(val));
    }

    public TypeSafeValue<String> lower(TypeSafeValue<String> val) {
        return new WrappedTypeSafeValue<>(query, "lower", val);
    }
    
    public <N extends Number> TypeSafeValue<N> min(N n) {
        return min(query.toValue(n));
    }

    public <N extends Number> TypeSafeValue<N> min(TypeSafeValue<N> nv) {
        return new WrappedTypeSafeValue<>(query, "min", nv);
    }
    
    public <N extends Number> TypeSafeValue<N> max(N n) {
        return max(query.toValue(n));
    }

    public <N extends Number> TypeSafeValue<N> max(TypeSafeValue<N> nv) {
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
