package ru.t1.java.demo.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Acceptance {
    private UUID clientId;
    private UUID accountId;
    private UUID transactionId;
    private LocalDateTime timestamp;
    private Double transactionAmount;
    private Double accountBalance;
}
