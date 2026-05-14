package br.ifsp.demo.security.config;

import br.ifsp.demo.domain.aggregate.Customer;
import br.ifsp.demo.domain.aggregate.DeliveryMan;
import br.ifsp.demo.domain.repository.CustomerRepository;
import br.ifsp.demo.domain.repository.DeliveryManRepository;
import br.ifsp.demo.domain.valueObject.CustomerType;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@Configuration
public class DatabaseSeeder {

    @Value("${seed.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner seedDatabase(
            CustomerRepository customerRepository,
            DeliveryManRepository deliveryManRepository,
            JpaUserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            seedUsers(userRepository, passwordEncoder);
            seedCustomers(customerRepository);
            seedDeliveryMen(deliveryManRepository);
        };
    }

    private void seedUsers(JpaUserRepository userRepository, PasswordEncoder passwordEncoder) {
        String email = "admin@test.com";

        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        User user = User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .name("Admin")
                .lastname("Test")
                .email(email)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);
    }

    private void seedCustomers(CustomerRepository customerRepository) {
        List<Customer> customers = List.of(
                Customer.reconstitute(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        "Acme Corp",
                        CustomerType.BUSINESS
                ),
                Customer.reconstitute(
                        UUID.fromString("22222222-2222-2222-2222-222222222222"),
                        "Beatriz Lima",
                        CustomerType.REGULAR
                ),
                Customer.reconstitute(
                        UUID.fromString("33333333-3333-3333-3333-333333333333"),
                        "Globex SA",
                        CustomerType.BUSINESS
                ),
                Customer.reconstitute(
                        UUID.fromString("44444444-4444-4444-4444-444444444444"),
                        "Studio Nova",
                        CustomerType.PREMIUM
                ),
                Customer.reconstitute(
                        UUID.fromString("55555555-5555-5555-5555-555555555555"),
                        "Initech",
                        CustomerType.PREMIUM
                )
        );

        customers.forEach(customer -> {
            if (customerRepository.findById(customer.getCustomerId()).isEmpty()) {
                customerRepository.save(customer);
            }
        });
    }

    private void seedDeliveryMen(DeliveryManRepository deliveryManRepository) {
        List<DeliveryMan> deliveryMen = List.of(
                DeliveryMan.reconstitute(
                        UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                        "Carlos Mendes",
                        3
                ),
                DeliveryMan.reconstitute(
                        UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                        "Marina Souza",
                        2
                ),
                DeliveryMan.reconstitute(
                        UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
                        "Felipe Ramos",
                        4
                ),
                DeliveryMan.reconstitute(
                        UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
                        "Ana Paula",
                        2
                ),
                DeliveryMan.reconstitute(
                        UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee"),
                        "João Victor",
                        5
                )
        );

        deliveryMen.forEach(deliveryMan -> {
            if (deliveryManRepository.findById(deliveryMan.getId()).isEmpty()) {
                deliveryManRepository.save(deliveryMan);
            }
        });
    }
}