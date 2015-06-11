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
package be.shad.tsqb;

import java.util.Collection;

/**
 * Specialized named parameter which can have an associated batchSize.
 * The batch size is used during the doQuery of the TypeSafeQueryDao
 * to fetch the results in multiple times.
 */
public class CollectionNamedParameter extends NamedParameter {
    private Integer batchSize;

    public CollectionNamedParameter(String name, Collection<?> value, Integer batchSize) {
        super(name, value);
        this.batchSize = batchSize;
    }

    @Override
    public Collection<?> getValue() {
        return (Collection<?>) super.getValue();
    }

    public boolean hasBatchSize() {
        return batchSize != null;
    }

    public Integer getBatchSize() {
        return batchSize;
    }
}
