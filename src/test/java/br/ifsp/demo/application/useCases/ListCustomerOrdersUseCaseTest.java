package br.ifsp.demo.application.useCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.repository.CustomerRepository;
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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListCustomerOrdersUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderDeliveryRepository orderDeliveryRepository;

    @InjectMocks
    private ListCustomerOrdersUseCase sut;

    private Customer customer;
    private OrderDelivery orderDelivery;
    private OrderDelivery orderDispatched;
    private OrderDelivery orderEnRoute;
    private OrderDelivery orderConcluded;
    private OrderDelivery orderCanceled;

    @BeforeEach
    void setUp() {
        customer = new Customer("John Doe", CustomerType.REGULAR);

        DeliveryMan deliveryMan = new DeliveryMan("John Doe", 10);

        Address pickupAddress = new Address("Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000"));
        Address deliveryAddress = new Address("Street B", "11", "Center", "Araraquara", "SP", "Brasil", new Cep("13400-000"));

        orderDelivery = new OrderDelivery(customer, pickupAddress, deliveryAddress);

        orderDispatched = new OrderDelivery(customer, pickupAddress, deliveryAddress);
        orderDispatched.dispatch(deliveryMan);

        orderEnRoute = new OrderDelivery(customer, pickupAddress, deliveryAddress);
        orderEnRoute.startRoute();

        orderConcluded = new OrderDelivery(customer, pickupAddress, deliveryAddress);
        orderConcluded.concluded();

        orderCanceled = new OrderDelivery(customer, pickupAddress, deliveryAddress);
        orderCanceled.cancel();
    }

    @TDD
    @Test
    @DisplayName("[#52] Should return all the orders from a customer")
    void shouldReturnAllOrdersFromACustomer(){
        when(customerRepository.exists(customer)).thenReturn(true);
        when(orderDeliveryRepository.findAllByCustomer(customer)).thenReturn(List.of(orderDelivery, orderDispatched, orderEnRoute, orderConcluded, orderCanceled));

        List<OrderDelivery> result = sut.listAll(customer);

        assertThat(result).isNotEmpty().contains(orderDelivery);
    }

    @TDD
    @Test
    @DisplayName("[#54] Should return a empty list when the customer doesn't have any orders")
    void shouldReturnEmptyListWhenCustomerHasNoOrders(){
        when(customerRepository.exists(customer)).thenReturn(true);
        when(orderDeliveryRepository.findAllByCustomer(customer)).thenReturn(Collections.emptyList());

        List<OrderDelivery> result = sut.listAll(customer);

        assertThat(result).isEmpty();
    }
}
