package com.gogidix.rapidassist.common.domain.models.common;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Address value object representing a physical address.
 * Embedded in various entities like User, Customer, Provider, etc.
 */
@Embeddable
public class Address {

    @Size(max = 100)
    private String streetLine1;

    @Size(max = 100)
    private String streetLine2;

    @Size(max = 100)
    private String streetLine3;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 20)
    private String postalCode;

    @Size(max = 100)
    private String country;

    @Size(max = 10)
    private String countryCode; // ISO 3166-1 alpha-2

    private Double latitude;

    private Double longitude;

    @Size(max = 100)
    private String formattedAddress;

    @Size(max = 50)
    private String addressType; // HOME, WORK, SHIPPING, BILLING

    // Default constructor
    public Address() {
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getStreetLine1() {
        return streetLine1;
    }

    public void setStreetLine1(String streetLine1) {
        this.streetLine1 = streetLine1;
    }

    public String getStreetLine2() {
        return streetLine2;
    }

    public void setStreetLine2(String streetLine2) {
        this.streetLine2 = streetLine2;
    }

    public String getStreetLine3() {
        return streetLine3;
    }

    public void setStreetLine3(String streetLine3) {
        this.streetLine3 = streetLine3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    public String getFullAddress() {
        if (formattedAddress != null) {
            return formattedAddress;
        }
        StringBuilder sb = new StringBuilder();
        if (streetLine1 != null) sb.append(streetLine1);
        if (streetLine2 != null) sb.append(", ").append(streetLine2);
        if (city != null) sb.append(", ").append(city);
        if (state != null) sb.append(", ").append(state);
        if (postalCode != null) sb.append(" ").append(postalCode);
        if (country != null) sb.append(", ").append(country);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return Objects.equals(streetLine1, address.streetLine1) &&
               Objects.equals(streetLine2, address.streetLine2) &&
               Objects.equals(city, address.city) &&
               Objects.equals(postalCode, address.postalCode) &&
               Objects.equals(countryCode, address.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetLine1, streetLine2, city, postalCode, countryCode);
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    public static class Builder {
        private Address address = new Address();

        public Builder streetLine1(String streetLine1) {
            address.setStreetLine1(streetLine1);
            return this;
        }

        public Builder streetLine2(String streetLine2) {
            address.setStreetLine2(streetLine2);
            return this;
        }

        public Builder city(String city) {
            address.setCity(city);
            return this;
        }

        public Builder state(String state) {
            address.setState(state);
            return this;
        }

        public Builder postalCode(String postalCode) {
            address.setPostalCode(postalCode);
            return this;
        }

        public Builder country(String country) {
            address.setCountry(country);
            return this;
        }

        public Builder countryCode(String countryCode) {
            address.setCountryCode(countryCode);
            return this;
        }

        public Builder latitude(Double latitude) {
            address.setLatitude(latitude);
            return this;
        }

        public Builder longitude(Double longitude) {
            address.setLongitude(longitude);
            return this;
        }

        public Builder addressType(String addressType) {
            address.setAddressType(addressType);
            return this;
        }

        public Address build() {
            return address;
        }
    }
}
