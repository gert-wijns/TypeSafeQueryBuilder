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
package be.shad.tsqb.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

import be.shad.tsqb.exceptions.TsqbException;

public class ConcreteDtoClassResolverImpl implements ConcreteDtoClassResolver {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> getConcreteClass(Class<T> requestedClass) {
        if (requestedClass.isInterface()) {
            if (SortedMap.class.isAssignableFrom(requestedClass)) {
                return (Class<T>) TreeMap.class;
            }
            if (Map.class.isAssignableFrom(requestedClass)) {
                return (Class<T>) HashMap.class;
            }
            if (Set.class.isAssignableFrom(requestedClass)) {
                return (Class<T>) HashSet.class;
            }
            if (List.class.isAssignableFrom(requestedClass)) {
                return (Class<T>) ArrayList.class;
            }
            if (Collection.class.isAssignableFrom(requestedClass)) {
                return (Class<T>) ArrayList.class;
            }
            throw new IllegalArgumentException("Don't know implementation "
                    + "to use for interface: " + requestedClass);
        } else {
            return requestedClass;
        }
    }

    protected <T> Supplier<T> newBuilderSupplier(Class<T> clazz) {
        try {
            int innerClassEnd = clazz.getName().lastIndexOf("$");
            if (innerClassEnd > 0) {
                Class<?> valueClass = Class.forName(clazz.getName().substring(0, innerClassEnd));
                return newInstanceSupplier(valueClass.getMethod("builder"));
            } else {
                throw new IllegalArgumentException("Clan't create builder supplier for " + clazz);
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new TsqbException(e);
        }
    }

    protected <T> Supplier<T> newInstanceSupplier(Class<T> clazz) {
        return () -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                throw new TsqbException(e);
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected <T> Supplier<T> newInstanceSupplier(Method newInstance) {
        return () -> {
            try {
                return (T) newInstance.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new TsqbException(e);
            }
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> Class<T> getSelectionBuilderClass(Class<?> builderClass) {
        if (Modifier.isFinal(builderClass.getModifiers())) {
            return (Class) builderClass.getSuperclass();
        }
        return (Class) builderClass;
    }

    @SuppressWarnings("unchecked")
    protected <T, R> BuildFn<T, R> getBuildFunction(Class<T> builderClass) {
        try {
            Method buildMethod = getBuilderMethod(builderClass);
            return builder -> {
                try {
                    return (R) buildMethod.invoke(builder);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalArgumentException("buildMethodName: build " +
                            " could not be invoked on " + builder, e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("buildMethodName: build " +
                    "is not found on " + builderClass.getCanonicalName(), e);
        }
    }

    protected <T> Method getBuilderMethod(Class<T> builderClass) throws NoSuchMethodException {
        return builderClass.getMethod("build");
    }

    protected <T> boolean isBuilderClass(Class<T> builderClass) {
        return builderClass.getName().contains("$") && builderClass.getName().endsWith("Builder");
    }

    @SuppressWarnings("unchecked")
    protected <T, R> Class<R> getResultClass(Class<T> selectionBuilderClass) {
        try {
            return (Class<R>) getBuilderMethod(selectionBuilderClass).getReturnType();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("buildMethodName: build " +
                    "is not found on " + selectionBuilderClass.getCanonicalName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SB, SR> SelectionBuilderSpec<SB, SR> createBuilderSpec(Class<SB> selectionBuilderClass) {
        if (isBuilderClass(selectionBuilderClass)) {
            selectionBuilderClass = getSelectionBuilderClass(selectionBuilderClass);
            Class<SR> selectionResultClass = getResultClass(selectionBuilderClass);
            BuildFn<SB, SR> buildFunction = getBuildFunction(selectionBuilderClass);
            Supplier<SB> builderSup = newBuilderSupplier(selectionBuilderClass);
            return new SelectionBuilderSpec<>(selectionBuilderClass, selectionResultClass, buildFunction, builderSup);
        } else {
            Class<SB> selectionResultClass = getConcreteClass(selectionBuilderClass);
            Supplier<SB> supplier = newInstanceSupplier(selectionResultClass);
            return new SelectionBuilderSpec<>(selectionResultClass, (Class<SR>) selectionResultClass, null, supplier);
        }
    }
}
