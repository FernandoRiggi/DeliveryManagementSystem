package br.ifsp.demo.domain.valueObject;

import static org.assertj.core.api.Assertions.assertThat;

import br.ifsp.demo.annotation.Structural;
import org.junit.jupiter.api.DisplayName;
import br.ifsp.demo.annotation.Mutation;
import org.junit.jupiter.api.Test;

class AddressTest {
    @Mutation
    @Test
    @DisplayName("[Mutation] Should return non-null Address when reconstituting from raw fields")
    void shouldReturnNonNullAddressWhenReconstituting() {
        Address address = Address.reconstitute("Rua das Flores", "123", "Centro",
                "São Paulo", "SP", "Brasil", "12345678");
        assertThat(address).isNotNull();
    }

    @Structural
    @Test
    void shouldReturnAddressDataWhenCreated() {
        Address address = new Address(
                "Street A",
                "10",
                "Center",
                "São Carlos",
                "SP",
                "Brasil",
                new Cep("13500-000")
        );

        assertThat(address.getStreet()).isEqualTo("Street A");
        assertThat(address.getNumber()).isEqualTo("10");
        assertThat(address.getNeighborhood()).isEqualTo("Center");
        assertThat(address.getCity()).isEqualTo("São Carlos");
        assertThat(address.getState()).isEqualTo("SP");
        assertThat(address.getCountry()).isEqualTo("Brasil");
        assertThat(address.getCep()).isEqualTo("13500000");
    }
}