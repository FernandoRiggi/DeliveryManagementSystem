package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.annotation.Mutation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class DeliveryManTest {

    @Mutation
    @Test
    @DisplayName("[Mutation] Should reconstitute delivery man with same data")
    void shouldReconstituteDeliveryManWithSameData() {
        UUID deliveryManId = UUID.randomUUID();
        String name = "John Doe";
        int capacity = 3;

        DeliveryMan deliveryMan = DeliveryMan.reconstitute(deliveryManId, name, capacity);

        assertThat(deliveryMan).isNotNull();
        assertThat(deliveryMan.getId()).isEqualTo(deliveryManId);
        assertThat(deliveryMan.getName()).isEqualTo(name);
        assertThat(deliveryMan.getCapacity()).isEqualTo(capacity);
    }
}