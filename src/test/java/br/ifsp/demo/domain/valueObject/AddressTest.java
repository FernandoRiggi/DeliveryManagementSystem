package br.ifsp.demo.domain.valueObject;

import static org.assertj.core.api.Assertions.assertThat;
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
}