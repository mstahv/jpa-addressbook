package org.example.backend;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Matti Tahvonen
 */
@Embeddable
public class PhoneBookAddress implements Serializable {
    
    public enum AddressType {
        Home, Work, Other
    }

    private AddressType type;
    private String street;
    private String city;
    private String zip;

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

}
