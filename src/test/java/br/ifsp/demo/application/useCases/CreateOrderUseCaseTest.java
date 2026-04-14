package br.ifsp.demo.application.useCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.dto.CreateOrderRequest;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;
import br.ifsp.demo.domain.valueObject.Address;
import br.ifsp.demo.domain.valueObject.Cep;
import br.ifsp.demo.domain.valueObject.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateOrderUseCaseTest {

    @Mock
    private OrderDeliveryRepository repo;

    @InjectMocks CreateOrderUseCase sut;

    @TDD
    @Test
    @DisplayName("[#56] Should create and save order delivery")
    void ShouldCreateAndSaveOrderDelivery(){
        CreateOrderRequest request = new CreateOrderRequest(createValidCustomer(), createValidPickupAddress(), createValidDeliveryAddress());

        sut.create(request);

        verify(repo, times(1)).save(any(OrderDelivery.class));
    }

    @TDD
    @Test
    @DisplayName("[#3] Should not save order when pickup address is invalid")
    void shouldNotSaveOrderWhenPickupAddressIsInvalid() {
        CreateOrderRequest request = new CreateOrderRequest(
                createValidCustomer(),
                null,
                createValidDeliveryAddress()
        );

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(NullPointerException.class);

        verify(repo, never()).save(any(OrderDelivery.class));
    }

    private DeliveryMan createValidDeliveryMan(){
        return new DeliveryMan("John Doe", 10);
    }

    private Customer createValidCustomer(){
        return new Customer("John Doe", CustomerType.REGULAR);
    }

    private Address createValidPickupAddress() {
        return new Address("Street A", "10", "Center", "São Carlos", "SP", "Brasil", new Cep("13500-000"));
    }

    private Address createValidDeliveryAddress() {
        return new Address("Street B", "11", "Center", "Araraquara", "SP", "Brasil", new Cep("13400-000"));
    }

}
