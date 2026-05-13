package br.ifsp.demo.application.UseCases;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
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

    @TDD
    @Test
    @DisplayName("[#48] Given non existing order id, when cancelRoute, then should throw OrderNotFoundException")
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();

        when(repo.findById(orderId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> sut.cancelRoute(orderId))
                .withMessage("[OrderDelivery Not Found]");

        verify(repo).findById(orderId);
        verify(repo, never()).save(any());
    }

    @TDD
    @Test
    @DisplayName("[#48] Given invalid order state, when cancelRoute, then should propagate IllegalStateException")
    void shouldPropagateIllegalStateExceptionWhenOrderCannotCancelRoute() {
        UUID orderId = UUID.randomUUID();
        OrderDelivery order = mock(OrderDelivery.class);

        when(repo.findById(orderId)).thenReturn(Optional.of(order));
        doThrow(new IllegalStateException("[OrderStatus is not DISPATCHED or EN_ROUTE]"))
                .when(order).cancelRoute();

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> sut.cancelRoute(orderId))
                .withMessage("[OrderStatus is not DISPATCHED or EN_ROUTE]");

        verify(repo).findById(orderId);
        verify(order).cancelRoute();
        verify(repo, never()).save(any());
    }

}
