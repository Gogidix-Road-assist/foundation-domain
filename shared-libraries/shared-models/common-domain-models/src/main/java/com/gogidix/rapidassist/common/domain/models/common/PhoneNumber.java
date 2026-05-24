package com.gogidix.rapidassist.common.domain.models.common;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * PhoneNumber value object for validated phone number storage.
 * Supports international phone numbers with country code.
 */
@Embeddable
public class PhoneNumber {

    @Size(max = 10)
    private String countryCode; // e.g., +1, +44, +353

    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String number;

    @Size(max = 10)
    private String extension;

    @Size(max = 50)
    private String type; // MOBILE, HOME, WORK, FAX

    private Boolean verified = false;

    @Size(max = 20)
    private String carrier;

    // Default constructor
    public PhoneNumber() {
    }

    public PhoneNumber(String number) {
        this.number = number;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getFullNumber() {
        StringBuilder sb = new StringBuilder();
        if (countryCode != null) {
            sb.append(countryCode.startsWith("+") ? countryCode : "+" + countryCode);
        }
        sb.append(number);
        if (extension != null) {
            sb.append(" ext.").append(extension);
        }
        return sb.toString();
    }

    public String getNationalFormat() {
        // Simple implementation - for production use libphonenumber
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber)) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(countryCode, that.countryCode) &&
               Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, number);
    }

    @Override
    public String toString() {
        return getFullNumber();
    }

    public static class Builder {
        private PhoneNumber phoneNumber = new PhoneNumber();

        public Builder countryCode(String countryCode) {
            phoneNumber.setCountryCode(countryCode);
            return this;
        }

        public Builder number(String number) {
            phoneNumber.setNumber(number);
            return this;
        }

        public Builder extension(String extension) {
            phoneNumber.setExtension(extension);
            return this;
        }

        public Builder type(String type) {
            phoneNumber.setType(type);
            return this;
        }

        public Builder verified(Boolean verified) {
            phoneNumber.setVerified(verified);
            return this;
        }

        public PhoneNumber build() {
            return phoneNumber;
        }
    }
}
