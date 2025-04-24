package rw.bnr.banking.v1.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import rw.bnr.banking.v1.validators.ValidPassword;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreateCustomerDTO {
    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    @ValidPassword
    private String password;

    @NotNull
    private String mobile;

    @NotNull
    @PastOrPresent(message = "Date of birth should be in the past")
    @NotNull(message = "Date of birth should not be empty")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @NotNull
    @DecimalMin(value = "0.1", message = "Balance should be greater than 0", inclusive = false)
    private Double balance;
}
