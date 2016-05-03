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
package be.shad.tsqb.values;

import java.util.Date;

import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.partial.PartialNullIf;

/**
 * Provides a bunch of functions, this list may grow in time.
 */
public class TypeSafeValueFunctions {
    private final TypeSafeQueryInternal query;

    public TypeSafeValueFunctions(TypeSafeQueryInternal query) {
        this.query = query;
    }

    public <VAL> CaseTypeSafeValue<VAL> caseWhen(Class<VAL> valueClass) {
        return new CaseTypeSafeValue<>(query, valueClass);
    }

    public <VAL> PartialNullIf<VAL> nullIf(VAL val) {
        return nullIf(query.toValue(val));
    }

    public <VAL> PartialNullIf<VAL> nullIf(TypeSafeValue<VAL> val) {
        return new PartialNullIf<VAL>(query, val);
    }

    public <VAL> TypeSafeValue<VAL> distinct(VAL val) {
        return distinct(query.toValue(val));
    }

    public <VAL> TypeSafeValue<VAL> distinct(TypeSafeValue<VAL> val) {
        return new DistinctTypeSafeValue<>(query, val);
    }

    public TypeSafeValue<Long> count() {
        return new CustomTypeSafeValue<>(query, Long.class, "count(*)");
    }

    public <VAL> TypeSafeValue<Long> countDistinct(VAL val) {
        return countDistinct(query.toValue(val));
    }

    public <VAL> TypeSafeValue<Long> countDistinct(TypeSafeValue<VAL> val) {
        return new CountTypeSafeValue(query, distinct(val));
    }

    public <VAL, CAST> TypeSafeValue<CAST> cast(VAL val, Class<CAST> type) {
        return cast(query.toValue(val), type);
    }

    public <VAL, CAST> TypeSafeValue<CAST> cast(TypeSafeValue<VAL> val, Class<CAST> type) {
        return new CastTypeSafeValue<>(query, type, val);
    }

    /**
     * Initiates a concat function value with the given value.
     */
    public ConcatTypeSafeValue concat(String val) {
        return concat(query.toValue(val));
    }

    /**
     * Initiates a concat function value with the given value.
     */
    public ConcatTypeSafeValue concat(Enum<?> val) {
        return concat(query.toValue(val));
    }

    /**
     * Initiates a concat function value with the given value.
     */
    public ConcatTypeSafeValue concat(Number val) {
        return concat(query.toValue(val));
    }

    /**
     * Initiates a concat function value with the given value.
     */
    public ConcatTypeSafeValue concat(TypeSafeValue<?> val) {
        ConcatTypeSafeValue concat = new ConcatTypeSafeValue(query);
        concat.append(val);
        return concat;
    }

    public <VAL> CoalesceTypeSafeValue<VAL> coalesce(VAL val) {
        return coalesce(query.toValue(val));
    }

    public <VAL> CoalesceTypeSafeValue<VAL> coalesce(TypeSafeValue<VAL> val) {
        return new CoalesceTypeSafeValue<>(query, val);
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

    /**
     * Wrapps the value in brackets.
     */
    public <N> TypeSafeValue<N> wrap(TypeSafeValue<N> value) {
        return new WrappedTypeSafeValue<>(query, "", value);
    }

}
