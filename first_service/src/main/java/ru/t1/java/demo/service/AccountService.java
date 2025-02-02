package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.AccountRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    public List<Account> getAll() {
        return accountRepository.findAll();
    }
    public Optional<Account> get(Long id) {
        return accountRepository.findById(id);
    }
    public void save(Account account) {
        account.setAccountId(UUID.randomUUID());
        accountRepository.save(account);
    }
    public void change(Account old, AccountDto fresh) {
        if (fresh.getClientId() != null)
            old.setClientId(fresh.getClientId());
        if (fresh.getAccountType() != null)
            old.setAccountType(AccountType.valueOf(fresh.getAccountType()));
        if (fresh.getBalance() != null)
            old.setBalance(fresh.getBalance());
        accountRepository.save(old);
    }
    public void delete(Account account) {
        accountRepository.delete(account);
    }
    public void changeBalance(Account account, Double transactionAmount) {
        account.setBalance(account.getBalance() + transactionAmount);
        accountRepository.save(account);
    }
    public void changeStatus(Account account, AccountStatus status) {
        account.setAccountStatus(status);
        accountRepository.save(account);
    }
    public void changeBalanceByUUIDAfterRejection(UUID uuid, Double amount) {
        Account account =  accountRepository.findByAccountId(uuid).orElseThrow(ClientException::new);
        account.setBalance(account.getBalance() - amount);
    }
    public Account getAccountByUUID(UUID uuid) {
        return accountRepository.findByAccountId(uuid).orElseThrow(ClientException::new);
    }
    public void frozeAmountAndBlockAccount(Account account, Double amount) {
        account.setFrozenAmount(amount);
        account.setBalance(account.getBalance() - amount);
        account.setAccountStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
    }
}
