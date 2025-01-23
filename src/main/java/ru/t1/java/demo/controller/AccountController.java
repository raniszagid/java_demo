package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/accounts")
@LogDataSourceError
public class AccountController {
    private final ClientService clientService;
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    @GetMapping
    public List<AccountDto> getAll() {
        return accountService.getAll().stream().map(accountMapper::toDto).toList();
    }
    @GetMapping("/{id}")
    public AccountDto getCertain(@PathVariable("id") Long id) {
        Account account = accountService.get(id).orElseThrow(() -> new ClientException(id));
        return accountMapper.toDto(account);
    }
    @PostMapping("/new")
    public void create(@RequestBody AccountDto accountDto) {
        validate(accountDto);
        accountService.save(accountMapper.toEntity(accountDto));
    }
    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id,
                       @RequestBody AccountDto accountDto) {
        Account account = accountService.get(id).orElseThrow(() -> new ClientException(id));
        validate(accountDto);
        accountService.change(account, accountDto);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        Account account = accountService.get(id).orElseThrow(() -> new ClientException(id));
        accountService.delete(account);
    }
    private void validate(AccountDto accountDto) {
        clientService.get(accountDto.getClientId())
                .orElseThrow(() -> new ClientException(accountDto.getClientId()));
        if (!(accountDto.getAccountType().equals("DEBIT") || accountDto.getAccountType().equals("CREDIT")))
            throw new ClientException(accountDto.getAccountType());
    }
}
