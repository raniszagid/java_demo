package ru.t1.java.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;
    @Column(name = "transaction_status")
    @Enumerated(value = EnumType.STRING)
    private TransactionStatus transactionStatus;
    @Column(name = "transaction_id")
    private UUID transactionId;
}
