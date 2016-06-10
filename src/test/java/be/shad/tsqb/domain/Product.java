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
package be.shad.tsqb.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import be.shad.tsqb.domain.properties.ManyProperties;
import be.shad.tsqb.domain.properties.ProductProperties;

@Entity
@Table(name = "Product")
public class Product extends DomainObject {
    private static final long serialVersionUID = -6807452894720315681L;

    @Embedded
    private ProductProperties productProperties;

    @Embedded
    private ManyProperties manyProperties;

    private String name;

    public ManyProperties getManyProperties() {
        return manyProperties;
    }

    public void setManyProperties(ManyProperties manyProperties) {
        this.manyProperties = manyProperties;
    }

    public ProductProperties getProductProperties() {
        return productProperties;
    }

    public void setProductProperties(ProductProperties productProperties) {
        this.productProperties = productProperties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
