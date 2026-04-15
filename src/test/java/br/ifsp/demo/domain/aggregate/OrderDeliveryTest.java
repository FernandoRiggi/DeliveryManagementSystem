package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.annotation.Functional;
import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.event.EventType;
import br.ifsp.demo.domain.event.OrderDeliveryEvent;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.domain.valueObject.CustomerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class OrderDeliveryTest {
    //Como cliente

    @Nested
    @DisplayName("Creation")
    class CreationTest {

        @TDD
        @Test
        @DisplayName("[#1]Should create order delivery with pickup and delivery addresses")
        void shouldCreateOrderDeliveryWithPickupAndDeliveryAddresses(){
            Address pickupAddress = new Address("Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000"));
            Address deliveryAddress = new Address("Street B", "11", "Center", "Araraquara", "SP", "Brasil", new Cep("13400-000"));
            Customer customer = new Customer("John Doe", CustomerType.REGULAR);
            OrderDelivery sut = new OrderDelivery(customer, pickupAddress, deliveryAddress);

            assertThat(sut.getPickupAddress()).isEqualTo(pickupAddress);
            assertThat(sut.getDeliveryAddress()).isEqualTo(deliveryAddress);
            assertThat(sut.getStatus()).isEqualTo(StatusOrder.CREATED);
        }

        @TDD
        @ParameterizedTest
        @MethodSource("invalidCustomers")
        @DisplayName("Should throw exception when customer is invalid")
        void shouldThrowExceptionWhenCustomerIsInvalid(String name, CustomerType type){
            Address pickupAddress = createValidPickupAddress();
            Address deliveryAddress = createValidDeliveryAddress();

            assertThatThrownBy(() -> {
                Customer sut = new Customer(name, type);
                new OrderDelivery(sut, pickupAddress, deliveryAddress);
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @TDD
        @ParameterizedTest
        @NullSource
        @DisplayName("Should throw null pointer exception when customer is null")
        void shouldThrowNullPointerExceptionWhenCustomerIsNull(Customer customer){
            Address pickupAddress = createValidPickupAddress();
            Address deliveryAddress = createValidDeliveryAddress();

            assertThatNullPointerException().isThrownBy(() -> {
                new OrderDelivery(customer, pickupAddress, deliveryAddress);
            });
        }

        @TDD
        @Test
        @DisplayName("Should throw null pointer exception when pickup address is null")
        void shouldThrowNullPointerExceptionWhenPickupAddressIsNull(){
            assertThatNullPointerException().isThrownBy(() -> {
                new OrderDelivery(createValidCustomer(), null, createValidDeliveryAddress());
            });
        }

        @TDD
        @Test
        @DisplayName("Should throw null pointer exception when delivery address is null")
        void shouldThrownNullPointerExceptionWhenDeliveryAddressIsNull(){
            assertThatNullPointerException().isThrownBy(() -> {
                new OrderDelivery(createValidCustomer(), createValidPickupAddress(), null);
            });
        }

        @TDD
        @ParameterizedTest
        @MethodSource("invalidAddresses")
        @DisplayName("[#3] Should throw exception when pickup address is invalid")
        void shouldThrowExceptionWhenPickupAddressIsInvalid(String street, String number, String neighborhood, String city, String state, String country, String cepValue){
            Address deliveryAddress = new Address("Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000"));

            assertThatThrownBy(() -> {
                Cep cep = cepValue == null ? null : new Cep(cepValue);
                Address pickupAddress = new Address(street, number, neighborhood, city, state, country, cep);
                new OrderDelivery(createValidCustomer() ,pickupAddress, deliveryAddress);
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @TDD
        @ParameterizedTest
        @MethodSource("invalidAddresses")
        @DisplayName("[#4] Should throw exception when delivery address is invalid")
        void shouldThrownExceptionWhenDeliveryAddressIsInvalid(String street, String number, String neighborhood, String city, String state, String country, String cepValue) {
            Address pickupAddress = new Address("Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000"));

            assertThatThrownBy(() -> {
                Cep cep = cepValue == null ? null : new Cep(cepValue);
                Address deliveryAddress = new Address(street, number, neighborhood, city, state, country, cep);
                new OrderDelivery(createValidCustomer(), pickupAddress, deliveryAddress);
            }).isInstanceOf(IllegalArgumentException.class);
        }

        private static Stream<Arguments> invalidAddresses() {
            return Stream.of(
                    arguments(null, "10", "Center", "São Carlos", "SP", "Brasil", "13500-000"),
                    arguments("", "10", "Center", "São Carlos", "SP", "Brasil", "13500-000"),

                    arguments("Street B", null, "Center", "São Carlos", "SP", "Brasil", "13500-000"),
                    arguments("Street B", "", "Center", "São Carlos", "SP", "Brasil", "13500-000"),

                    arguments("Street B", "10", null, "São Carlos", "SP", "Brasil", "13500-000"),
                    arguments("Street B", "10", "", "São Carlos", "SP", "Brasil", "13500-000"),

                    arguments("Street B", "10", "Center", null, "SP", "Brasil", "13500-000"),
                    arguments("Street B", "10", "Center", "", "SP", "Brasil", "13500-000"),

                    arguments("Street B", "10", "Center", "São Carlos", null, "Brasil", "13500-000"),
                    arguments("Street B", "10", "Center", "São Carlos", "", "Brasil", "13500-000"),

                    arguments("Street B", "10", "Center", "São Carlos", "SP", null, "13500-000"),
                    arguments("Street B", "10", "Center", "São Carlos", "SP", "", "13500-000"),

                    arguments("Street B", "10", "Center", "São Carlos", "SP", "Brasil", null),
                    arguments("Street B", "10", "Center", "São Carlos", "SP", "Brasil", ""),
                    arguments("Street B", "10", "Center", "São Carlos", "SP", "Brasil", "cep-invalido")
            );
        }

        private static Stream<Arguments> invalidCustomers() {
            return Stream.of(
                    arguments(null, CustomerType.REGULAR),
                    arguments("", CustomerType.REGULAR),
                    arguments(" ", CustomerType.REGULAR),
                    arguments("   ", CustomerType.REGULAR),
                    arguments("John Doe", null)
            );
        }
    }


    @Nested
    @DisplayName("Cancellation and dispatch")
    class LifecycleTest {
        private OrderDelivery order;
        private DeliveryMan deliveryMan;

        @BeforeEach
        void SetUp() {
            order = createValidOrder();
            deliveryMan = createValidDeliveryMan();
        }

        @TDD
        @Test
        @DisplayName("[#13] Given that an order has been created, when canceled, the status should be canceled.")
        void ShouldReturnStatusCanceledWhenCanceled() {
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(StatusOrder.CANCELED);
        }

        @TDD
        @Test
        @DisplayName("[#14] Given that an order has StatusOrder DISPATCHED, when canceled, the status should be canceled")
        void ShouldReturnStatusCanceledWhenCanceledOrderDispatched() {
            order.dispatch(deliveryMan);
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(StatusOrder.CANCELED);
        }

        @TDD
        @Test
        @DisplayName("[#15] Given that an order has StatusOrder CONCLUDED, when canceled," +
                " must throw an exception of type IllegalStateException.")
        void ShouldThrowIllegalStateExceptionWhenCanceledOrderConcluded() {
            order.concluded();
            assertThatIllegalStateException().isThrownBy(order::cancel);
        }

        @TDD
        @Test
        @DisplayName("[#16] Given that an order is canceled," +
                " the system should generate a CANCELLATION EventDelivery and record it in the Order Status.")
        void ShouldReturnCancellationEventDelivery() {
            order.cancel();
            boolean hasCanceledEvent = order.getEvents().stream()
                    .anyMatch(event -> event.getType() == EventType.CANCELLATION);
            assertThat(hasCanceledEvent).isTrue();
        }

        @TDD
        @Test
        @DisplayName("[#46] Canceling an order that has a cancellation event" +
                " should return an error and not change anything.")
        void ShouldReturnIllegalStateExceptionWhenOrderHasCancellationEvent() {
            order.cancel();
            assertThatIllegalStateException().isThrownBy(order::cancel);
        }

        @TDD
        @Test
        @DisplayName("[#47 Given that an order has StatusOrder EN_ROUTE, when canceled, the status should be canceled ]")
        void ShouldReturnStatusCanceledWhenCanceledOrderEnRoute() {
            order.startRoute();
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(StatusOrder.CANCELED);
        }

        //Como operador
        @TDD
        @Test
        @DisplayName("[#18] Given order CREATED and deliveryman, when dispatch, then status should be DISPATCHED")
        void shouldChangeStatusToDispatchedWhenDeliverymanAssociated() {
            order.dispatch(deliveryMan);

            assertThat(order.getStatus()).isEqualTo(StatusOrder.DISPATCHED);
            assertThat(order.getDeliveryman()).isEqualTo(deliveryMan);
        }

        @TDD
        @Test
        @DisplayName("[#19] Given order CANCELED, when dispatch, then should throw IllegalStateException")
        void ShouldThrowIllegalStateExceptionWhenDispatchCanceledOrder() {
            order.cancel();
            assertThatIllegalStateException().isThrownBy(() -> order.dispatch(deliveryMan));
        }

        @TDD
        @Test
        @DisplayName("[#20] Given deliveryman without sufficient capacity, when dispatch," +
                " then should throw IllegalStateException")
        void shouldThrowIllegalStateExceptionWhenDeliverymanHasInsufficientCapacity() {
            deliveryMan.setCapacity(0);
            assertThatIllegalStateException().isThrownBy(() -> order.dispatch(deliveryMan));
        }

        @TDD
        @Test
        @DisplayName("[#21] Given order dispatched successfully, then should generate DISPATCHED event")
        void ShouldGenerateDispatchedEventWhenOrderDispatched() {
            order.dispatch(deliveryMan);

            boolean hasDispatchedEvent = order.getEvents().stream()
                    .anyMatch(event -> event.getType() == EventType.DISPATCHED);
            assertThat(hasDispatchedEvent).isTrue();
        }

        @TDD
        @Test
        @DisplayName("[#24] Given order is DISPATCHED, when deliveryMan start route, then status should be EN_ROUTE")
        void ShouldChangeStatusToEnRouteWhenDeliveryManStartRoute() {
            order.dispatch(deliveryMan);
            order.startRoute();

            assertThat(order.getStatus()).isEqualTo(StatusOrder.EN_ROUTE);
        }

        @Test
        @DisplayName("Should generate EN_ROUTE event when route starts")
        void shouldGenerateEnRouteEventWhenRouteStarts() {
            order.dispatch(deliveryMan);
            order.startRoute();

            assertThat(order.getEvents())
                    .anyMatch(event -> event.getType() == EventType.EN_ROUTE);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when order is already EN_ROUTE")
        void shouldThrowIllegalStateExceptionWhenOrderIsAlreadyEnRoute(){
            order.dispatch(deliveryMan);
            order.startRoute();

            assertThatIllegalStateException().isThrownBy(() -> order.startRoute());
        }

        @TDD
        @Test
        @DisplayName("[#25] Given order is EN_ROUTE, when deliveryMan confirm deliver, then status should be CONCLUDED")
        void shouldChangeStatusToConcludedWhenDeliveryManConfirmDeliver(){
            order.dispatch(deliveryMan);
            order.startRoute();
            order.concluded();

            assertThat(order.getEvents()).anyMatch(event -> event.getType() == EventType.CONCLUDED);
        }

        @Test
        @DisplayName("Should generate CONCLUDED event when order is delivered")
        void shouldGenerateConcludedEventWhenOrderIsDelivered(){
            order.dispatch(deliveryMan);
            order.startRoute();
            order.concluded();

            assertThat(order.getEvents())
                    .anyMatch(event -> event.getType() == EventType.CONCLUDED);
        }

        @TDD
        @Test
        @DisplayName("[#26] Should throw IllegalStateException when order is already CONCLUDED")
        void shouldThrowIllegalStateExceptionWhenOrderIsAlreadyCONCLUDED(){
            order.dispatch(deliveryMan);
            order.startRoute();
            order.concluded();

            assertThatIllegalStateException().isThrownBy(() -> order.concluded());
        }

        @TDD
        @Test
        @DisplayName("[#39] Given order DISPATCHED, when dispatch again, then should throw IllegalStateException")
        void shouldThrowIllegalStateExceptionWhenDispatchAlreadyDispatchedOrder() {
            order.dispatch(deliveryMan);

            assertThatIllegalStateException().isThrownBy(() -> order.dispatch(deliveryMan));
        }

        @TDD
        @Test
        @DisplayName("[#40] Given order CONCLUDED, when dispatch, then should throw IllegalStateException")
        void shouldThrowIllegalStateExceptionWhenDispatchConcludedOrder() {
            order.concluded();

            assertThatIllegalStateException().isThrownBy(() -> order.dispatch(deliveryMan));
        }

        @TDD
        @Test
        @DisplayName("[#41] Given order CREATED and no deliveryman available, when dispatch, then should throw IllegalStateException")
        void shouldThrowIllegalStateExceptionWhenNoDeliverymanAvailable() {
            assertThatIllegalStateException().isThrownBy(() -> order.dispatch(null));
        }

        @Functional
        @Test
        @DisplayName("[VL] Canceled order must have at minimum 2 events: CREATED and CANCELLATION")
        void canceledOrderMustHaveMinimumTwoEvents() {
            order.cancel();
            assertThat(order.getEvents().size()).isEqualTo(2);
            assertThat(order.getEvents()).extracting(OrderDeliveryEvent::getType)
                    .containsExactly(EventType.CREATED, EventType.CANCELLATION);
        }

        @Functional
        @Test
        @DisplayName("[VL] Order must have maximum 4 events: CREATED, DISPATCHED, EN_ROUTE and CONCLUDED or" +
                " CREATED, DISPATCHED, EN_ROUTE AND CANCELLATION")
        void MustContainMaximumOfFourEventsInAnOrder() {
            order.dispatch(deliveryMan);
            order.startRoute();
            order.cancel();

            List<EventType> eventsTypes = order.getEvents()
                    .stream()
                    .map(OrderDeliveryEvent::type)
                    .toList();

            assertThat(eventsTypes.size()).isEqualTo(4);

            boolean sequenceValid = eventsTypes.equals(List.of(
                    EventType.CREATED,
                    EventType.DISPATCHED,
                    EventType.EN_ROUTE,
                    EventType.CONCLUDED)
            ) || eventsTypes.equals(List.of(
                    EventType.CREATED,
                    EventType.DISPATCHED,
                    EventType.EN_ROUTE,
                    EventType.CANCELLATION));

            assertThat(sequenceValid).isTrue();
        }
    }

    @Nested
    @DisplayName("Event history")
    class EventHistoryTest {
        private OrderDelivery order;
        private DeliveryMan deliveryMan;

        @BeforeEach
        void setUp() {
            order = createValidOrder();
            deliveryMan = createValidDeliveryMan();
        }

        @TDD
        @Test
        @DisplayName("[#27] Given order is created, when the system registers the creation, then CREATED event should be added to history")
        void shouldAddCreatedEventToHistoryWhenOrderIsCreated() {
            assertThat(order.getEvents())
                    .extracting(OrderDeliveryEvent::getType)
                    .containsExactly(EventType.CREATED);
        }
    }

    private OrderDelivery createValidOrder(){
        Address pickupAddress = new Address("Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000"));
        Address deliveryAddress = new Address("Street B", "11", "Center", "Araraquara", "SP", "Brasil", new Cep("13400-000"));
        Customer customer = new Customer("John Doe", CustomerType.REGULAR);
        return new OrderDelivery(customer, pickupAddress, deliveryAddress);
    }

    private DeliveryMan createValidDeliveryMan(){
        return new DeliveryMan("John Doe", 10);
    }

    private Customer createValidCustomer(){
        return new Customer("John Doe", CustomerType.REGULAR);
    }

    private Address createValidPickupAddress() {
        return new Address("Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000"));
    }

    private Address createValidDeliveryAddress() {
        return new Address("Street B", "11", "Center", "Araraquara", "SP", "Brasil", new Cep("13400-000"));
    }
}
