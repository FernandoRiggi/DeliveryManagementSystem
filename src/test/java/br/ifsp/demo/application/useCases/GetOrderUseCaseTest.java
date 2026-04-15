package br.ifsp.demo.application.useCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.domain.valueObject.CustomerType;
import br.ifsp.demo.exception.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
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
    void shouldReturnCurrentStatusWhenGettingOrderById() {
        OrderDelivery order = mock(OrderDelivery.class);
        UUID orderId = UUID.randomUUID();

        when(orderDeliveryRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getStatus()).thenReturn(StatusOrder.DISPATCHED);

        OrderDelivery response = sut.findById(orderId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(StatusOrder.DISPATCHED);

        verify(orderDeliveryRepository).findById(orderId);
        verify(order).getStatus();
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order id not found")
    void shouldThrowOrderNotFoundWhenOrderIdNotFound() {
        UUID idNotFound = UUID.randomUUID();
        when(orderDeliveryRepository.findById(idNotFound)).thenReturn(Optional.empty());

        assertThatExceptionOfType(OrderNotFoundException.class).isThrownBy(() -> sut.findById(idNotFound));
        verify(orderDeliveryRepository, never()).save(any());
    }

    @TDD
    @Test
    @DisplayName("Should return all orders by customer")
    void shouldReturnAllOrdersByCustomer() {
        Customer customer = new Customer("John Doe", CustomerType.REGULAR);

        OrderDelivery order1 = mock(OrderDelivery.class);
        OrderDelivery order2 = mock(OrderDelivery.class);

        List<OrderDelivery> orders = List.of(order1, order2);

        when(orderDeliveryRepository.findAllByCustomer(customer)).thenReturn(orders);

        List<OrderDelivery> response = sut.findAllOrdersByCustomer(customer);

        assertThat(response).isNotNull();
        assertThat(response).hasSize(2);
        assertThat(response).containsExactly(order1, order2);

        verify(orderDeliveryRepository).findAllByCustomer(customer);
    }
    @TDD
    @Test
    @DisplayName("Should throw OrderNotFoundException when customer has no orders")
    void shouldThrowOrderNotFoundExceptionWhenCustomerHasNoOrders() {
        Customer customer = new Customer("John Doe", CustomerType.REGULAR);

        when(orderDeliveryRepository.findAllByCustomer(customer)).thenReturn(List.of());

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> sut.findAllOrdersByCustomer(customer))
                .withMessage("[Orders not found]");

        verify(orderDeliveryRepository).findAllByCustomer(customer);
    }

    @TDD
    @Test
    @DisplayName("Should throw NullPointerException when customer is null")
    void shouldThrowNullPointerExceptionWhenCustomerIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> sut.findAllOrdersByCustomer(null))
                .withMessage("Customer cannot be null");

        verify(orderDeliveryRepository, never()).findAllByCustomer(any());
    }

}
