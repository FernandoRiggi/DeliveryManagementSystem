package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.domain.valueObject.CustomerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class StartDeliveryUseCaseTest {
    @Mock
    private OrderDeliveryRepository orderRepository;

    @InjectMocks
    private StartDeliveryUseCase sut;

    private Customer customer;
    private Address pickupAddress;
    private Address deliverydAddress;

    @BeforeEach
    void setup() {
        customer = new Customer("John", CustomerType.REGULAR);

        pickupAddress = new Address("A", "1", "Centro", "São Carlos", "SP", "Brasil", new Cep("13500-000"));
        deliverydAddress = new Address("B", "2", "Centro", "Araraquara", "SP", "Brasil", new Cep("13500-000"));
    }

    @Test
    @DisplayName("Should start delivery when order is dispatched")
    void shouldStartDeliveryWhenOrderIsDispatched() {

        OrderDelivery order = new OrderDelivery(customer, pickupAddress, deliverydAddress, 5.0);
        order.dispatch(new DeliveryMan("Carlos", 5));

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        sut.startRoute(order.getId());

        assertThat(order.getStatus()).isEqualTo(StatusOrder.EN_ROUTE);
    }
}
