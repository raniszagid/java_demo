package ru.t1.java.demo.mapper;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.enums.AccountType;

@Component
public class AccountMapper {
    public Account toEntity(AccountDto dto) {
        return Account.builder()
                .clientId(dto.getClientId())
                .accountType(AccountType.valueOf(dto.getAccountType()))
                .balance(dto.getBalance())
                .build();
    }
    public AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .accountType(entity.getAccountType().name())
                .balance(entity.getBalance())
                .build();
    }
}
