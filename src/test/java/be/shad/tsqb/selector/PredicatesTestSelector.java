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
package be.shad.tsqb.selector;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

public class PredicatesTestSelector {
    private Collection<Date> constructionDates;
    private String street;
    private BigDecimal price;
    
    public Collection<Date> getConstructionDates() {
        return constructionDates;
    }
    
    public void setConstructionDates(Collection<Date> constructionDates) {
        this.constructionDates = constructionDates;
    }
    
    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
