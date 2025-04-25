package rw.bnr.banking.v1.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.bnr.banking.v1.dtos.response.ApiResponseDTO;
import rw.bnr.banking.v1.models.Customer;
import rw.bnr.banking.v1.services.ICustomerService;
import rw.bnr.banking.v1.services.IMessageService;
import rw.bnr.banking.v1.utils.Constants;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final IMessageService messageService;
    private final ICustomerService customerService;

    //    get specific message

    @Operation(
            summary = "get specific  message",
            description = "get specific  message",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "  message fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> getMessageById(
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(ApiResponseDTO.success("Message fetched successfully", this.messageService.getMessageById(id)));
    }

    //     get all  messages by admin
    @Operation(
            summary = "get all  messages by admin",
            description = "get all  messages by admin",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " all  messages by admin fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO> getAllMessages(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(ApiResponseDTO.success("Messages fetched successfully", this.messageService.findAllMessages(pageable)));
    }

    //     get all  messages of customer by admin
    @Operation(
            summary = "get all  messages of customer by admin",
            description = "get all  messages of customer by admin",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " all  messages fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customer/messages")
    private ResponseEntity<ApiResponseDTO> getAllTransactionsOfCustomerByAdmin(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page, @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit, @RequestParam(value = "customerId") UUID customerId) {
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(ApiResponseDTO.success("Transactions fetched successfully", messageService.getAllTransactionsOfCustomerByAdmin(pageable, customerId)));
    }

    //     get all  messages by customer
    @Operation(
            summary = "get all  messages by customer",
            description = "get all  messages by customer",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " all  messages by customer fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @GetMapping("/customer")
    public ResponseEntity<ApiResponseDTO> getAllMessagesByCustomer(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        Customer customer = customerService.getLoggedInCustomer();
        return ResponseEntity.ok(ApiResponseDTO.success("Messages fetched successfully", this.messageService.findAllMessagesByCustomer(pageable, customer.getId())));
    }

}
