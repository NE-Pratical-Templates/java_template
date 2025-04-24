package rw.bnr.banking.v1.services;

import rw.bnr.banking.v1.models.Customer;

public interface CustomerService {
    Customer getLoggedInCustomer();
}
