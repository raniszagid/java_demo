package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.model.enums.AccountType;

import java.io.Serializable;

@LogDataSourceError
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto implements Serializable {
    private Long id;
    @JsonProperty("client_id")
    private Long clientId;
    @JsonProperty("account_type")
    private String accountType;
    @JsonProperty("balance")
    private Double balance;
}
