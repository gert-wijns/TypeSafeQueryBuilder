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
package be.shad.tsqb.domain.properties;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class ProductProperties {
    
    @Embedded
    private PlanningProperties planning;

    @Embedded
    private SalesProperties sales;

    public PlanningProperties getPlanning() {
        return planning;
    }

    public void setPlanning(PlanningProperties planning) {
        this.planning = planning;
    }

    public SalesProperties getSales() {
        return sales;
    }

    public void setSales(SalesProperties sales) {
        this.sales = sales;
    }
    
}
