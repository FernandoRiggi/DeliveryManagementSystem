package br.ifsp.demo.application.useCases;
import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.repository.DeliveryManRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        useCase = new DispatchOrderUseCase(orderRepository, deliverymanRepository);
    }

    @TDD
    @Test
    @DisplayName("[#42] Given non-existent deliveryman, when dispatch, then should throw DeliveryManNotFoundException")
    void ShouldThrowDeliverymanNotFoundExceptionWhenDeliverymanNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID deliverymanId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(new OrderDelivery(StatusOrder.CREATED)));
        when(deliverymanRepository.findById(deliverymanId))
                .thenReturn(Optional.empty());

        assertThatDeliveryManNotFoundException().isThrownBy(()-> useCase.dispatch(orderId, deliverymanId));
    }
}