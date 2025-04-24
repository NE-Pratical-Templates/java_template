package rw.bnr.banking.v1.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.bnr.banking.v1.dtos.UpdateCustomerDTO;
import rw.bnr.banking.v1.dtos.response.ApiResponseDTO;
import rw.bnr.banking.v1.models.Customer;
import rw.bnr.banking.v1.services.CustomerService;
import rw.bnr.banking.v1.utils.Constants;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    //     get logged in customer
    @Operation(
            summary = "Get currently logged-in customer",
            description = "Fetch the profile of the authenticated customer based on JWT token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer profile fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @GetMapping(path = "/current-customer")
    public ResponseEntity<ApiResponseDTO> currentlyLoggedInCustomer() {
        return ResponseEntity.ok(ApiResponseDTO.success("Currently logged in customer fetched", customerService.getLoggedInCustomer()));
    }

    //     update your  account
    @Operation(
            summary = "updated your account ",
            description = "update your account",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/update")
    public ResponseEntity<ApiResponseDTO> update(@RequestBody UpdateCustomerDTO dto) {
        Customer updated = this.customerService.update(this.customerService.getLoggedInCustomer().getId(), dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Customer updated successfully", updated));
    }

    //    delete account
    @Operation(
            summary = "delete your account ",
            description = "delete your account",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseDTO> deleteMyAccount() {
        Customer customer = this.customerService.getLoggedInCustomer();
        this.customerService.delete(customer.getId());
        return ResponseEntity.ok(ApiResponseDTO.success("Account deleted successfully"));
    }

    //    get all customers  account by admin
    @Operation(
            summary = " get all customers  account by admin",
            description = " get all customers  account by admin",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/all")
    public ResponseEntity<ApiResponseDTO> getAllUsers(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit
    ) {
        Pageable pageable = Pageable.ofSize(limit).withPage(page);
        return ResponseEntity.ok(ApiResponseDTO.success("Users fetched successfully", this.customerService.getAll(pageable)));
    }

    //    search   account by admin
    @Operation(
            summary = " search   account by admin ",
            description = " search   account by admin",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/search")
    public ResponseEntity<ApiResponseDTO> search(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @RequestParam(value = "q") String q
    ) {
        Pageable pageable = Pageable.ofSize(limit).withPage(page);
        return ResponseEntity.ok(ApiResponseDTO.success("Users fetched successfully", this.customerService.search(pageable, q)));
    }


    //    get customer    account by admin with id
    @Operation(
            summary = " get customer    account by admin with id ",
            description = " get customer    account by admin with id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/account/{id}")
    public ResponseEntity<ApiResponseDTO> getById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(ApiResponseDTO.success("Customer fetched successfully", this.customerService.getById(id)));
    }


    //    delete customer    account by admin with id
    @Operation(
            summary = " delete customer    account by admin with id ",
            description = " delete customer    account by admin with id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDTO> deleteByAdmin(
            @PathVariable(value = "id") UUID id
    ) {
        this.customerService.delete(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Account deleted successfully"));
    }
}
