package br.ifsp.demo.infrastructure.persistence.repository;

import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.infrastructure.persistence.entity.AddressEmbeddable;
import br.ifsp.demo.infrastructure.persistence.entity.CustomerEntity;
import br.ifsp.demo.infrastructure.persistence.entity.OrderDeliveryEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("PersistenceTest")
@Tag("IntegrationTest")
class OrderDeliveryJpaRepositoryPersistenceTest {

    private static final UUID SEEDED_CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private OrderDeliveryJpaRepository orderRepository;

    @Autowired
    private CustomerJpaRepository customerRepository;

    private final List<UUID> createdOrderIds = new ArrayList<>();

    @AfterEach
    void cleanOrders() {
        createdOrderIds.forEach(orderRepository::deleteById);
        createdOrderIds.clear();
    }

    @Test
    @DisplayName("findAllActiveOrders should return only orders that are not canceled or concluded")
    void findAllActiveOrdersShouldReturnOnlyOrdersThatAreNotCanceledOrConcluded() {
        CustomerEntity customer = customerRepository.findById(SEEDED_CUSTOMER_ID).orElseThrow();
        OrderDeliveryEntity createdOrder = saveOrder(customer, StatusOrder.CREATED);
        OrderDeliveryEntity dispatchedOrder = saveOrder(customer, StatusOrder.DISPATCHED);
        OrderDeliveryEntity enRouteOrder = saveOrder(customer, StatusOrder.EN_ROUTE);
        OrderDeliveryEntity canceledOrder = saveOrder(customer, StatusOrder.CANCELED);
        OrderDeliveryEntity concludedOrder = saveOrder(customer, StatusOrder.CONCLUDED);

        List<OrderDeliveryEntity> activeOrders = orderRepository.findAllActiveOrders(
                customer,
                List.of(StatusOrder.CANCELED, StatusOrder.CONCLUDED)
        );

        List<UUID> activeOrderIds = activeOrders.stream()
                .map(OrderDeliveryEntity::getId)
                .toList();

        assertThat(activeOrderIds)
                .contains(createdOrder.getId(), dispatchedOrder.getId(), enRouteOrder.getId())
                .doesNotContain(canceledOrder.getId(), concludedOrder.getId());
        assertThat(activeOrders)
                .extracting(OrderDeliveryEntity::getStatusOrder)
                .doesNotContain(StatusOrder.CANCELED, StatusOrder.CONCLUDED);
    }

    private OrderDeliveryEntity saveOrder(CustomerEntity customer, StatusOrder status) {
        OrderDeliveryEntity order = new OrderDeliveryEntity();
        order.setId(UUID.randomUUID());
        order.setCustomer(customer);
        order.setStatusOrder(status);
        order.setDistanceKm(10.0);
        order.setPickupAddress(address());
        order.setDeliveryAddress(address());

        OrderDeliveryEntity savedOrder = orderRepository.save(order);
        createdOrderIds.add(savedOrder.getId());
        return savedOrder;
    }

    private AddressEmbeddable address() {
        AddressEmbeddable address = new AddressEmbeddable();
        address.setStreet("Persistence Test Street");
        address.setNumber("100");
        address.setNeighborhood("Center");
        address.setCity("Sao Carlos");
        address.setState("SP");
        address.setCountry("Brasil");
        address.setCep("13500-000");
        return address;
    }
}
