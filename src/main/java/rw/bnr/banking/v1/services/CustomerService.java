package rw.bnr.banking.v1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.bnr.banking.v1.dtos.UpdateCustomerDTO;
import rw.bnr.banking.v1.models.Customer;

import java.util.UUID;

public interface CustomerService {
    Customer getLoggedInCustomer();

    Customer update(UUID id, UpdateCustomerDTO dto);

    boolean delete(UUID id);

    Page<Customer> getAll(Pageable pageable);

    Page<Customer> search(Pageable pageable, String q);

    Customer getById(UUID id);
}
