package rw.bnr.banking.v1.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import rw.bnr.banking.v1.audits.InitiatorAudit;
import rw.bnr.banking.v1.enums.ETransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_transactions")
public class BankingTransaction extends InitiatorAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "receiver_to_id")
    private Customer receiver;

    @Column(name = "amount")
    private double amount;

    @Column(name = "account")
    private String account;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ETransactionType transactionType;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "banking_date_time")
    private LocalDateTime bankingDateTime;
}
