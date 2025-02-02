package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }
    public Optional<Transaction> get(Long id) {
        return transactionRepository.findById(id);
    }
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
    public void change(Transaction old, TransactionDto fresh) {
        if (fresh.getAccountId() != null)
            old.setAccountId(fresh.getAccountId());
        if (fresh.getAmount() != null)
            old.setAmount(fresh.getAmount());
        if (fresh.getTransactionTime() != null)
            old.setTransactionTime(fresh.getTransactionTime());
        transactionRepository.save(old);
    }
    public void delete(Transaction transaction) {
        transactionRepository.delete(transaction);
    }
    public void saveAccepting(TransactionDto transactionDto) {
        transactionDto.setTransactionTime(LocalDateTime.now());
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setTransactionStatus(TransactionStatus.REQUESTED);
        transactionRepository.save(transaction);
    }
    public List<Transaction> getTransactionsLastTime(LocalDateTime current, Long minutes) {
        LocalDateTime dateTime = current.minusMinutes(minutes);
        return transactionRepository.findTransactionsByTransactionTimeAfter(dateTime);
    }
    public long countTransactionsLastTime(LocalDateTime current, Long minutes) {
        LocalDateTime dateTime = current.minusMinutes(minutes);
        return transactionRepository.countTransactionsByTransactionTimeAfter(dateTime);
    }
    public long countTransactionsLastTimeByAccount(LocalDateTime current, Long minutes, Long accId) {
        LocalDateTime dateTime = current.minusMinutes(minutes);
        return transactionRepository
                .countTransactionsByTransactionTimeAfterAndAccountId(dateTime, accId);
    }
}
