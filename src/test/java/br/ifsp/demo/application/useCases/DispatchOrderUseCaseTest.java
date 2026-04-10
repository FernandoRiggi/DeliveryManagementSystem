package br.ifsp.demo.application.useCases;
import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.repository.DeliveryManRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.exception.DeliveryManNotFoundException;
import br.ifsp.demo.exception.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DispatchOrderUseCaseTest {

    @Mock
    private OrderDeliveryRepository orderRepository;

    @Mock
    private DeliveryManRepository deliverymanRepository;

    private DispatchOrderUseCase useCase;

    @BeforeEach
    void setUp() {
       useCase = new DispatchOrderUseCase(deliverymanRepository, orderRepository);
    }

    @TDD
    @Test
    @DisplayName("[#42] Given non-existent deliveryman, when dispatch, then should throw DeliveryManNotFoundException")
    void ShouldThrowDeliverymanNotFoundExceptionWhenDeliverymanNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID deliverymanId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(new OrderDelivery()));
        when(deliverymanRepository.findById(deliverymanId))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(DeliveryManNotFoundException.class)
                .isThrownBy(() -> useCase.dispatch(orderId, deliverymanId));
    }

    @TDD
    @Test
    @DisplayName("[#43] Given non-existent order, when dispatch, then should throw OrderNotFoundException")
    void shouldThrowOrderNotFoundExceptionWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID deliverymanId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> useCase.dispatch(orderId, deliverymanId));

    }
}