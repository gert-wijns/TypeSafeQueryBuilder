package be.shad.tsqb.dto;

import java.util.Date;

public class TownDetailsDto {
    private TownDetailsNestedDto nestedDto;
    private Long inhabitants;
    private Date lastUfoSpottingDate;
    private Long id;
    private String name;
    private String customString;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomString() {
        return customString;
    }

    public void setCustomString(String customString) {
        this.customString = customString;
    }

    public TownDetailsNestedDto getNestedDto() {
        return nestedDto;
    }

    public void setNestedDto(TownDetailsNestedDto nestedDto) {
        this.nestedDto = nestedDto;
    }

    public Long getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(Long inhabitants) {
        this.inhabitants = inhabitants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Date getLastUfoSpottingDate() {
        return lastUfoSpottingDate;
    }

    public void setLastUfoSpottingDate(Date lastUfoSpottingDate) {
        this.lastUfoSpottingDate = lastUfoSpottingDate;
    }
    
}
