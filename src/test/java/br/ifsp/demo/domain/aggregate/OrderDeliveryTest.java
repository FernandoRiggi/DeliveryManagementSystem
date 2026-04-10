package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.annotation.Functional;
import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.event.EventType;
import br.ifsp.demo.domain.event.OrderDeliveryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class OrderDeliveryTest {
    private OrderDelivery order;
    private DeliveryMan deliveryMan;


    @BeforeEach
    void SetUp(){
        order = new OrderDelivery();
        deliveryMan = new DeliveryMan("John Doe", 10);
    }

    //Como cliente
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
    void  ShouldReturnIllegalStateExceptionWhenOrderHasCancellationEvent() {
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
        assertThatIllegalStateException().isThrownBy(()-> order.dispatch(deliveryMan));
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


}
