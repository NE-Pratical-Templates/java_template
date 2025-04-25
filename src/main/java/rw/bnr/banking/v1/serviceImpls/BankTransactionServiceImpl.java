package rw.bnr.banking.v1.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rw.bnr.banking.v1.dtos.request.CreateTransactionDTO;
import rw.bnr.banking.v1.enums.ETransactionType;
import rw.bnr.banking.v1.exceptions.BadRequestException;
import rw.bnr.banking.v1.exceptions.ResourceNotFoundException;
import rw.bnr.banking.v1.models.BankingTransaction;
import rw.bnr.banking.v1.models.Customer;
import rw.bnr.banking.v1.repositories.IBankTransactionRepository;
import rw.bnr.banking.v1.repositories.ICustomerRepository;
import rw.bnr.banking.v1.services.IBankTransactionService;
import rw.bnr.banking.v1.services.ICustomerService;
import rw.bnr.banking.v1.services.IMessageService;
import rw.bnr.banking.v1.standalone.MailService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankTransactionServiceImpl implements IBankTransactionService {
    private final ICustomerService customerService;
    private final ICustomerRepository customerRepo;
    private final IBankTransactionRepository bankTransactionRepo;
    private final MailService mailService;
    private final IMessageService messageService;

    @Override
    public BankingTransaction createTransaction(CreateTransactionDTO dto, String receiverAccount) {
        Customer customer = customerService.getLoggedInCustomer();
        BankingTransaction transaction = new BankingTransaction();
        if (dto.getTransactionType() == ETransactionType.WITHDRAW) {
            if (customer.getBalance() < dto.getAmount()) throw new BadRequestException("insufficient founds");
            customer.setBalance(customer.getBalance() - dto.getAmount());
            transaction.setAccount(customer.getAccount());

        } else if (dto.getTransactionType() == ETransactionType.SAVING) {
            customer.setBalance(customer.getBalance() + dto.getAmount());
            transaction.setAccount(customer.getAccount());

        } else if (dto.getTransactionType() == ETransactionType.TRANSFER && receiverAccount != null) {
            if (customer.getBalance() < dto.getAmount()) throw new BadRequestException("insufficient founds");
            customer.setBalance(customer.getBalance() - dto.getAmount());
            Customer receiver = customerRepo.findByAccount(receiverAccount).orElseThrow(() -> new BadRequestException(" no customer found with that  bank account number "));
            if (receiver.getId() == customer.getId()) throw new BadRequestException("you can't transfer to yourself ");
            receiver.setBalance(receiver.getBalance() + dto.getAmount());
            customerService.save(receiver);
            transaction.setReceiver(receiver);
            transaction.setAccount(receiverAccount);

        } else {
            if (dto.getTransactionType() == ETransactionType.TRANSFER)
                throw new BadRequestException("Receiver id is required");
            throw new BadRequestException("Invalid transaction type");
        }
        customerService.save(customer);
        transaction.setCustomer(customer);
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionType(dto.getTransactionType());
        String message = null;
        if (dto.getTransactionType() == ETransactionType.SAVING) {
            message = String.format("Dear %s, your SAVING of %.2f on account %s has been Completed Successfully.",
                    customer.getFullName(), dto.getAmount(), customer.getAccount());
            mailService.sendSavingsStoredSuccessfullyEmail(customer.getEmail(), customer.getFullName(), dto.getAmount().toString(), String.valueOf(customer.getBalance()), customer.getAccount(), customer.getId());

        } else if (dto.getTransactionType() == ETransactionType.WITHDRAW) {
            message = String.format("Dear %s, your WITHDRAWAL of %.2f from account %s has been Completed Successfully.",
                    customer.getFullName(), dto.getAmount(), customer.getAccount());
            mailService.sendWithdrawalSuccessfulEmail(customer.getEmail(), customer.getFullName(), dto.getAmount().toString(), String.valueOf(customer.getBalance()), customer.getAccount(), customer.getId());

        } else if (dto.getTransactionType() == ETransactionType.TRANSFER) {
            message = String.format("Dear %s, you TRANSFERRED %.2f to %s from your account %s.",
                    customer.getFullName(), dto.getAmount(), transaction.getReceiver().getFullName(), customer.getAccount());
            mailService.sendTransferSuccessfulEmail(customer.getEmail(), customer.getFullName(), dto.getAmount().toString(), String.valueOf(customer.getBalance()), transaction.getReceiver().getFullName(), customer.getAccount(), customer.getId());
            mailService.sendReceivedAmountEmail(transaction.getReceiver().getEmail(), transaction.getReceiver().getFullName(), customer.getFullName(), dto.getAmount().toString(), String.valueOf(transaction.getReceiver().getBalance()));
            // Optional message for the receiver
            String receiverMessage = String.format("Dear %s, you RECEIVED %.2f from %s into your account %s.",
                    transaction.getReceiver().getFullName(), dto.getAmount(), customer.getFullName(), transaction.getReceiver().getAccount());
            messageService.create(transaction.getReceiver(), receiverMessage);
        }
        messageService.create(customer, message);
        return bankTransactionRepo.save(transaction);
    }

    @Override
    public Page<BankingTransaction> getAllTransactions(Pageable pageable) {
        return bankTransactionRepo.findAll(pageable);
    }

    @Override
    public Page<BankingTransaction> getAllTransactionsByCustomer(Pageable pageable, UUID customerId) {
        return bankTransactionRepo.findAllByCustomerId(pageable, customerId);
    }

    @Override
    public Page<BankingTransaction> getAllTransactionsOfCustomerByAdmin(Pageable pageable, UUID customerId) {
        return bankTransactionRepo.findAllByCustomerId(pageable, customerId);
    }

    @Override
    public Page<BankingTransaction> getAllTransactionsByType(Pageable pageable, ETransactionType type) {
        return bankTransactionRepo.findAllByTransactionType(pageable, type);
    }

    @Override
    public BankingTransaction getTransactionById(UUID id) {
        return bankTransactionRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Transaction", "id", id.toString()));
    }

    @Override
    public List<BankingTransaction> getAllTransactionsByCustomer(UUID customerId) {
        return bankTransactionRepo.findAllByCustomerId(customerId);
    }
}
