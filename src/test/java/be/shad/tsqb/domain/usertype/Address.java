package be.shad.tsqb.domain.usertype;

import java.io.Serializable;

public class Address implements Serializable {
    private static final long serialVersionUID = -8688203930182416032L;
    
    private String street;
    private String number;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    
}