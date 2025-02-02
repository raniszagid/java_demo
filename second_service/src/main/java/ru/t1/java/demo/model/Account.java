package ru.t1.java.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account /*extends AbstractPersistable<Long>*/ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "client_id")
    private Long clientId;
    @Column(name = "account_type")
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;
    @Column(name = "balance")
    private Double balance;
    @Column(name = "frozen_amount")
    private Double frozenAmount;
    @Column(name = "account_status")
    @Enumerated(value = EnumType.STRING)
    private AccountStatus accountStatus;
    @Column(name = "account_id")
    private UUID accountId;
}
