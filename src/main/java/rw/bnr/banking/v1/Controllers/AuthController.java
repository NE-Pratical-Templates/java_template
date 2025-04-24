package rw.bnr.banking.v1.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.bnr.banking.v1.dtos.request.CreateCustomerDTO;
import rw.bnr.banking.v1.dtos.response.ApiResponse;
import rw.bnr.banking.v1.services.IAuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/register")
    RequestEntity<ApiResponse> registerCustomer(@Valid @RequestBody CreateCustomerDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(authService.registerCustomer(dto)));
    }
}
