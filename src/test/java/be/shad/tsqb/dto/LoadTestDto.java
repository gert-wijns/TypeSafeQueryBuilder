package be.shad.tsqb.dto;

public class LoadTestDto {
    private String townName;
    private int maxAge;
    private Long fiftyPlusCount;

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void setFiftyPlusCount(Long fiftyPlusCount) {
        this.fiftyPlusCount = fiftyPlusCount;
    }
    
    public Long getFiftyPlusCount() {
        return fiftyPlusCount;
    }
    
}
