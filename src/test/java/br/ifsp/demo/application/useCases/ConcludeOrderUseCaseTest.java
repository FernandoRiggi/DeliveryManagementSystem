package br.ifsp.demo.application.useCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConcludeOrderUseCaseTest {

    @Mock
    private OrderDeliveryRepository repo;

    @InjectMocks
    private ConcludeOrderUseCase sut;

    @Test
    @DisplayName("Should conclude order when order is EN_ROUTE")
    void shouldConcludeOrderWhenOrderIsEnRoute() {
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
        order.startRoute();

        when(repo.findById(orderId)).thenReturn(Optional.of(order));

        sut.conclude(orderId);

        assertThat(order.getStatus()).isEqualTo(StatusOrder.CONCLUDED);
        assertThat(order.getEvents())
                .extracting(event -> event.getType())
                .contains(br.ifsp.demo.domain.event.EventType.CONCLUDED);

        verify(repo).findById(orderId);
        verify(repo).save(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();

        when(repo.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.conclude(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("[OrderDelivery Not Found]");

        verify(repo).findById(orderId);
        verify(repo, never()).save(any());
    }

}