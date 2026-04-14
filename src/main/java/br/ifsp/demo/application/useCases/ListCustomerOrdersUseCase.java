package br.ifsp.demo.application.useCases;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.OrderDelivery;
import br.ifsp.demo.domain.aggregate.StatusOrder;
import br.ifsp.demo.domain.repository.CustomerRepository;
import br.ifsp.demo.domain.repository.OrderDeliveryRepository;

import java.util.List;

public class ListCustomerOrdersUseCase {
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final CustomerRepository customerRepository;

    public ListCustomerOrdersUseCase(OrderDeliveryRepository orderDeliveryRepository, CustomerRepository customerRepository) {
        this.orderDeliveryRepository = orderDeliveryRepository;
        this.customerRepository = customerRepository;
    }

    List<OrderDelivery> listAll(Customer customer){
        if(!customerRepository.exists(customer)){
            throw new IllegalArgumentException("Customer not found");
        }
        return orderDeliveryRepository.findAllByCustomer(customer);
    }

    List<OrderDelivery> listActiveOrders(Customer customer){
        if(!customerRepository.exists(customer)){
            throw new IllegalArgumentException("Customer not found");
        }
        List<OrderDelivery> activeOrders = new java.util.ArrayList<>(List.of());
        List<OrderDelivery> allOrders = orderDeliveryRepository.findAllByCustomer(customer);
        for(int i = 0; i < allOrders.size(); i++){
            if(allOrders.get(i).getStatus() != StatusOrder.CONCLUDED && allOrders.get(i).getStatus() != StatusOrder.CANCELED){
                activeOrders.add(allOrders.get(i));
            }
        }
        return activeOrders;
    }

}
