package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findTransactionByTransactionId(UUID uuid);
    List<Transaction>
    findTransactionsByTransactionTimeAfterAndAccountId(LocalDateTime localDateTime, Long id);
    List<Transaction> findTransactionsByAccountId(Long id);
    List<Transaction>
    findTransactionsByAccountIdAndTransactionStatus(Long accountId, TransactionStatus status);
}
