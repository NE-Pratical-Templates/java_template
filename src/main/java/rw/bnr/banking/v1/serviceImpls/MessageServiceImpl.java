package rw.bnr.banking.v1.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rw.bnr.banking.v1.exceptions.ResourceNotFoundException;
import rw.bnr.banking.v1.models.Customer;
import rw.bnr.banking.v1.models.Message;
import rw.bnr.banking.v1.repositories.IMessageRepository;
import rw.bnr.banking.v1.services.IMessageService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {
    private final IMessageRepository messageRepo;

    @Override
    public void create(Customer customer, String content) {
        Message message = new Message();
        message.setCustomer(customer);
        message.setMessage(content);
        message.setCreatedAt(LocalDateTime.now());
        messageRepo.save(message);
    }

    @Override
    public Message getMessageById(UUID id) {
        return this.messageRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Message", "id", id.toString()));
    }

    @Override
    public Page<Message> findAllMessages(Pageable pageable) {
        return this.messageRepo.findAll(pageable);
    }

    @Override
    public Page<Message> findAllMessagesByCustomer(Pageable pageable, UUID customerId) {
        return this.messageRepo.findAllByCustomerId(pageable, customerId);
    }

    @Override
    public Page<Message> getAllTransactionsOfCustomerByAdmin(Pageable pageable, UUID customerId) {
        return this.messageRepo.findAllByCustomerId(pageable, customerId);
    }
}
