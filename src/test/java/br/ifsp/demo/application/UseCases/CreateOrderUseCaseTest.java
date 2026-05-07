package br.ifsp.demo.application.UseCases;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.dto.CreateOrderRequest;
import br.ifsp.demo.domain.repository.CustomerRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateOrderUseCaseTest {

    @Mock
    private OrderDeliveryRepository repo;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    CreateOrderUseCase sut;

    @TDD
    @Test
    @DisplayName("[#56] Should create and save order delivery")
    void ShouldCreateAndSaveOrderDelivery(){
        UUID customerId = UUID.randomUUID();
        Customer customer = createValidCustomer();
        CreateOrderRequest request = new CreateOrderRequest(customerId, createValidPickupAddress(), createValidDeliveryAddress(), 10.0);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        sut.create(request);

        verify(repo, times(1)).save(any(OrderDelivery.class));
    }

    @TDD
    @Test
    @DisplayName("[#3] Should not save order when pickup address is invalid")
    void shouldNotSaveOrderWhenPickupAddressIsInvalid() {
        UUID customerId = UUID.randomUUID();
        Customer customer = createValidCustomer();
        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                null,
                createValidDeliveryAddress(),
                10.0
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(NullPointerException.class);

        verify(repo, never()).save(any(OrderDelivery.class));
    }

    @TDD
    @Test
    @DisplayName("[#4] Should not save order when delivery address is invalid")
    void shouldNotSaveOrderWhenDeliveryAddressIsInvalid(){
        UUID customerId = UUID.randomUUID();
        Customer customer = createValidCustomer();
        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                createValidPickupAddress(),
                null,
                10.0
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));


        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(NullPointerException.class);

        verify(repo, never()).save(any(OrderDelivery.class));
    }

    @TDD
    @Test
    @DisplayName("[#44] Should throw exception when customer is not found")
    void shouldThrowExceptionWhenCustomerIsNotFound(){
        UUID customerId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                createValidPickupAddress(),
                createValidDeliveryAddress(),
                10.0
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer not found");

        verify(customerRepository, times(1)).findById(customerId);
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
