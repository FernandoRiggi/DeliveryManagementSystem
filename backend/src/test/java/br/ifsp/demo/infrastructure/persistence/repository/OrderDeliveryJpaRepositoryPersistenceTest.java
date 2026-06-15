package br.ifsp.demo.infrastructure.persistence.repository;

import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.event.EventType;
import br.ifsp.demo.infrastructure.persistence.entity.AddressEmbeddable;
import br.ifsp.demo.infrastructure.persistence.entity.CustomerEntity;
import br.ifsp.demo.infrastructure.persistence.entity.OrderDeliveryEntity;
import br.ifsp.demo.infrastructure.persistence.entity.OrderDeliveryEventEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Tag("PersistenceTest")
@Tag("IntegrationTest")
class OrderDeliveryJpaRepositoryPersistenceTest {

    private static final UUID SEEDED_CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private OrderDeliveryJpaRepository orderRepository;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Autowired
    private EntityManager entityManager;

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

    @Test
    @DisplayName("save should fail when order has no customer")
    void saveShouldFailWhenOrderHasNoCustomer() {
        OrderDeliveryEntity order = validOrder(null, StatusOrder.CREATED);

        assertThatThrownBy(() -> orderRepository.saveAndFlush(order))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("customer");
    }

    @Test
    @DisplayName("save should fail when required pickup address field is missing")
    void saveShouldFailWhenRequiredPickupAddressFieldIsMissing() {
        CustomerEntity customer = customerRepository.findById(SEEDED_CUSTOMER_ID).orElseThrow();
        OrderDeliveryEntity order = validOrder(customer, StatusOrder.CREATED);
        order.getPickupAddress().setStreet(null);

        assertThatThrownBy(() -> orderRepository.saveAndFlush(order))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("pickup");
    }

    @Test
    @DisplayName("save should cascade order events")
    void saveShouldCascadeOrderEvents() {
        CustomerEntity customer = customerRepository.findById(SEEDED_CUSTOMER_ID).orElseThrow();
        OrderDeliveryEntity order = validOrder(customer, StatusOrder.CREATED);
        OrderDeliveryEventEntity event = new OrderDeliveryEventEntity();
        event.setType(EventType.CREATED);
        event.setDateTime(LocalDateTime.now());
        order.getOrderEvents().add(event);

        OrderDeliveryEntity savedOrder = orderRepository.saveAndFlush(order);
        createdOrderIds.add(savedOrder.getId());

        Number eventCount = (Number) entityManager
                .createNativeQuery("select count(*) from order_delivery_event where order_delivery_id = :orderId")
                .setParameter("orderId", savedOrder.getId())
                .getSingleResult();

        assertThat(eventCount.longValue()).isEqualTo(1L);
    }

    private OrderDeliveryEntity saveOrder(CustomerEntity customer, StatusOrder status) {
        OrderDeliveryEntity order = validOrder(customer, status);
        OrderDeliveryEntity savedOrder = orderRepository.save(order);
        createdOrderIds.add(savedOrder.getId());
        return savedOrder;
    }

    private OrderDeliveryEntity validOrder(CustomerEntity customer, StatusOrder status) {
        OrderDeliveryEntity order = new OrderDeliveryEntity();
        order.setId(UUID.randomUUID());
        order.setCustomer(customer);
        order.setStatusOrder(status);
        order.setDistanceKm(10.0);
        order.setPickupAddress(address());
        order.setDeliveryAddress(address());
        return order;
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
