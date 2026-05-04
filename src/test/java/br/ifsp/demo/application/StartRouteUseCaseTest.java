package br.ifsp.demo.application;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.event.EventType;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.domain.valueObject.CustomerType;
import br.ifsp.demo.exception.OrderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartRouteUseCaseTest {

    @Mock
    private OrderDeliveryRepository repo;

    @InjectMocks
    StartRouteUseCase sut;

    @TDD
    @Test
    @DisplayName("[#BUG] Should start route when order is DISPATCHED")
    void shouldStartRouteWhenOrderIsDispatched() {
        UUID orderId = UUID.randomUUID();

        Customer customer = new Customer("John Doe", CustomerType.REGULAR);
        DeliveryMan deliveryMan = new DeliveryMan("Mike", 10);

        Address pickupAddress = new Address(
                "Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000")
        );
        Address deliveryAddress = new Address(
                "Street B", "11", "Center", "Araraquara", "SP", "Brasil", new Cep("13400-000")
        );

        OrderDelivery order = new OrderDelivery(customer, pickupAddress, deliveryAddress, 10.0);
        order.dispatch(deliveryMan);

        when(repo.findById(orderId)).thenReturn(Optional.of(order));

        sut.startRoute(orderId);

        assertThat(order.getStatus()).isEqualTo(StatusOrder.EN_ROUTE);
        assertThat(order.getEvents())
                .extracting(event -> event.getType())
                .contains(EventType.EN_ROUTE);

        verify(repo).findById(orderId);
        verify(repo).save(order);
    }

    @TDD
    @Test
    @DisplayName("[#BUG] Should throw OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();

        when(repo.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.startRoute(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("[OrderDelivery Not Found]");

        verify(repo).findById(orderId);
        verify(repo, never()).save(any());
    }
    @TDD
    @Test
    @DisplayName("[#BUG] Should throw IllegalStateException when order is not DISPATCHED")
    void shouldThrowIllegalStateExceptionWhenOrderIsNotDispatched() {
        UUID orderId = UUID.randomUUID();

        Customer customer = new Customer("John Doe", CustomerType.REGULAR);

        Address pickupAddress = new Address(
                "Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000")
        );
        Address deliveryAddress = new Address(
                "Street B", "11", "Center", "Araraquara", "SP", "Brasil", new Cep("13400-000")
        );

        OrderDelivery order = new OrderDelivery(customer, pickupAddress, deliveryAddress, 10.0);

        when(repo.findById(orderId)).thenReturn(Optional.of(order));

        assertThatIllegalStateException()
                .isThrownBy(() -> sut.startRoute(orderId))
                .withMessage("[OrderStatus is not DISPATCHED]");

        verify(repo).findById(orderId);
        verify(repo, never()).save(any());
    }


}