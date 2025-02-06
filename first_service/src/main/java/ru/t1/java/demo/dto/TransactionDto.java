package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {
    private Long id;
    @JsonProperty("account_id")
    private Long accountId;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("transaction_time")
    private LocalDateTime transactionTime;
    @JsonProperty("transaction_status")
    private String transactionStatus;
    @JsonProperty("transaction_id")
    private UUID transactionId;
}
