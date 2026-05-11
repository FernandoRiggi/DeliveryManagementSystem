package br.ifsp.demo.domain.valueObject;

import br.ifsp.demo.annotation.Mutation;
import br.ifsp.demo.annotation.Structural;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CepTest {

    @Structural
    @Test
    @DisplayName("[Structural] Should throw IllegalArgumentException when CEP value is null")
    void shouldThrowIllegalArgumentExceptionWhenCepIsNull() {
        assertThatIllegalArgumentException().isThrownBy(()-> new Cep(null));
    }

    @Mutation
    @Test
    @DisplayName("[Mutation] Should return formatted CEP with dash separator")
    void shouldReturnFormattedCepWithDashSeparator() {
        Cep cep = new Cep("12345678");
        assertThat(cep.formatted()).isEqualTo("12345-678");
    }
}