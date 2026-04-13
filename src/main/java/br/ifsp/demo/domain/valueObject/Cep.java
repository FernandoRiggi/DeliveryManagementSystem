package br.ifsp.demo.domain.valueObject;

public record Cep(String value) {

    public Cep {
        if (value == null || value.isBlank()){
            throw new IllegalArgumentException("[cep is required]");
        }

        String normalizedValue = value.replace("-", "").trim();

        if (!normalizedValue.matches("\\d{8}")) {
            throw new IllegalArgumentException("[cep must contain 8 digits]");
        }

        value = normalizedValue;
    }

    public String formatted(){
        return value.substring(0, 5) + "-" + value.substring(5);
    }
}
