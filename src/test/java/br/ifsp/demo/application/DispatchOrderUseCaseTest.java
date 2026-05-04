package br.ifsp.demo.application;
import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.DeliveryManRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.domain.valueObject.CustomerType;
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
                .thenReturn(Optional.of(createValidOrder()));
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

    private OrderDelivery createValidOrder(){
        Address pickupAddress = new Address("Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000"));
        Address deliveryAddress = new Address("Street B", "11", "Center", "Araraquara", "SP", "Brasil", new Cep("13400-000"));
        Customer customer = new Customer("John Doe", CustomerType.REGULAR);

        return new OrderDelivery(customer, pickupAddress, deliveryAddress,10.0);
    }

    private DeliveryMan createValidDeliveryMan(){
        return new DeliveryMan("John Doe", 10);
    }
}