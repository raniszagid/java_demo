package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.AccountRepository;

import java.util.List;
import java.util.Optional;

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
}
