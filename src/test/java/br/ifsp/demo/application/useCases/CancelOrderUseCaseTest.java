package br.ifsp.demo.application.useCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.model.aggregate.OrderDelivery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.when;

@TDD
@ExtendWith(MockitoExtension.class)
public class CancelOrderUseCaseTest {
    @Mock
    private OrderDeliveryRepository repo;

    @InjectMocks
    private CancelOrderUseCase sut;

    @DisplayName("[#45] Should Return [order not found] and not modify any order, when order id not found.")
    void ShouldReturnOrderNotFoundWhenIdNotFound() {
        UUID idNotFound = UUID.randomUUID();
        when(repo.findById(idNotFound).thenReturn(Optional.empty()));

        assertThatIllegalNotFoundOrder().isThrowBy(()-> sut.cancelOrderDelivery(idNotFound));
    }
}
