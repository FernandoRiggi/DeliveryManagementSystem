    package br.ifsp.demo.domain.repository;

    import br.ifsp.demo.domain.aggregate.Customer;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.UUID;

    public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    }
