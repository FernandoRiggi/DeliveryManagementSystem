package br.ifsp.demo.domain.valueObject;

import lombok.Getter;

@Getter
public class Address {
    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String state;
    private String country;
    private String cep;

    private Address() {}

    public Address(String street, String number, String neighborhood, String city, String state, String country, Cep cep) {
        if (street == null || street.isBlank()) throw new IllegalArgumentException("[street is required]");
        if (number == null || number.isBlank()) throw new IllegalArgumentException("[number is required]");
        if (neighborhood == null || neighborhood.isBlank()) throw new IllegalArgumentException("[neighborhood is required]");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("[city is required]");
        if (state == null || state.isBlank()) throw new IllegalArgumentException("[state is required]");
        if (country == null || country.isBlank()) throw new IllegalArgumentException("[country is required]");
        if (cep == null) throw new IllegalArgumentException("[cep is required]");

        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.country = country;
        this.cep = cep.getValue();
    }

    public static Address reconstitute(String street, String number, String neighborhood,
            String city, String state, String country, String cep) {
        Address a = new Address();
        a.street = street;
        a.number = number;
        a.neighborhood = neighborhood;
        a.city = city;
        a.state = state;
        a.country = country;
        a.cep = cep;
        return a;
    }
}