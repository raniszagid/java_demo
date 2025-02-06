package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }
    public Transaction get(Long id) {
        return transactionRepository.findById(id).orElseThrow(ClientException::new);
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
    public void changeStatus(UUID uuid, TransactionStatus status) {
        Transaction transaction = transactionRepository
                .findTransactionByTransactionId(uuid)
                .orElseThrow(ClientException::new);
        transaction.setTransactionStatus(status);
        transactionRepository.save(transaction);
    }
    public Double getAmountByUUID(UUID uuid) {
        Transaction transaction = transactionRepository
                .findTransactionByTransactionId(uuid)
                .orElseThrow(ClientException::new);
        return transaction.getAmount();
    }
    public void blockTransactions(Long accountId, UUID lastTransactionId, Long minutes) {
        Transaction transaction = transactionRepository
                .findTransactionByTransactionId(lastTransactionId)
                .orElseThrow(ClientException::new);
        LocalDateTime timestamp = transaction.getTransactionTime().minusMinutes(minutes);
        List<Transaction> transactionsToBlock = transactionRepository
                .findTransactionsByTransactionTimeAfterAndAccountId(timestamp, accountId);
        transactionsToBlock
                .stream().forEach(transToBlock -> transToBlock.setTransactionStatus(TransactionStatus.BLOCKED));
        transactionRepository.saveAll(transactionsToBlock);
    }
    public Double summaryBlockedTransactionsOfAccount(Long accountId) {
        List<Transaction> list = transactionRepository
                .findTransactionsByAccountIdAndTransactionStatus(accountId, TransactionStatus.BLOCKED);
        return list.stream().map(Transaction::getAmount).mapToDouble(Double::doubleValue).sum();
    }
}
