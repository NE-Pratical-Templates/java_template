package rw.bnr.banking.v1.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rw.bnr.banking.v1.dtos.UpdateCustomerDTO;
import rw.bnr.banking.v1.exceptions.BadRequestException;
import rw.bnr.banking.v1.exceptions.ResourceNotFoundException;
import rw.bnr.banking.v1.models.Customer;
import rw.bnr.banking.v1.models.File;
import rw.bnr.banking.v1.repositories.ICustomerRepository;
import rw.bnr.banking.v1.services.ICustomerService;
import rw.bnr.banking.v1.services.IFileService;
import rw.bnr.banking.v1.standalone.FileStorageService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    private final ICustomerRepository customerRepo;
    private final FileStorageService fileStorageService;
    private final IFileService fileService;

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

    @Override
    public Customer update(UUID id, UpdateCustomerDTO dto) {
        Customer entity = customerRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "id", id.toString()));

        Optional<Customer> userOptional = customerRepo.findByEmail(dto.getEmail());
        if (userOptional.isPresent() && (userOptional.get().getId() != entity.getId()))
            throw new BadRequestException(String.format("Customer with email '%s' already exists", entity.getEmail()));

        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setMobile(dto.getMobile());
        entity.setDob(dto.getDob());

        return customerRepo.save(entity);
    }

    @Override
    public boolean delete(UUID id) {
        customerRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User", "id", id));

        customerRepo.deleteById(id);
        return true;
    }

    @Override
    public Page<Customer> getAll(Pageable pageable) {
        return customerRepo.findAll(pageable);
    }

    @Override
    public Page<Customer> search(Pageable pageable, String searchKey) {
        return customerRepo.search(pageable, searchKey);
    }

    @Override
    public Customer getById(UUID id) {
        return customerRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "id", id.toString()));
    }

    @Override
    public Customer changeProfileImage(UUID id, File file) {
        Customer entity = customerRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Document", "id", id.toString()));
        File existingFile = entity.getProfileImage();
        if (existingFile != null) {
            this.fileStorageService.removeFileOnDisk(existingFile.getPath());
        }
        entity.setProfileImage(file);
        return customerRepo.save(entity);

    }

    @Override
    public Customer removeProfileImage(UUID id) {
        Customer user = customerRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "id", id.toString()));
        File file = user.getProfileImage();
        if (file != null) {
            this.fileService.delete(file.getId());
        }
        user.setProfileImage(null);
        return customerRepo.save(user);
    }
}
