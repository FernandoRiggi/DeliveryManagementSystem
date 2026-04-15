package br.ifsp.demo.application.useCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CancelRouteUseCaseTest {

    @Mock
    private OrderDeliveryRepository repo;

    @InjectMocks
    private CancelRouteUseCase sut;


    @TDD
    @Test
    @DisplayName("[#48] Given existing order id, when cancelRoute, then should cancel route and save order")
    void shouldCancelRouteWhenOrderExists() {
        UUID orderId = UUID.randomUUID();
        OrderDelivery order = mock(OrderDelivery.class);

        when(repo.findById(orderId)).thenReturn(Optional.of(order));

        sut.cancelRoute(orderId);

        verify(repo).findById(orderId);
        verify(order).cancelRoute();
        verify(repo).save(order);
    }

}
