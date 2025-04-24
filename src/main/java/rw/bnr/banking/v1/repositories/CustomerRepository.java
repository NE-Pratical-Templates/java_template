package rw.bnr.banking.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.bnr.banking.v1.models.Customer;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByAccount(String accountCode);

    Optional<Customer> findByActivationCode(String verificationCode);
}
