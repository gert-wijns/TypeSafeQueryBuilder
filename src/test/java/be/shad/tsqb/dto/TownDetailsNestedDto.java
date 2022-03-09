package be.shad.tsqb.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TownDetailsNestedDto {
    private Date oldestBuildingConstructionDate;
    private double lattitude;
}
