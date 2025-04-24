package rw.bnr.banking.v1.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import rw.bnr.banking.v1.dtos.request.CreateCustomerDTO;
import rw.bnr.banking.v1.dtos.request.LoginDTO;
import rw.bnr.banking.v1.dtos.response.JwtAuthenticationResponse;
import rw.bnr.banking.v1.models.Customer;

public interface IAuthService {
    Customer registerCustomer(@Valid CreateCustomerDTO dto);

    void verifyAccount(String verificationCode);

    void initiateAccountVerification(@Email String email);

    JwtAuthenticationResponse login(@Valid LoginDTO dto);

    void initiateResetPassword(@NotBlank @Email String email);

    void resetPassword(@NotBlank String email, @NotBlank String passwordResetCode, String newPassword);
}
