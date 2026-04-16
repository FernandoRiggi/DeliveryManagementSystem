package br.ifsp.demo.application.useCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.domain.valueObject.CustomerType;
import br.ifsp.demo.domain.valueObject.LogisticScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GetPriorityQueueUseCaseTest {

    private Customer regularCustomer;
    private Customer businessCustomer;
    private Customer premiumCustomer;
    private Address pickupAddress;
    private Address deliveryAddress;

    @BeforeEach
    void setUp() {

        regularCustomer = new Customer("John Doe", CustomerType.REGULAR);
        businessCustomer = new Customer("John Doe", CustomerType.BUSINESS);
        premiumCustomer = new Customer("John Doe", CustomerType.PREMIUM);
        pickupAddress = new Address("Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000"));
        deliveryAddress = new Address("Street B", "11", "Center", "Araraquara", "SP", "Brasil", new Cep("13400-000"));
    }


    @TDD
    @Test
    @DisplayName("[#22] Should sort orders by score")
    void shouldSortOrdersByScore() {

        OrderDelivery normal = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 10.0);
        OrderDelivery normalHigher = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 20.0);
        OrderDelivery urgent = new OrderDelivery(businessCustomer, pickupAddress, deliveryAddress, 5.0);
        OrderDelivery critical = new OrderDelivery(premiumCustomer, pickupAddress, deliveryAddress, 2.0);

        Map<OrderDelivery, LogisticScore> score = Map.of(
                normal, new LogisticScore(10),
                normalHigher, new LogisticScore(20),
                urgent, new LogisticScore(5),
                critical, new LogisticScore(2));

        List<OrderDelivery> result = sut.execute(List.of(normal, normalHigher, urgent, critical), score);

        assertThat(result).containsExactly(critical, urgent, normalHigher, normal);
    }

}