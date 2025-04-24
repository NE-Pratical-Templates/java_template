package rw.bnr.banking.v1.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.bnr.banking.v1.dtos.response.ApiResponseDTO;
import rw.bnr.banking.v1.services.CustomerService;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

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

}
