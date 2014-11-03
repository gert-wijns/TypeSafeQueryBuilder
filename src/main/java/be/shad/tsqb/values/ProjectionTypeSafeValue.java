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

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;

/**
 * A value which is replaced when transforming the query
 * into an hqlQuery object.
 */
public class ProjectionTypeSafeValue<T> extends TypeSafeValueImpl<T> {
    private final String propertyPath;

    public ProjectionTypeSafeValue(CopyContext context, ProjectionTypeSafeValue<T> original) {
        super(context, original);
        this.propertyPath = original.propertyPath;
    }

    public ProjectionTypeSafeValue(TypeSafeQuery query, Class<T> valueType, String propertyPath) {
        super(query, valueType);
        this.propertyPath = propertyPath;
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        return query.getProjections().getTypeSafeValue(propertyPath,
                params.isBuildingForDisplay()).
                toHqlQueryValue(params);
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new ProjectionTypeSafeValue<>(context, this);
    }
}
