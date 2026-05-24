package com.gogidix.rapidassist.common.domain.models.common;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * EmailAddress value object for validated email storage.
 */
@Embeddable
public class EmailAddress {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private Boolean verified = false;

    private Boolean primary = false;

    // Default constructor
    public EmailAddress() {
    }

    public EmailAddress(String email) {
        this.email = email;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getLocalPart() {
        if (email == null || !email.contains("@")) return email;
        return email.substring(0, email.indexOf("@"));
    }

    public String getDomain() {
        if (email == null || !email.contains("@")) return null;
        return email.substring(email.indexOf("@") + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailAddress)) return false;
        EmailAddress that = (EmailAddress) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return email;
    }

    public static class Builder {
        private EmailAddress emailAddress = new EmailAddress();

        public Builder email(String email) {
            emailAddress.setEmail(email);
            return this;
        }

        public Builder verified(Boolean verified) {
            emailAddress.setVerified(verified);
            return this;
        }

        public Builder primary(Boolean primary) {
            emailAddress.setPrimary(primary);
            return this;
        }

        public EmailAddress build() {
            return emailAddress;
        }
    }
}
