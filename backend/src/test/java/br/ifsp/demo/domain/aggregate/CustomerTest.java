package br.ifsp.demo.domain.aggregate;

import br.ifsp.demo.annotation.Mutation;
import br.ifsp.demo.domain.valueObject.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class CustomerTest {
    @Mutation
    @Test
    @DisplayName("[Mutation] Should reconstitute customer with same data")
    void shouldReconstituteCustomerWithSameData() {
        UUID customerId = UUID.randomUUID();
        String name = "John Doe";
        CustomerType type = CustomerType.REGULAR;

        Customer customer = Customer.reconstitute(customerId, name, type);

        assertThat(customer).isNotNull();
        assertThat(customer.getCustomerId()).isEqualTo(customerId);
        assertThat(customer.getName()).isEqualTo(name);
        assertThat(customer.getType()).isEqualTo(type);
    }
}