package rw.bnr.banking.v1.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rw.bnr.banking.v1.dtos.UpdateCustomerDTO;
import rw.bnr.banking.v1.dtos.response.ApiResponseDTO;
import rw.bnr.banking.v1.exceptions.BadRequestException;
import rw.bnr.banking.v1.models.Customer;
import rw.bnr.banking.v1.models.File;
import rw.bnr.banking.v1.services.ICustomerService;
import rw.bnr.banking.v1.services.IFileService;
import rw.bnr.banking.v1.utils.Constants;
import rw.bnr.banking.v1.utils.Utility;

import java.util.UUID;

//TODO: transactions and  upload profile
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final ICustomerService customerService;
    private final IFileService fileService;
    @Value("${uploads.directory.customer_profiles}")
    private String customerProfilesDirectory;

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

    //    upload profile of your  account
    @Operation(
            summary = "Upload profile image",
            description = "Allows a logged-in customer to upload a profile image.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile image uploaded successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid file type"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PutMapping(path = "/upload-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO> uploadProfileImage(
            @Parameter(
                    description = "Image file to upload",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart("file") MultipartFile document
    ) {
        if (!Utility.isImageFile(document)) {
            throw new BadRequestException("Only image files are allowed");
        }
        Customer customer = this.customerService.getLoggedInCustomer();
        File file = this.fileService.create(document, customerProfilesDirectory);

        Customer updated = this.customerService.changeProfileImage(customer.getId(), file);

        return ResponseEntity.ok(ApiResponseDTO.success("File saved successfully", updated));
    }


    //    delete your profile picture
    @Operation(
            summary = "delete your profile picture",
            description = "delete your profile picture",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(path = "/remove-profile")
    public ResponseEntity<ApiResponseDTO> removeProfileImage() {
        Customer customer = this.customerService.getLoggedInCustomer();
        Customer updated = this.customerService.removeProfileImage(customer.getId());
        return ResponseEntity.ok(ApiResponseDTO.success("Profile image removed successfully", updated));
    }

    //    ADMIN operations
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
