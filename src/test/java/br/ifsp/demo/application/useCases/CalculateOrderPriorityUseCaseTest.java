package br.ifsp.demo.application.useCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.PriorityLevel;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.domain.valueObject.CustomerType;
import br.ifsp.demo.domain.valueObject.LogisticScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculateOrderPriorityUseCaseTest {


    private Customer regularCustomer;
    private Customer businessCustomer;
    private Customer premiumCustomer;
    private Address pickupAddress;
    private Address deliveryAddress;
    private OrderDelivery order;

    @BeforeEach
    void setUp() {
        regularCustomer = new Customer("John Doe", CustomerType.REGULAR);
        businessCustomer = new Customer("John Doe", CustomerType.BUSINESS);
        premiumCustomer = new Customer("John Doe", CustomerType.PREMIUM);

        pickupAddress = new Address("Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000"));
        deliveryAddress = new Address("Street B", "11", "Center", "Araraquara", "SP", "Brasil", new Cep("13400-000"));

    }

    @Mock
    OrderDeliveryRepository orderRepository;
    @InjectMocks
    CalculateOrderPriorityUseCase sut;


    @TDD
    @Test
    @DisplayName("[#30] UseCase: Regular, 0 active orders in the repository, 8Km, 10Min")
    void shouldReturnNormalForRegularCustomerWithNoOrders(){
        OrderDelivery order = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 8.0);
        when(orderRepository.findAllActiveOrders(regularCustomer)).thenReturn(List.of());

        LogisticScore score = sut.calculate(order,10);

        assertThat(score.getPriorityLevel()).isEqualTo(PriorityLevel.NORMAL);
    }

    @TDD
    @Test
    @DisplayName("Should throw NullPointerException when order is null")
    void shouldThrowWhenOrderIsNull() {
        assertThatThrownBy(() -> sut.calculate(null, 10))
                .isInstanceOf(NullPointerException.class);
    }

    @TDD
    @Test
    @DisplayName("Should throw IllegalStateException when order is not CREATED")
    void shouldThrowWhenOrderIsNotCreated() {
        OrderDelivery dispatched = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 8.0);

        dispatched.dispatch(new DeliveryMan("Carlos", 5));

        assertThatThrownBy(() -> sut.calculate(dispatched, 10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CREATED");
    }

    @TDD
    @Test
    @DisplayName("[#37] Order with higher score should come firts in the queue")
    void orderWithHigherScoreShouldComeFirstInQueue() {
        OrderDelivery order1 = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 8.0);
        OrderDelivery order2 = new OrderDelivery(premiumCustomer, pickupAddress, deliveryAddress, 3.0);

        when(orderRepository.findAllActiveOrders(regularCustomer)).thenReturn(List.of(order1));
        when(orderRepository.findAllActiveOrders(premiumCustomer)).thenReturn(List.of(order2));

        List<OrderDelivery> queue = sut.getPriorityQueue();

        assertThat(queue).first().isEqualTo(order2);
    }

    @TDD
    @Test
    @DisplayName("[#37] Should not appear dispatched orders in the queue")
    void shouldNotAppearDispatchedOrdersInQueue() {

        OrderDelivery createdOrder = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 8.0);
        OrderDelivery dispatchedOrder = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 8.0);
        dispatchedOrder.dispatch(new DeliveryMan("Carlos", 5));

        when(orderRepository.findAllActiveOrders(regularCustomer)).thenReturn(List.of(createdOrder));

        List<OrderDelivery> queue = sut.getPriorityQueue();

        assertThat(queue).containsOnly(createdOrder);
    }
}








