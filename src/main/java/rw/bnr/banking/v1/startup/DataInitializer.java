package rw.bnr.banking.v1.startup;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rw.bnr.banking.v1.enums.ECustomerStatus;
import rw.bnr.banking.v1.enums.ERole;
import rw.bnr.banking.v1.exceptions.BadRequestException;
import rw.bnr.banking.v1.models.Customer;
import rw.bnr.banking.v1.models.Role;
import rw.bnr.banking.v1.repositories.CustomerRepository;
import rw.bnr.banking.v1.repositories.RoleRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepo;
    private final CustomerRepository customerRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(RoleRepository roleRepo, CustomerRepository customerRepo, PasswordEncoder encoder) {
        this.roleRepo = roleRepo;
        this.customerRepo = customerRepo;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // Roles to be created
        Set<ERole> roles = new HashSet<>();
        roles.add(ERole.ADMIN);
        roles.add(ERole.CUSTOMER);

        for (ERole role : roles) {
            // Check if the role already exists in the database
            if (!roleRepo.existsByName(role)) {
                // If not, create and save the new role
                Role newRole = new Role(role, role.toString());
                roleRepo.save(newRole);
                log.info("Created: {}", role);
            } else {
                log.info("{} already exists.", role);
            }
        }
        Optional<Customer> admin = customerRepo.findByEmail("admin@gmail.com");
        if (admin.isEmpty()) {
            Customer newAdmin = new Customer();
            newAdmin.setFirstName("admin");
            newAdmin.setLastName("admin");
            newAdmin.setEmail("admin@gmail.com");
            newAdmin.setPassword(encoder.encode("admin"));
            newAdmin.setMobile("0799999999");
            newAdmin.setStatus(ECustomerStatus.ACTIVE);
            Role role = roleRepo.findByName(ERole.ADMIN).orElseThrow(
                    () -> new BadRequestException("ADMIN Role not set"));
            newAdmin.setRoles(Collections.singleton(role));

            customerRepo.save(newAdmin);
        } else {
            log.info("{} already exists.", admin.get().getEmail());

        }
    }
}
