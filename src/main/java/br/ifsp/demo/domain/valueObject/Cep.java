package br.ifsp.demo.domain.valueObject;

import lombok.Getter;

public class Cep {
    @Getter
    private final String value;

    public Cep(String value) {
        if (value == null || value.isBlank()){
            throw new IllegalArgumentException("[cep is required]");
        }

        String normalizedValue = value.replace("-", "").trim();

        if (!normalizedValue.matches("\\d{8}")) {
            throw new IllegalArgumentException("[cep must contain 8 digits]");
        }

        this.value = normalizedValue;
    }

    public String formatted(){
        return value.substring(0, 5) + "-" + value.substring(5);
    }
}
