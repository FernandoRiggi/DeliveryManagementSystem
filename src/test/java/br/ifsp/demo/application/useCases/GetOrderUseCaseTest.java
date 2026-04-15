package br.ifsp.demo.application.useCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetOrderUseCaseTest {

    @Mock
    private OrderDeliveryRepository orderDeliveryRepository;

    @InjectMocks
    private GetOrderUseCase sut;

    @TDD
    @Test
    @DisplayName("[#28] Given customer has an order, when getById, then current status should be returned")
    void shouldReturnCurrentStatusWhenGettingOrderById(StatusOrder currentStatus) {
        OrderDelivery order = mock(OrderDelivery.class);
        UUID orderId = UUID.randomUUID();

        when(orderDeliveryRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getStatus()).thenReturn(currentStatus);

        OrderDelivery response = sut.findById(orderId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(currentStatus);

        verify(orderDeliveryRepository).findById(orderId);
        verify(order).getStatus();
    }
}
