package br.ifsp.demo.application.UseCases;

import br.ifsp.demo.annotation.Mutation;
import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.exception.OrderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CancelOrderUseCaseTest {
    @Mock
    private OrderDeliveryRepository repo;
    @Mock
    private OrderDelivery order;

    @InjectMocks
    private CancelOrderUseCase sut;

    @TDD
    @Test
    @DisplayName("[#45] Should Return [order not found] and not modify any order, when order id not found.")
    void ShouldReturnOrderNotFoundWhenIdNotFound() {
        UUID idNotFound = UUID.randomUUID();
        when(repo.findById(idNotFound)).thenReturn(Optional.empty());

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(()-> sut.cancelOrderDelivery(idNotFound));
        verify(repo, never()).save(any());
    }

    @Mutation
    @Test
    @DisplayName("[Mutation] Should cancel order and save when order exists")
    void ShouldCancelAndSaveOrderSuccessfully(){
        UUID orderId = UUID.randomUUID();
        when(repo.findById(orderId)).thenReturn(Optional.of(order));
        sut.cancelOrderDelivery(orderId);
        verify(order, times(1)).cancel();
        verify(repo,times(1)).save(order);
    }
}
