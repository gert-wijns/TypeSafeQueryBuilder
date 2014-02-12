package be.shad.tsqb.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import be.shad.tsqb.domain.properties.ProductProperties;

@Entity
@Table(name = "Product")
public class Product extends DomainObject {
    private static final long serialVersionUID = -6807452894720315681L;
    
    @Embedded
    private ProductProperties properties;
    
    private String name;

    public ProductProperties getProperties() {
        return properties;
    }

    public void setProperties(ProductProperties properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
