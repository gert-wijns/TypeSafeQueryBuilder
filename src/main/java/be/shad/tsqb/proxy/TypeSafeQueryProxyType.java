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
package be.shad.tsqb.proxy;

/**
 * Gathers the flags related to the type of proxy.
 */
public enum TypeSafeQueryProxyType {
    EntityType(true, false, false),
    EntityCollectionType(true, true, false),
    EntityPropertyType(false, false, false),
    CompositeType(false, false, true),
    ComponentType(false, false, true),
    SelectionDtoType(false, false, false);

    private final boolean entity;
    private final boolean collection;
    private final boolean composite;

    private TypeSafeQueryProxyType(boolean entity, boolean collection, boolean composite) {
        this.entity = entity;
        this.collection = collection;
        this.composite = composite;
    }

    /**
     * These are proxies which have a propertyType which can
     * be used to get metadata from hibernate.
     */
    public boolean isEntity() {
        return entity;
    }

    /**
     * Collections have some restriction, such as not being chainable.
     */
    public boolean isCollection() {
        return collection;
    }

    /**
     * The proxy is an embedded type of another entity and all of its
     * properties can be accessed through nested property paths.
     */
    public boolean isComposite() {
        return composite;
    }
}
