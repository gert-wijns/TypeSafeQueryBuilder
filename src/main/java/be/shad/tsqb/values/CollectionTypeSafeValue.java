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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.NamedParameter;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.restrictions.RestrictionOperator;

/**
 * The value is a collection of actual values, not proxies or property paths.
 * These values are added to the query as params.
 */
public class CollectionTypeSafeValue<T> extends TypeSafeValueImpl<T> implements NamedValueEnabled, OperatorAwareValue {
    private Collection<T> values;

    /**
     * Copy constructor
     */
    @SuppressWarnings("unchecked")
    protected CollectionTypeSafeValue(CopyContext context, CollectionTypeSafeValue<T> original) {
        super(context, original);
        if (original.values != null) {
            try {
                values = original.values.getClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Couldn't create same "
                        + "collection as existing collection.", e);
            }
            for(T value: original.values){
                values.add(context.getOrOriginal(value));
            }
        }
    }
    
    public CollectionTypeSafeValue(TypeSafeQuery query, Class<T> valueClass, Collection<T> value) {
        this(query, valueClass);
        setValues(value);
    }
    
    public CollectionTypeSafeValue(TypeSafeQuery query, Class<T> valueClass) {
        super(query, valueClass);
    }
    
    public Collection<T> getValues() {
        return values == null ? null: Collections.unmodifiableCollection(values);
    }

    /**
     * Validate the values are not empty.
     */
    public void setValues(Collection<T> values) {
        setNamedValue(values);
    }

    @Override
    public HqlQueryValueImpl toHqlQueryValue(HqlQueryBuilderParams params) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Collection is empty when transforming to query");
        }
        if (params.isRequiresLiterals()) {
            StringBuilder sb = new StringBuilder("(");
            for(Object val: values) {
                if( sb.length() > 1 ) {
                    sb.append(", ");
                }
                sb.append(query.getHelper().toLiteral(val));
            }
            sb.append(")");
            return new HqlQueryValueImpl(sb.toString());
        } else {
            String name = params.createNamedParameter();
            return new HqlQueryValueImpl(new StringBuilder("(:").append(name).append(")").toString(), new NamedParameter(name, values));
        }
    }

    /**
     * Sets the collection value of this parameter, 
     * the collection will have to be null or the elements in 
     * the collection will have to be assignable from the value class.
     * The collection will be validated:
     * <ul>
     * <li>Null is allowed as collection value. Though presense of a value will be
     *     checked when the query is transformed to HQL.</li>
     * <li>When a collection is set, all of its elements must be assignable from the value class.</li>
     * <li>Elements must not be null.</li>
     * <li>When a collection is set, it must not be empty. A defensive copy is taken, 
     *     so adding elements to a referenced list will not work.</li>
     * </ul>
     */
    @Override
    public void setNamedValue(Object namedValue) {
        if (namedValue == null) {
            this.values = null;
            return;
        }
        
        Collection<?> values = null;
        if (namedValue instanceof Collection<?>) {
            values = (Collection<?>) namedValue;
        } else {
            values = Collections.singleton(namedValue);
        }
        
        List<T> namedValues = new LinkedList<T>();
        for(Object value: values) {
            if (value == null) {
                throw new IllegalArgumentException(String.format("Null value in "
                        + "collection is not allowed. Collection: %s.", values));
            }
            if (!getValueClass().isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException(String.format("The value must be of type "
                        + "[%s] but was of type [%s].", getValueClass(), value.getClass()));
            }
            namedValues.add(getValueClass().cast(value));
        }
        this.values = namedValues;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new CollectionTypeSafeValue<>(context, this);
    }

    /**
     * Use more specific operator when the collections only contains a single value
     */
    @Override
    public RestrictionOperator getOperator(RestrictionOperator original) {
        if (values.size() == 1) {
            switch (original) {
                case IN: return RestrictionOperator.EQUAL;
                case NOT_IN: return RestrictionOperator.NOT_EQUAL;
                default:
            }
        }
        return original;
    }

}
