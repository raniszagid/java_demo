package ru.t1.java.demo.mapper;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

@Component
public class AccountMapper {
    public Account toEntity(AccountDto dto) {
        AccountStatus status = dto.getAccountStatus() != null
                ? AccountStatus.valueOf(dto.getAccountStatus())
                : AccountStatus.OPEN;
        return Account.builder()
                .clientId(dto.getClientId())
                .accountType(AccountType.valueOf(dto.getAccountType()))
                .balance(dto.getBalance())
                .accountStatus(status)
                .frozenAmount(dto.getFrozenAmount())
                .accountId(dto.getAccountId())
                .build();
    }
    public AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .accountType(entity.getAccountType().name())
                .balance(entity.getBalance())
                .accountStatus(entity.getAccountStatus().name())
                .frozenAmount(entity.getFrozenAmount())
                .accountId(entity.getAccountId())
                .build();
    }
}
