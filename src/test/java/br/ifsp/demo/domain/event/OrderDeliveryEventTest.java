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

    @Mutation
    @Test
    @DisplayName("[Mutation] Should create event with fixed datetime")
    void shouldCreateEventWithFixedDateTime() {
        EventType type = EventType.CREATED;
        LocalDateTime dateTime = LocalDateTime.of(2026, 5, 11, 10, 30);

        OrderDeliveryEvent event = new OrderDeliveryEvent(type, dateTime);

        assertThat(event.getType()).isEqualTo(type);
        assertThat(event.getDateTime()).isEqualTo(dateTime);
    }
}