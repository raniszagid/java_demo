package ru.t1.java.demo.repository;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    long countTransactionsByTransactionTimeAfter(LocalDateTime localDateTime);
    long countTransactionsByTransactionTimeAfterAndAccountId(LocalDateTime dateTime, Long id);
    List<Transaction> findTransactionsByTransactionTimeAfter(LocalDateTime localDateTime);
}
