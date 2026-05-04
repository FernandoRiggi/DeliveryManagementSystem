package br.ifsp.demo.application;

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
import java.util.Map;

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

        List<OrderDelivery> queue = sut.getPriorityQueue(List.of(order1, order2), Map.of(order1, 10, order2, 60));

        assertThat(queue).first().isEqualTo(order2);
    }

    @TDD
    @Test
    @DisplayName("[#37] Should not appear dispatched orders in the queue")
    void shouldNotAppearDispatchedOrdersInQueue() {

        OrderDelivery createdOrder = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 8.0);
        OrderDelivery dispatchedOrder = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 8.0);
        dispatchedOrder.dispatch(new DeliveryMan("Carlos", 5));

        List<OrderDelivery> queue = sut.getPriorityQueue(List.of(createdOrder, dispatchedOrder), Map.of(createdOrder, 10, dispatchedOrder, 10));

        assertThat(queue).containsOnly(createdOrder);
    }

    @TDD
    @Test
    @DisplayName("[#49] New order increases created count then recalculates existing scores")
    void newOrderShouldReduceExistingScores() {
        OrderDelivery existing = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 10.0);
        OrderDelivery newOrder = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 10.0);

        when(orderRepository.findAllActiveOrders(regularCustomer)).thenReturn(List.of(existing)).thenReturn(List.of(existing, newOrder));

        LogisticScore before = sut.calculate(existing, 10);

        List<LogisticScore> after = sut.recalculateQueue(newOrder, Map.of(existing, 10, newOrder, 10));

        assertThat(after.getFirst().value()).isLessThanOrEqualTo(before.value());
    }

    @TDD
    @Test
    @DisplayName("[#50] Cancelling an order decreases created count then recalculates existing scores")
    void cancelOrderShouldReduceExistingScores() {
        OrderDelivery existing = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 10.0);
        OrderDelivery newOrder = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 10.0);

        when(orderRepository.findAllActiveOrders(regularCustomer)).thenReturn(List.of(existing)).thenReturn(List.of(existing, newOrder));

        LogisticScore before = sut.calculate(existing, 10);

        List<LogisticScore> after = sut.recalculateQueue(newOrder, Map.of(existing, 10, newOrder, 10));

        assertThat(after.getFirst().value()).isLessThanOrEqualTo(before.value());
    }
}








