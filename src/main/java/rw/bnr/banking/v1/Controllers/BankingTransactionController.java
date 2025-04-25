package rw.bnr.banking.v1.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.bnr.banking.v1.dtos.request.CreateTransactionDTO;
import rw.bnr.banking.v1.dtos.response.ApiResponseDTO;
import rw.bnr.banking.v1.enums.ETransactionType;
import rw.bnr.banking.v1.models.BankingTransaction;
import rw.bnr.banking.v1.models.Customer;
import rw.bnr.banking.v1.services.IBankTransactionService;
import rw.bnr.banking.v1.services.ICustomerService;
import rw.bnr.banking.v1.standalone.ExcelService;
import rw.bnr.banking.v1.standalone.FileStorageService;
import rw.bnr.banking.v1.utils.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class BankingTransactionController {
    private final ICustomerService customerService;
    private final IBankTransactionService bankTransactionService;
    private final ExcelService excelService;
    private final FileStorageService fileStorageService;
    @Value("${uploads.directory.docs}")
    private String docsFolder;

    //    make transaction
    @Operation(
            summary = "make transaction",
            description = "make transaction",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "make transaction successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @PostMapping("/create")
    private ResponseEntity<ApiResponseDTO> createTransaction(@RequestBody @Valid CreateTransactionDTO dto, @RequestParam(value = "receiverAccount", required = false) String receiverAccount) {
        return ResponseEntity.ok().body(ApiResponseDTO.success("transaction done successfully", bankTransactionService.createTransaction(dto, receiverAccount)));
    }


    //     get all  transactions by admin
    @Operation(
            summary = "get all  transactions by admin",
            description = "get all  transactions by admin",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " all  transactions by admin  fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    private ResponseEntity<ApiResponseDTO> getAllTransactions(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page, @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(ApiResponseDTO.success("Transactions fetched successfully", bankTransactionService.getAllTransactions(pageable)));
    }

    //     get   transaction by id
    @Operation(
            summary = "Transaction fetched successfully",
            description = "Transaction fetched successfully",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "  Transaction fetched successfully successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @GetMapping("/transaction/{id}")
    private ResponseEntity<ApiResponseDTO> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseDTO.success("Transaction fetched successfully", bankTransactionService.getTransactionById(id)));
    }

    //     get all  transactions by type  by admin
    @Operation(
            summary = "get all  transactions by type  by admin",
            description = "get all  transactions by type  by admin",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " all  transactions by admin  fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/type/{type}")
    private ResponseEntity<ApiResponseDTO> getAllTransactionsByType(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page, @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit, @PathVariable("type") ETransactionType type) {
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(ApiResponseDTO.success("Transactions fetched successfully", bankTransactionService.getAllTransactionsByType(pageable, type)));
    }


    //     get all  transactions of customer by admin
    @Operation(
            summary = "get all  transactions of customer by admin",
            description = "get all  transactions of customer by admin",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " all  transactions fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customer/transactions")
    private ResponseEntity<ApiResponseDTO> getAllTransactionsOfCustomerByAdmin(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page, @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit, @RequestParam(value = "customerId") UUID customerId) {
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(ApiResponseDTO.success("Transactions fetched successfully", bankTransactionService.getAllTransactionsOfCustomerByAdmin(pageable, customerId)));
    }

    //     get all  transactions of customer
    @Operation(
            summary = "get all  transactions of customer",
            description = "get all  transactions of customer",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " all  transactions fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @GetMapping("/customer")
    private ResponseEntity<ApiResponseDTO> getAllTransactionsByCustomer(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page, @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Customer customer = customerService.getLoggedInCustomer();
        return ResponseEntity.ok(ApiResponseDTO.success("Transactions fetched successfully", bankTransactionService.getAllTransactionsByCustomer(pageable, customer.getId())));
    }

    //     download  transactions of customer
    @GetMapping("/download")
    @Operation(
            summary = "Download customer transactions",
            description = "Generates and downloads all transactions for the authenticated customer in Excel format",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
                    content = @Content)
    })
    public ResponseEntity<Resource> downloadExcel() throws IOException {
        Customer customer = customerService.getLoggedInCustomer();

        List<String> fileHeaders = Arrays.asList("#", "Customer Names", "Transaction Type", "Amount", "Your Account", "Receiver Account", "Transaction Date");
        List<BankingTransaction> transactions = bankTransactionService.getAllTransactionsByCustomer(customer.getId());

        List<List<String>> data = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            BankingTransaction transaction = transactions.get(i);
            data.add(Arrays.asList(
                    String.valueOf(i + 1),
                    transaction.getCustomer().getFirstName() + " " + transaction.getCustomer().getLastName(),
                    transaction.getTransactionType().name(),
                    String.valueOf(transaction.getAmount()),
                    transaction.getCustomer().getAccount(),
                    transaction.getReceiver() != null ? transaction.getReceiver().getAccount() : "N/A",
                    transaction.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            ));
        }

        // Generate Excel file bytes
        byte[] excelBytes = excelService.generateExcelTransactions(fileHeaders, data);

        // Store the file on disk
        String uuid = UUID.randomUUID().toString();
        String originalName = customer.getFirstName() + "_" + customer.getLastName() + "_transactions.xlsx";
        String finalName = originalName.replace(".xlsx", "-" + uuid + ".xlsx");

        Path directory = Paths.get(docsFolder); // Use your actual path
        Files.createDirectories(directory);
        Path filePath = directory.resolve(finalName);
        Files.write(filePath, excelBytes);

        // Serve file as Resource
        Resource fileResource = new UrlResource(filePath.toUri());
        if (!fileResource.exists()) {
            throw new FileNotFoundException("Generated file not found at: " + filePath.toString());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalName + "\"")
                .body(fileResource);
    }

}
