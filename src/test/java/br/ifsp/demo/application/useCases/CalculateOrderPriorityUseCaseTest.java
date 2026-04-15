package br.ifsp.demo.application.useCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.PriorityLevel;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.domain.valueObject.CustomerType;
import br.ifsp.demo.domain.valueObject.LogisticScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.when;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CalculateOrderPriorityUseCaseTest {


    @TDD
    @Test
    @DisplayName("[#30] UseCase: Regular, 0 active orders in the repository, 8Km, 10Min")
    void shouldReturnNormalForRegularCustomerWithNoOrders(){
        OrderDelivery order = new OrderDelivery(regularCustomer, pickupAddress, deliveryAddress, 8.0);
        when(orderRepository.findAllActiveByCustomer(regularCustomer)).thenReturn(List.of(order));

        LogisticScore score = sut.calculate(order,10);

        assertThat(score.GetPriorityLevel()).isEqualTo(PriorityLevel.NORMAL);
    }
}
