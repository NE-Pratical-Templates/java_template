package rw.bnr.banking.v1.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rw.bnr.banking.v1.exceptions.ResourceNotFoundException;
import rw.bnr.banking.v1.models.Customer;
import rw.bnr.banking.v1.repositories.CustomerRepository;
import rw.bnr.banking.v1.services.CustomerService;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepo;

    @Override
    public Customer getLoggedInCustomer() {
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return customerRepo.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", email));
    }
}
