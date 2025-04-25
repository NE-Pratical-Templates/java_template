package rw.bnr.banking.v1.services;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.bnr.banking.v1.dtos.request.CreateTransactionDTO;
import rw.bnr.banking.v1.enums.ETransactionType;
import rw.bnr.banking.v1.models.BankingTransaction;

import java.util.List;
import java.util.UUID;

public interface IBankTransactionService {
    BankingTransaction createTransaction(@Valid CreateTransactionDTO dto, String receiverAccount);

    Page<BankingTransaction> getAllTransactions(Pageable pageable);

    Page<BankingTransaction> getAllTransactionsByCustomer(Pageable pageable, UUID customerId);
    Page<BankingTransaction> getAllTransactionsOfCustomerByAdmin(Pageable pageable, UUID customerId);

    Page<BankingTransaction> getAllTransactionsByType(Pageable pageable, ETransactionType type);

    BankingTransaction getTransactionById(UUID id);

    List<BankingTransaction> getAllTransactionsByCustomer(UUID customerId);
}
