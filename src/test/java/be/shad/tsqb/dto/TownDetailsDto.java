package be.shad.tsqb.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TownDetailsDto {
    private TownDetailsNestedDto nestedDto;
    private Long inhabitants;
    private Date lastUfoSpottingDate;
    private Long id;
    private String name;
    private String customString;
}
