package rw.bnr.banking.v1.services;

import jakarta.validation.Valid;
import rw.bnr.banking.v1.dtos.request.CreateCustomerDTO;

public interface IAuthService {
    String registerCustomer(@Valid CreateCustomerDTO dto);
}
