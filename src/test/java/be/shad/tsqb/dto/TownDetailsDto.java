package be.shad.tsqb.dto;

public class TownDetailsDto {
    private TownDetailsNestedDto nestedDto;
    private Long inhabitants;
    private String name;

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
    
}
