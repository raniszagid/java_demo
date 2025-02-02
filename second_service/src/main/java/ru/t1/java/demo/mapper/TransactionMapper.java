package ru.t1.java.demo.mapper;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;

@Component
public class TransactionMapper {
    public Transaction toEntity(TransactionDto dto) {
        TransactionStatus status = dto.getTransactionStatus() != null
                ? TransactionStatus.valueOf(dto.getTransactionStatus())
                : null;
        return Transaction.builder()
                .accountId(dto.getAccountId())
                .amount(dto.getAmount())
                .transactionTime(dto.getTransactionTime())
                .transactionStatus(status)
                .transactionId(dto.getTransactionId())
                .build();
    }
    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId())
                .amount(entity.getAmount())
                .transactionTime(entity.getTransactionTime())
                .transactionStatus(entity.getTransactionStatus().name())
                .transactionId(entity.getTransactionId())
                .build();
    }
}
