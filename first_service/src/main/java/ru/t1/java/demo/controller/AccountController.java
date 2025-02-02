package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.kafka.consumer.KafkaAccountConsumer;
import ru.t1.java.demo.kafka.producer.KafkaAccountProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.mapper.AccountMapper;

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
    private final KafkaAccountProducer producer;
    @GetMapping
    @Metric
    public List<AccountDto> getAll() {
        return accountService.getAll().stream().map(accountMapper::toDto).toList();
    }
    @GetMapping("/{id}")
    @Metric(1)
    public AccountDto getCertain(@PathVariable("id") Long id) {
        Account account = accountService.get(id).orElseThrow(() -> new ClientException(id));
        return accountMapper.toDto(account);
    }
    @Metric
    @PostMapping
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
    /*@PatchMapping("/{id}/status")
    public void changeStatus(@PathVariable("id") Long id,
                             @RequestParam(name = "new") String status) {
        Account account = accountService.get(id).orElseThrow(() -> new ClientException(id));
        AccountStatus accountStatus = AccountStatus.valueOf(status);
        accountService.changeStatus(account, accountStatus);
    }*/

    // метод разработан для тестирования передачи данных по следующей схеме
    // controller -> producer -> consumer -> service -> database
    @Metric
    @PostMapping("/test_producer")
    public void createSendingToTopic(@RequestBody AccountDto accountDto) {
        validate(accountDto);
        producer.send(accountDto);
    }
    private void validate(AccountDto accountDto) {
        clientService.get(accountDto.getClientId())
                .orElseThrow(() -> new ClientException(accountDto.getClientId()));
        if (!(accountDto.getAccountType().equals("DEBIT") || accountDto.getAccountType().equals("CREDIT")))
            throw new ClientException(accountDto.getAccountType());
    }
}
