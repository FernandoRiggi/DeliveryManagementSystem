package br.ifsp.demo.domain.valueObject;

public record Address(
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String country,
        Cep cep
) {
    public Address {
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
    }
}
