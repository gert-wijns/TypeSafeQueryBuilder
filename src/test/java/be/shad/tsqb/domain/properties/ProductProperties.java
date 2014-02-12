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
