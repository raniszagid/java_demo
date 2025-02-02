package ru.t1.java.demo.model;

import lombok.*;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionResult {
    private TransactionStatus status;
    private UUID accountId;
    private UUID transactionId;
}
