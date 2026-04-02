package br.ifsp.demo.domain.model.aggregate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.tracing.Status;

import static org.assertj.core.api.Assertions.*;

public class OrderDeliveryTest {
    @Test
    @DisplayName("[#13] Given that an order has been created, when canceled, the status should be canceled.")
    void ShouldReturnStatusCanceledWhenCanceled() {
        OrderDelivery order = new OrderDelivery(StatusOrder.CREATED);
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(StatusOrder.CANCELED);
    }

    @Test
    @DisplayName("[#14] Given that an order has StatusOrder DISPATCHED, when canceled, the status should be canceled")
    void ShouldReturnStatusCanceledWhenCanceledOrderDispatched() {
        OrderDelivery order = new OrderDelivery(StatusOrder.CREATED);
        order.setStatusOrder(StatusOrder.DISPATCHED);
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(StatusOrder.CANCELED);
    }

    @Test
    @DisplayName("[#15] Given that an order has StatusOrder CONCLUDED, when canceled," +
            " must throw an exception of type IllegalStateException.")
    void ShouldThrowIllegalStateExceptionWhenCanceledOrderConcluded() {
        OrderDelivery order = new OrderDelivery(StatusOrder.CREATED);
        order.setStatusOrder(StatusOrder.CONCLUDED);
        order.cancel();
        assertThatIllegalStateException().isThrownBy(order::cancel);

    }
}
