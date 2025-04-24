package rw.bnr.banking.v1.startup;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import rw.bnr.banking.v1.enums.ERole;
import rw.bnr.banking.v1.models.Role;
import rw.bnr.banking.v1.repositories.RoleRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepo;

    public DataInitializer(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
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
                System.out.println("Created: " + role);
            } else {
                // If the role already exists, skip creation
                System.out.println(role + " already exists.");
            }
        }
    }
}
