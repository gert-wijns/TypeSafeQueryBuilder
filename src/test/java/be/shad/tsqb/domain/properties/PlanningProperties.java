package be.shad.tsqb.domain.properties;

import javax.persistence.Embeddable;

@Embeddable
public class PlanningProperties {

    private String algorithm;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    
}
