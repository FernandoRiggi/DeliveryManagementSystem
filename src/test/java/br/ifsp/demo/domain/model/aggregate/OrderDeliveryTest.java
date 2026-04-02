package br.ifsp.demo.domain.model.aggregate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.tracing.Status;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderDeliveryTest {
    @Test
    @DisplayName("Given that an order has been created, when canceled, the status should be canceled.")
    void ShouldReturnStatusCanceledWhenCanceled() {
        OrderDelivery order = new OrderDelivery();
        order.canceled();
        assertThat(order.getStatus()).isEqualTo(StatusOrder.CANCELED);
    }
}
