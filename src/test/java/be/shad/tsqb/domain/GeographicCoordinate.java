package be.shad.tsqb.domain;

import javax.persistence.Embeddable;

@Embeddable
public class GeographicCoordinate {

    private double longitude;
    private double lattitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }
    
}
