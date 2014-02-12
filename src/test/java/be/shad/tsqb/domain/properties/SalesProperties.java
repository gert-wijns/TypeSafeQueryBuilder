package be.shad.tsqb.domain.properties;

import javax.persistence.Embeddable;

@Embeddable
public class SalesProperties {
    
    private boolean salesAllowed;

    public boolean isSalesAllowed() {
        return salesAllowed;
    }

    public void setSalesAllowed(boolean salesAllowed) {
        this.salesAllowed = salesAllowed;
    }
    
}
