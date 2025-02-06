package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;

import java.io.Serializable;
import java.util.UUID;

@LogDataSourceError
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {
    private Long id;
    @JsonProperty("client_id")
    private Long clientId;
    @JsonProperty("account_type")
    private String accountType;
    @JsonProperty("balance")
    private Double balance;
    @JsonProperty("frozen_amount")
    private Double frozenAmount;
    @JsonProperty("account_status")
    private String accountStatus;
    @JsonProperty("account_id")
    private UUID accountId;
}
