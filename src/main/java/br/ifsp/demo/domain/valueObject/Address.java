package br.ifsp.demo.domain.valueObject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address{
    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String neighborhood;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String cep;

    protected Address() {
    }

    public Address(String street, String number, String neighborhood, String city, String state, String country, Cep cep) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("[street is required]");
        }
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("[number is required]");
        }
        if (neighborhood == null || neighborhood.isBlank()) {
            throw new IllegalArgumentException("[neighborhood is required]");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("[city is required]");
        }
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("[state is required]");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("[country is required]");
        }
        if (cep == null) {
            throw new IllegalArgumentException("[cep is required]");
        }

        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.country = country;
        this.cep = cep.getValue();
    }
}
