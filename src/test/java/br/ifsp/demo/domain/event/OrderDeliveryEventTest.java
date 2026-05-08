package br.ifsp.demo.domain.event;

import br.ifsp.demo.annotation.Mutation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDeliveryEventTest {
    @Mutation
    @Test
    @DisplayName("[Mutation] Should return non-null when reconstituting an event")
    void shouldReturnEventWhenReconstituted() {
        LocalDateTime dateTime = LocalDateTime.now();
        OrderDeliveryEvent event = OrderDeliveryEvent.reconstitute(1L, EventType.CREATED, dateTime);
        assertThat(event).isNotNull();
        assertThat(event.getType()).isEqualTo(EventType.CREATED);
    }
}