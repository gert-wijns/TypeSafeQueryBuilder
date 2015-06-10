package be.shad.tsqb.dto;

import java.util.Date;

public class TownDetailsNestedDto {
    private Date oldestBuildingConstructionDate;
    private double lattitude;

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public Date getOldestBuildingConstructionDate() {
        return oldestBuildingConstructionDate;
    }

    public void setOldestBuildingConstructionDate(Date oldestBuildingConstructionDate) {
        this.oldestBuildingConstructionDate = oldestBuildingConstructionDate;
    }

}
