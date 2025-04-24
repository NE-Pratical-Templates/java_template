package rw.bnr.banking.v1.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rw.bnr.banking.v1.dtos.request.CreateCustomerDTO;
import rw.bnr.banking.v1.dtos.request.LoginDTO;
import rw.bnr.banking.v1.dtos.response.JwtAuthenticationResponse;
import rw.bnr.banking.v1.enums.ECustomerStatus;
import rw.bnr.banking.v1.enums.ERole;
import rw.bnr.banking.v1.exceptions.AppException;
import rw.bnr.banking.v1.exceptions.BadRequestException;
import rw.bnr.banking.v1.exceptions.ResourceNotFoundException;
import rw.bnr.banking.v1.models.Customer;
import rw.bnr.banking.v1.models.Role;
import rw.bnr.banking.v1.repositories.CustomerRepository;
import rw.bnr.banking.v1.repositories.RoleRepository;
import rw.bnr.banking.v1.security.JwtTokenProvider;
import rw.bnr.banking.v1.security.UserPrincipal;
import rw.bnr.banking.v1.services.IAuthService;
import rw.bnr.banking.v1.standalone.MailService;
import rw.bnr.banking.v1.utils.Utility;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final CustomerRepository customerRepo;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RoleRepository roleRepo;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    //    register customer
    @Override
    public Customer registerCustomer(CreateCustomerDTO dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setMobile(dto.getMobile());
        customer.setDob(dto.getDob());
        try {
            String accountCode;
            do {
                accountCode = Utility.generateCode();
            } while (customerRepo.findByAccount(accountCode).isPresent());
            boolean u = customerRepo.findByEmail(dto.getEmail()).isPresent();
            if (u) {
                throw new BadRequestException("customer with  email already exists");
            }

            Role role = roleRepo.findByName(ERole.CUSTOMER).orElseThrow(
                    () -> new BadRequestException("Customer Role not set"));
            String encodedPassword = passwordEncoder.encode(dto.getPassword());

            customer.setPassword(encodedPassword);
            customer.setAccount(accountCode);
            customer.setRoles(Collections.singleton(role));
            return customerRepo.save(customer);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = Utility.getConstraintViolationMessage(ex, customer);
            throw new BadRequestException(errorMessage, ex);
        }
    }

    //    login
    @Override
    public JwtAuthenticationResponse login(LoginDTO dto) {
        String jwt = null;

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);


            jwt = jwtTokenProvider.generateToken(authentication);

        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        Customer customer = customerRepo.findByEmail(dto.getEmail()).orElseThrow(() -> new UsernameNotFoundException("user not found "));
        if (customer.getStatus() == ECustomerStatus.PENDING)
            throw new BadRequestException("please verify account before login ");
        if (customer.getStatus() == ECustomerStatus.DEACTIVATED)
            throw new BadRequestException("your account is deactivated , please activate it before using it ");
        return new JwtAuthenticationResponse(jwt, customer);
    }

    //    initiate reset of password
    @Override
    public void initiateResetPassword(String email) {
        Customer user = customerRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("no customer found with that email"));
        user.setActivationCode(Utility.randomUUID(6, 0, 'N'));
        user.setStatus(ECustomerStatus.RESET);
        customerRepo.save(user);
        mailService.sendResetPasswordMail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), user.getActivationCode());
    }


    //    reset password
    @Override
    public void resetPassword(String email, String passwordResetCode, String newPassword) {
        Customer user = customerRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("no customer found with  that email "));
        if (Utility.isCodeValid(user.getActivationCode(), passwordResetCode) &&
                (user.getStatus().equals(ECustomerStatus.RESET)) || user.getStatus().equals(ECustomerStatus.PENDING)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setActivationCode(Utility.randomUUID(6, 0, 'N'));
            user.setActivationCodeExpiresAt(null);
            user.setStatus(ECustomerStatus.ACTIVE);
            customerRepo.save(user);
            this.mailService.sendPasswordResetSuccessfully(user.getEmail(), user.getFullName());
        } else {
            throw new BadRequestException("Invalid code or account status");
        }
    }

//initiate to get verification codes

    @Override
    public void initiateAccountVerification(String email) {
        Customer user = customerRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("no customer found with email"));
        if (user.getStatus() == ECustomerStatus.ACTIVE) {
            throw new BadRequestException("Customer is already verified");
        }
        String verificationCode;
        do {
            verificationCode = Utility.generateCode();
        } while (customerRepo.findByActivationCode(verificationCode).isPresent());
        LocalDateTime verificationCodeExpiresAt = LocalDateTime.now().plusHours(6);
        user.setActivationCode(verificationCode);
        user.setActivationCodeExpiresAt(verificationCodeExpiresAt);
        this.mailService.sendActivateAccountEmail(user.getEmail(), user.getFullName(), verificationCode);
        customerRepo.save(user);
    }

//      verify account

    @Override
    public void verifyAccount(String verificationCode) {
        Optional<Customer> _user = customerRepo.findByActivationCode(verificationCode);
        if (_user.isEmpty()) {
            throw new ResourceNotFoundException("Customer", verificationCode, verificationCode);
        }
        Customer user = _user.get();
        if (user.getActivationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification code is invalid or expired");
        }
        user.setStatus(ECustomerStatus.ACTIVE);
        user.setActivationCodeExpiresAt(null);
        user.setActivationCode(null);
        this.mailService.sendAccountVerifiedSuccessfullyEmail(user.getEmail(), user.getFullName());
        customerRepo.save(user);
    }

}
