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
package be.shad.tsqb.query;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.helper.TypeSafeQueryHelper;
import be.shad.tsqb.proxy.TypeSafeQueryProxy;
import be.shad.tsqb.selection.SelectionValueTransformer;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Maintains the invocationQueue, provides the entity aliases and buffers the last selected value.
 */
public class TypeSafeRootQueryImpl extends AbstractTypeSafeQuery implements TypeSafeRootQuery, TypeSafeRootQueryInternal {
    
    private List<TypeSafeQueryProxyData> invocationQueue = new LinkedList<>();
    private TypeSafeValue<?> lastSelectedValue;
    private int entityAliasCount = 1;

    public TypeSafeRootQueryImpl(TypeSafeQueryHelper helper) {
        super(helper);
        setRootQuery(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQueryInternal getParentQuery() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void invocationWasMade(TypeSafeQueryProxyData data) {
        invocationQueue.add(data);
    }

    /**
     * {@inheritDoc}
     */
    public List<TypeSafeQueryProxyData> dequeueInvocations() {
        List<TypeSafeQueryProxyData> old = invocationQueue;
        invocationQueue = new LinkedList<>();
        return old;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeQueryProxyData dequeueInvocation() {
        List<TypeSafeQueryProxyData> invocations = dequeueInvocations();
        if( invocations.isEmpty() ) {
            return null;
        }
        if( invocations.size() > 1 ) {
            throw new IllegalStateException(String.format("There are %d invocations pending. Only 1 should be pending. "
                    + "The one that was used to call join(value, joinType).", invocations.size()));
        }
        return invocations.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createEntityAlias() {
        return "hobj"+ entityAliasCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectValue(Object value) {
        getProjections().project(value, null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T select(Class<T> resultClass) {
        return helper.createTypeSafeSelectProxy(this, resultClass);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T, V> V select(Class<V> transformedClass, T value, SelectionValueTransformer<T, V> transformer) {
        if (value instanceof TypeSafeQueryProxy) {
            // invocation was not added because it is not a leaf (to support method chaining).
            invocationWasMade(((TypeSafeQueryProxy) value).getTypeSafeProxyData());
        }
        getProjections().setTransformerForNextProjection(transformer);
        return (V) getDummyValue(transformedClass);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T queueValueSelected(TypeSafeValue<T> value) {
        lastSelectedValue = value;
        return (T) getDummyValue(value.getValueClass());
    }

    /**
     * return a random value, (but take primitives into account to prevent NPEs)
     */
    private Object getDummyValue(Class<?> clazz) {
        Class<?> primitiveClass = getPrimitiveClass(clazz);
        if( primitiveClass != null ) {
            return defaultPrimitiveValue(primitiveClass);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeValue<?> dequeueSelectedValue() {
        TypeSafeValue<?> value = lastSelectedValue;
        lastSelectedValue = null;
        return value;
    }
    
    /**
     * @return the primitive class if the class could be a primitive.
     */
    private Class<?> getPrimitiveClass(Class<?> valueClass) {
        if( Boolean.class.equals(valueClass) ) {
            return Boolean.TYPE;
        } else if ( Integer.class.equals(valueClass) ) {
            return Integer.TYPE;
        } else if ( Long.class.equals(valueClass) ) {
            return Long.TYPE;
        } else if ( Double.class.equals(valueClass) ) {
            return Double.TYPE;
        } else if ( Byte.class.equals(valueClass) ) {
            return Byte.TYPE;
        } else if ( Short.class.equals(valueClass) ) {
            return Short.TYPE;
        } else if ( Float.class.equals(valueClass) ) {
            return Float.TYPE;
        } else if ( Character.class.equals(valueClass) ) {
            return Character.TYPE;
        }
        return null;
    }

    /**
     * @return a default value for each primitive class.
     */
    private Object defaultPrimitiveValue(Class<?> primitiveClass) {
        if( primitiveClass == Boolean.TYPE ) {
            return Boolean.FALSE;
        } else if( primitiveClass == Integer.TYPE ) {
            return Integer.valueOf(0);
        } else if ( primitiveClass == Long.TYPE ) {
            return Long.valueOf(0);
        } else if ( primitiveClass == Double.TYPE ) {
            return Double.valueOf(0);
        } else if( primitiveClass == Byte.TYPE) {
            return Byte.valueOf((byte) 0);
        } else if( primitiveClass == Short.TYPE ) {
            return Short.valueOf((short) 0);
        } else if ( primitiveClass == Float.TYPE ) {
            return Float.valueOf(0);
        } else if ( primitiveClass == Character.TYPE ) {
            return Character.valueOf('a');
        }
        throw new IllegalArgumentException("Didn't take a primtiive class into account: " + primitiveClass);
    }

}
