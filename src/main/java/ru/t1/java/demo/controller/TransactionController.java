package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.mapper.TransactionMapper;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/transactions")
@LogDataSourceError
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final AccountService accountService;
    @GetMapping
    public List<TransactionDto> getAll() {
        return transactionService.getAll().stream().map(transactionMapper::toDto).toList();
    }
    @GetMapping("/{id}")
    public TransactionDto getCertain(@PathVariable("id") Long id) {
        Transaction transaction = transactionService.get(id).orElseThrow(ClientException::new);
        return transactionMapper.toDto(transaction);
    }
    @PostMapping
    public void create(@RequestBody TransactionDto transactionDto) {
        accountService.get(transactionDto.getAccountId()).orElseThrow(ClientException::new);
        transactionDto.setTransactionTime(LocalDateTime.now());
        transactionService.save(transactionMapper.toEntity(transactionDto));
    }
    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id,
                       @RequestBody TransactionDto transactionDto) {
        Transaction transaction = transactionService.get(id).orElseThrow(ClientException::new);
        Account account = accountService.get(transactionDto.getAccountId()).orElseThrow(ClientException::new);
        transactionService.change(transaction, transactionDto);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        Transaction transaction = transactionService.get(id).orElseThrow(ClientException::new);
        transactionService.delete(transaction);
    }
}
