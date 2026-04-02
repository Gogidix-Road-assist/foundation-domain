package com.gogidix.rapidassist.geo.location.service.domain.model;

public record Address(
    String streetAddress,
    String city,
    String state,
    String postalCode,
    String country,
    String countryCode,
    String administrativeAreaLevel1,
    String administrativeAreaLevel2,
    String locality,
    String sublocality,
    String premise,
    String subpremise,
    String formattedAddress
) {

    public static AddressBuilder builder() {
        return new AddressBuilder();
    }

    public static class AddressBuilder {
        private String streetAddress;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String countryCode;
        private String administrativeAreaLevel1;
        private String administrativeAreaLevel2;
        private String locality;
        private String sublocality;
        private String premise;
        private String subpremise;
        private String formattedAddress;

        public AddressBuilder streetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        public AddressBuilder city(String city) {
            this.city = city;
            return this;
        }

        public AddressBuilder state(String state) {
            this.state = state;
            return this;
        }

        public AddressBuilder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public AddressBuilder country(String country) {
            this.country = country;
            return this;
        }

        public AddressBuilder countryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public AddressBuilder administrativeAreaLevel1(String area) {
            this.administrativeAreaLevel1 = area;
            return this;
        }

        public AddressBuilder administrativeAreaLevel2(String area) {
            this.administrativeAreaLevel2 = area;
            return this;
        }

        public AddressBuilder locality(String locality) {
            this.locality = locality;
            return this;
        }

        public AddressBuilder sublocality(String sublocality) {
            this.sublocality = sublocality;
            return this;
        }

        public AddressBuilder premise(String premise) {
            this.premise = premise;
            return this;
        }

        public AddressBuilder subpremise(String subpremise) {
            this.subpremise = subpremise;
            return this;
        }

        public AddressBuilder formattedAddress(String formatted) {
            this.formattedAddress = formatted;
            return this;
        }

        public Address build() {
            return new Address(
                streetAddress,
                city,
                state,
                postalCode,
                country,
                countryCode,
                administrativeAreaLevel1,
                administrativeAreaLevel2,
                locality,
                sublocality,
                premise,
                subpremise,
                formattedAddress
            );
        }
    }
}