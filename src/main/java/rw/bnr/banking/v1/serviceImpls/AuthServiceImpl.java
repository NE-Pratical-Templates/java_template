package rw.bnr.banking.v1.serviceImpls;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rw.bnr.banking.v1.dtos.request.CreateCustomerDTO;
import rw.bnr.banking.v1.repositories.ICustomerRepository;
import rw.bnr.banking.v1.services.IAuthService;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl  implements IAuthService {
    private  final ICustomerRepository customerRepo;

    @Override
    public String registerCustomer(CreateCustomerDTO dto) {
        return "";
    }
}
