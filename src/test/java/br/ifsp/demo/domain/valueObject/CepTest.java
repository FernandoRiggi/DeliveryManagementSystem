package br.ifsp.demo.domain.valueObject;

import br.ifsp.demo.annotation.Structural;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CepTest {

    @Structural
    @Test
    @DisplayName("[Structural] Should throw IllegalArgumentException when CEP value is null")
    void shouldThrowIllegalArgumentExceptionWhenCepIsNull() {
        assertThatIllegalArgumentException().isThrownBy(()-> new Cep(null));
    }
}